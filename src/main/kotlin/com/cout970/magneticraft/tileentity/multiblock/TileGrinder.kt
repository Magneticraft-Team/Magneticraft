package com.cout970.magneticraft.tileentity.multiblock

import coffee.cypher.mcextlib.extensions.aabb.plus
import coffee.cypher.mcextlib.extensions.aabb.to
import coffee.cypher.mcextlib.extensions.inventories.get
import coffee.cypher.mcextlib.extensions.inventories.set
import coffee.cypher.mcextlib.extensions.vectors.minus
import coffee.cypher.mcextlib.extensions.vectors.toDoubleVec
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.heat.IHeatHandler
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.api.internal.energy.HeatConnection
import com.cout970.magneticraft.api.internal.heat.HeatContainer
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
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.electric.TileElectricHeatBase
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
import java.util.*

/**
 * Created by cout970 on 19/08/2016.
 */
class TileGrinder : TileElectricHeatBase(), IMultiblockCenter {

    override var multiblock: Multiblock?
        get() = MultiblockGrinder
        set(value) {/* ignored */
        }

    override var centerPos: BlockPos?
        get() = BlockPos.ORIGIN
        set(value) {/* ignored */
        }

    override var multiblockFacing: EnumFacing? = null

    val heatNode = HeatContainer(
            emit = false,
            tile = this,
            dissipation = 0.025,
            specificHeat = IRON_HEAT_CAPACITY * 20, /*PLACEHOLDER*/
            maxHeat = ((IRON_HEAT_CAPACITY * 20) * Config.defaultMachineMaxTemp).toLong(),
            conductivity = DEFAULT_CONDUCTIVITY
    )

    override val heatNodes: List<IHeatNode> get() = listOf(heatNode)

    val hammerAnimation = AnimationTimer()
    val safeHeat: Long = ((IRON_HEAT_CAPACITY * 20 * Config.defaultMachineSafeTemp)).toLong()
    val efficiency = 0.9
    var overtemp = false
    val node = ElectricNode({ worldObj }, { pos + direction.rotatePoint(BlockPos.ORIGIN, ENERGY_INPUT) })
    override val electricNodes: List<IElectricNode> get() = listOf(node)
    val in_inv_size = 4
    val out_inv_size = 4
    val inventory = ItemStackHandler(in_inv_size + out_inv_size)
    val craftingProcess: CraftingProcess
    val production = ValueAverage()

    private var recipeCache: IGrinderRecipe? = null
    private var inputCache: ItemStack? = null

    init {
        craftingProcess = CraftingProcess({//craft
            val recipe = getRecipe()!!
            var result = recipe.primaryOutput
            var secondary = if (recipe.probability > 0 && Random().nextFloat() <= recipe.probability) recipe.secondaryOutput else null
            for (i in in_inv_size until inventory.slots) {
                result = inventory.insertItem(i, result, false)
                if (result != null) continue
                for (j in in_inv_size until inventory.slots) {
                    secondary = inventory.insertItem(i, secondary, false)
                    if (secondary == null) {
                        consume_input()
                        return@CraftingProcess
                    }
                }
            }
        }, { //can craft
            node.voltage > TIER_1_MACHINES_MIN_VOLTAGE
                    && check_output_valid()
                    && overtemp == false
        }, { // use energy
            val applied = node.applyPower(-Config.grinderConsumption * interpolate(node.voltage, TIER_1_MACHINES_MIN_VOLTAGE, TIER_1_MAX_VOLTAGE) * 6, false)
            if (heatNode.pushHeat((applied * (1 - efficiency) * ENERGY_TO_HEAT).toLong(), false) > 0) { //If there's any heat leftover after we tried to push heat, the machine has overheated
                overtemp = true
            }
            production += applied
        }, {
            getRecipe()?.duration ?: 120f
        })
    }

    override fun shouldRefresh(world: World?, pos: BlockPos?, oldState: IBlockState?, newSate: IBlockState?): Boolean {
        return oldState?.block !== newSate?.block
    }

    fun posTransform(worldPos: BlockPos): BlockPos = direction.rotatePoint(BlockPos.ORIGIN, worldPos) + pos

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
            craftingProcess.tick(worldObj, 1.0f)
            production.tick()
            if (heatNode.heat < safeHeat) overtemp = false
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
        super.save()
    }

    override fun load(nbt: NBTTagCompound) = nbt.run {
        if (hasKey("direction")) multiblockFacing = getEnumFacing("direction")
        if (hasKey("inv")) inventory.deserializeNBT(getCompoundTag("inv"))
        if (hasKey("crafting")) craftingProcess.deserializeNBT(getCompoundTag("crafting"))
        super.load(nbt)
    }

    override fun getRenderBoundingBox(): AxisAlignedBB = (pos - BlockPos(1, 2, 0)) to (pos + BlockPos(2, 4, 3))

    companion object {
        val ENERGY_INPUT = BlockPos(1, 0, 1)
        val ITEM_INPUT = BlockPos(0, 2, 1)
        val ITEM_OUTPUT = BlockPos(0, 0, 2)
        val HEAT_OUTPUT = BlockPos(-1, 0, 1)
        val POTENTIAL_CONNECTIONS = setOf(BlockPos(-2, 0, 1)) //Optimistation to stop multiblocks checking inside themselves for heat connections
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

    override fun updateHeatConnections() {
        for (j in POTENTIAL_CONNECTIONS) {
            val relPos = posTransform(j)
            val tileOther = world.getTileEntity(relPos)
            if (tileOther == null) continue
            val handler = NODE_HANDLER!!.fromTile(tileOther) ?: continue
            if (handler !is IHeatHandler) continue
            for (otherNode in handler.nodes.filter { it is IHeatNode }.map { it as IHeatNode }) {
                heatConnections.add(HeatConnection(heatNode, otherNode))
                handler.addConnection(HeatConnection(otherNode, heatNode))
            }
        }
        heatNode.setAmbientTemp(biomeTemptoKelvin(world, pos)) //This might be unnecessary
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?, relPos: BlockPos): Boolean {
        if (capability == NODE_HANDLER && (facing == null ||
                (direction.rotatePoint(BlockPos.ORIGIN, ENERGY_INPUT) == relPos && facing == direction.rotateY()) ||
                direction.rotatePoint(BlockPos.ORIGIN, HEAT_OUTPUT) == relPos && facing == direction.rotateYCCW()))
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
        if (capability == NODE_HANDLER && (facing == null ||
                (direction.rotatePoint(BlockPos.ORIGIN, ENERGY_INPUT) == relPos && facing == direction.rotateY()) ||
                direction.rotatePoint(BlockPos.ORIGIN, HEAT_OUTPUT) == relPos && facing == direction.rotateYCCW()))
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
        if (capability == NODE_HANDLER) return true
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
        if (capability == NODE_HANDLER) return this as T
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
        var output = recipe.primaryOutput
        var secondary = recipe.secondaryOutput
        for (i in in_inv_size until inventory.slots) {
            output = inventory.insertItem(i, output, true)
            if (output != null) continue
            for (j in in_inv_size until inventory.slots) {
                secondary = inventory.insertItem(j, secondary, true) ?:
                        return true
            }
        }
        return false
    }

    fun suckItems() {
        val aabb = direction.rotateBox(BlockPos.ORIGIN.toDoubleVec(), (AxisAlignedBB(-1.0, 2.0, 0.0, 2.0, 4.0, 3.0))) + pos.toDoubleVec()
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