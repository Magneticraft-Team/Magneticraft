package com.cout970.magneticraft.tileentity.multiblock

import coffee.cypher.mcextlib.extensions.aabb.to
import coffee.cypher.mcextlib.extensions.inventories.get
import coffee.cypher.mcextlib.extensions.inventories.set
import coffee.cypher.mcextlib.extensions.vectors.minus
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.api.internal.registries.machines.grinder.GrinderRecipeManager
import com.cout970.magneticraft.api.registries.machines.grinder.IGrinderRecipe
import com.cout970.magneticraft.block.PROPERTY_ACTIVE
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.multiblock.IMultiblockCenter
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.impl.MultiblockGrinder
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.tileentity.electric.TileElectricBase
import com.cout970.magneticraft.util.*
import com.cout970.magneticraft.util.misc.AnimationTimer
import com.cout970.magneticraft.util.misc.CraftingProcess
import com.cout970.magneticraft.util.misc.ValueAverage
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by cout970 on 19/08/2016.
 */
class TileGrinder : TileElectricBase(), IMultiblockCenter {

    override var multiblock: Multiblock?
        get() = MultiblockGrinder
        set(value) {/* ignored */
        }

    override var centerPos: BlockPos?
        get() = BlockPos.ORIGIN
        set(value) {/* ignored */
        }

    override var multiblockFacing: EnumFacing? = null

    val hammerAnimation = AnimationTimer()
    val node = ElectricNode({ worldObj }, { pos + direction.rotatePoint(BlockPos.ORIGIN, ENERGY_INPUT) })
    override val electricNodes: List<IElectricNode> get() = listOf(node)
    val in_inv_size = 4
    val out_inv_size = 4
    val inventory = ItemStackHandler(in_inv_size + out_inv_size)
    val craftingProcess: CraftingProcess
    val production = ValueAverage()

    var redPower: Int = 0

    private var recipeCache: IGrinderRecipe? = null
    private var inputCache: ItemStack? = null

    init {
        craftingProcess = CraftingProcess({//craft
            val recipe = getRecipe()!!
            var result = recipe.output
            for (i in in_inv_size until inventory.slots) {
                result = inventory.insertItem(i, result, false)
                if (result == null) {
                    consume_input()
                    break
                }
            }
        }, { //can craft
            node.voltage > TIER_1_MACHINES_MIN_VOLTAGE
                    && check_output_valid()
        }, { // use energy
            val applied = node.applyPower(-Config.grinderConsumption * it, false)
            production += applied
        }, {
            getRecipe()?.duration ?: 120f
        })
    }

    override fun shouldRefresh(world: World?, pos: BlockPos?, oldState: IBlockState?, newSate: IBlockState?): Boolean {
        return oldState?.block !== newSate?.block
    }

    fun getRecipe(input: ItemStack): IGrinderRecipe? {
        if (input === inputCache) return recipeCache
        val recipe = GrinderRecipeManager.findRecipe(input)
        if (recipe != null) {
            recipeCache = recipe
            inputCache = input
        }
        return recipe
    }

    fun getRecipe(): IGrinderRecipe? = if (inventory[0] != null) getRecipe(inventory[0]!!) else null

    override fun update() {
        if (worldObj.isServer && active) {
            if (shouldTick(20)) {
                suckItems()
                sendUpdateToNearPlayers()
            }
            craftingProcess.tick(worldObj, if (redPower == 0) 1f else (node.voltage * TIER_1_MACHINES_MIN_VOLTAGE).toFloat())
            production.tick()
        }
        super.update()
    }

    override fun onActivate() {
        getBlockState()
        sendUpdateToNearPlayers()
    }

    override fun onDeactivate() {
    }

    val direction: EnumFacing get() = if (PROPERTY_DIRECTION.isIn(getBlockState()))
        PROPERTY_DIRECTION[getBlockState()] else EnumFacing.NORTH

    val active: Boolean get() = if (PROPERTY_ACTIVE.isIn(getBlockState()))
        PROPERTY_ACTIVE[getBlockState()] else false

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        if (multiblockFacing != null) setEnumFacing("direction", multiblockFacing!!)
        setTag("inv", inventory.serializeNBT())
        setTag("crafting", craftingProcess.serializeNBT())
    }

    override fun load(nbt: NBTTagCompound) = nbt.run {
        if (hasKey("direction")) multiblockFacing = getEnumFacing("direction")
        if (hasKey("inv")) inventory.deserializeNBT(getCompoundTag("inv"))
        if (hasKey("crafting")) craftingProcess.deserializeNBT(getCompoundTag("crafting"))
    }

    override fun getRenderBoundingBox(): AxisAlignedBB = (pos - BlockPos(1, 2, 0)) to (pos + BlockPos(2, 4, 3))

    companion object {
        val ENERGY_INPUT = BlockPos(1, 0, 1)
        val ITEM_INPUT = BlockPos(0, 2, 1)
        val ITEM_OUTPUT = BlockPos(0, 0, 2)
    }

    override fun onBreak() {
        super.onBreak()
        if (worldObj.isServer) {
            for (i in 0 until inventory.slots) {
                val item = inventory[i]
                if (item != null) {
                    dropItem(item, pos)
                }
            }
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?, relPos: BlockPos): Boolean {
        if (capability == NODE_HANDLER && direction.rotatePoint(BlockPos.ORIGIN, ENERGY_INPUT) == relPos && (facing == direction.rotateY() || facing == null))
            return true
        if (capability == ITEM_HANDLER) {
            if (facing == null)
                return true
            if (direction.rotatePoint(BlockPos.ORIGIN, ITEM_INPUT) == relPos && facing == EnumFacing.UP)
                return true
            if (direction.rotatePoint(BlockPos.ORIGIN, ITEM_OUTPUT) == relPos && facing == direction.rotateY())
                return true
        }
        return false
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?, relPos: BlockPos): T? {
        if (capability == NODE_HANDLER && direction.rotatePoint(BlockPos.ORIGIN, ENERGY_INPUT) == relPos && (facing == direction.rotateY() || facing == null))
            return this as T
        if (capability == ITEM_HANDLER) {
            if (facing == null)
                return inventory as T
            if (direction.rotatePoint(BlockPos.ORIGIN, ITEM_INPUT) == relPos && facing == EnumFacing.UP)
                return In_Inventory(inventory, in_inv_size) as T
            if (direction.rotatePoint(BlockPos.ORIGIN, ITEM_OUTPUT) == relPos && facing == direction.rotateY())
                return Out_Inventory(inventory, in_inv_size, out_inv_size) as T
        }
        return null
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        if (capability == ITEM_HANDLER) return true
        if (capability == NODE_HANDLER) return false
        return super.hasCapability(capability, facing)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
        if (capability == ITEM_HANDLER) {
            if (facing == null)
                return inventory as T
            if (facing == EnumFacing.UP)
                return In_Inventory(inventory, in_inv_size) as T
            else
                return Out_Inventory(inventory, in_inv_size, out_inv_size) as T
        }
        return super.getCapability(capability, facing)
    }

    override fun shouldRenderInPass(pass: Int): Boolean {
        return if (active) super.shouldRenderInPass(pass) else pass == 1
    }

    //If the last crafting action empties the active slot,
    // take the last filled slot, and place it in the last empty slot below it in the queue.
    // Repeat until the queue is sorted
    fun consume_input() {
        val recipe = getRecipe() ?: return
        inventory[0]!!.stackSize -= recipe.input.stackSize
        if (inventory[0]!!.stackSize == 0) inventory[0] = null
        else return
        for (i in (1 until in_inv_size).reversed()) {
            if (inventory[i] == null) continue
            for (j in (0 until i).reversed()) {
                if (inventory[j] != null) continue
                inventory[j] = inventory[i]
                inventory[i] = null
            }
        }
    }

    fun check_output_valid(): Boolean {
        val recipe = getRecipe() ?: return false
        if (inventory[0]!!.stackSize < recipe.input.stackSize) return false
        var output = recipe.output
        for (i in in_inv_size until inventory.slots) {
            output = inventory.insertItem(i, output, true) ?: return true
        }
        return false
    }

    fun suckItems() {
        val aabb = AxisAlignedBB(pos.up(2).add(direction.rotatePoint(BlockPos.ORIGIN, BlockPos(-1.0, 0.0, 0.0))), pos.up(3).add(direction.rotatePoint(BlockPos.ORIGIN, BlockPos(2.0, 1.0, 3.0))))
        val items = worldObj.getEntitiesWithinAABB(EntityItem::class.java, aabb)
        for (i in items) {
            val item = i.entityItem
            for (j in 0 until in_inv_size) {
                val inserted = inventory.insertItem(j, item, false)
                if (inserted != null) {
                    i.setEntityItemStack(inserted)
                } else {
                    i.setDead()
                    break
                }
            }
        }
    }

    class In_Inventory(val inv: IItemHandler, private val in_size: Int) : IItemHandler by inv {
        override fun getSlots(): Int {
            return in_size
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack? {
            return null
        }
    }

    class Out_Inventory(val inv: IItemHandler, private val in_size: Int, private val out_size: Int) : IItemHandler by inv {

        override fun getSlots(): Int {
            return out_size
        }

        override fun getStackInSlot(slot: Int): ItemStack {
            return getStackInSlot(slot + in_size)
        }

        override fun insertItem(slot: Int, stack: ItemStack?, simulate: Boolean): ItemStack? {
            return stack
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack? {
            return inv.extractItem(slot + in_size, amount, simulate)
        }
    }
}