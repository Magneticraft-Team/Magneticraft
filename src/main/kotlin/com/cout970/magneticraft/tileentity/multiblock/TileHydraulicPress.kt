package com.cout970.magneticraft.tileentity.multiblock

import coffee.cypher.mcextlib.extensions.aabb.to
import coffee.cypher.mcextlib.extensions.inventories.get
import coffee.cypher.mcextlib.extensions.inventories.set
import coffee.cypher.mcextlib.extensions.vectors.minus
import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress.HydraulicPressRecipeManager
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.IHydraulicPressRecipe
import com.cout970.magneticraft.block.PROPERTY_ACTIVE
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.multiblock.IMultiblockCenter
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.impl.MultiblockHydraulicPress
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.tileentity.electric.TileElectricHeatBase
import com.cout970.magneticraft.util.*
import com.cout970.magneticraft.util.misc.AnimationTimer
import com.cout970.magneticraft.util.misc.CraftingProcess
import net.minecraft.block.state.IBlockState
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
class TileHydraulicPress : TileElectricHeatBase(), IMultiblockCenter {

    override var multiblock: Multiblock?
        get() = MultiblockHydraulicPress
        set(value) {/* ignored */
        }

    override var centerPos: BlockPos?
        get() = BlockPos.ORIGIN
        set(value) {/* ignored */
        }

    override var multiblockFacing: EnumFacing? = null

    val hammerAnimation = AnimationTimer()
    val heatNode = HeatContainer(
            emit = false,
            tile = this,
            dissipation = 0.025,
            specificHeat = IRON_HEAT_CAPACITY * 10, /*PLACEHOLDER*/
            maxHeat = ((IRON_HEAT_CAPACITY * 10) * Config.defaultMachineMaxTemp).toLong(),
            conductivity = 0.05
    )
    override val heatNodes: List<IHeatNode> get() = listOf(heatNode)

    val safeHeat: Long = ((IRON_HEAT_CAPACITY * 10 * Config.defaultMachineSafeTemp)).toLong()
    val efficiency = 0.9
    var overtemp = false
    val node = ElectricNode({ worldObj }, { pos + direction.rotatePoint(BlockPos.ORIGIN, ENERGY_INPUT) })
    override val electricNodes: List<IElectricNode> get() = listOf(node)
    val inventory = ItemStackHandler(1)
    val craftingProcess: CraftingProcess

    private var recipeCache: IHydraulicPressRecipe? = null
    private var inputCache: ItemStack? = null

    init {
        craftingProcess = CraftingProcess({//craft
            val recipe = getRecipe()!!
            inventory[0] = null
            inventory[0] = recipe.output
        }, { //can craft
            node.voltage > TIER_1_MACHINES_MIN_VOLTAGE
                    && getRecipe() != null
                    && inventory[0]!!.stackSize == getRecipe()!!.input.stackSize
                    && overtemp == false

        }, { // use energy
            val applied = Config.hydraulicPressConsumption * interpolate(node.voltage, 60.0, 70.0)
            node.applyPower(-applied, false)
            if (heatNode.pushHeat((applied * (1 - efficiency) * ENERGY_TO_HEAT).toLong(), false) > 0) { //If there's any heat leftover after we tried to push heat, the machine has overheated
                overtemp = true
            }
        }, {
            getRecipe()?.duration ?: 120f
        })
    }

    override fun shouldRefresh(world: World?, pos: BlockPos?, oldState: IBlockState?, newSate: IBlockState?): Boolean {
        return oldState?.block !== newSate?.block
    }

    fun getRecipe(input: ItemStack): IHydraulicPressRecipe? {
        if (input === inputCache) return recipeCache
        val recipe = HydraulicPressRecipeManager.findRecipe(input)
        if (recipe != null) {
            recipeCache = recipe
            inputCache = input
        }
        return recipe
    }

    fun getRecipe(): IHydraulicPressRecipe? = if (inventory[0] != null) getRecipe(inventory[0]!!) else null

    override fun update() {
        if (worldObj.isServer && active) {
            if (shouldTick(20)) sendUpdateToNearPlayers()
            craftingProcess.tick(worldObj, 1f)
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

    override fun getRenderBoundingBox(): AxisAlignedBB = (pos - BlockPos(1, 0, 1)) to (pos + BlockPos(2, 5, 2))

    companion object {
        val ENERGY_INPUT = BlockPos(1, 1, 0)
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
        return false
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?, relPos: BlockPos): T? {
        if (capability == NODE_HANDLER && direction.rotatePoint(BlockPos.ORIGIN, ENERGY_INPUT) == relPos && (facing == direction.rotateY()) || facing == null)
            return this as T
        return null
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        if (capability == ITEM_HANDLER) return true
        if (capability == NODE_HANDLER) return false
        return super.hasCapability(capability, facing)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
        if (capability == ITEM_HANDLER) return Inventory(inventory) as T
        return super.getCapability(capability, facing)
    }

    override fun shouldRenderInPass(pass: Int): Boolean {
        return if (active) super.shouldRenderInPass(pass) else pass == 1
    }

    class Inventory(val inv: IItemHandler) : IItemHandler by inv {

        override fun insertItem(slot: Int, stack: ItemStack?, simulate: Boolean): ItemStack? {
            if (stack == null) return null

            var canAccept = false
            if (inv[0] == null) {
                canAccept = true
            } else {
                val manager = MagneticraftApi.getHydraulicPressRecipeManager()
                val recipe = manager.findRecipe(inv[0])
                if (recipe != null && recipe.input.stackSize > inv[0]!!.stackSize) {
                    canAccept = true
                }
            }

            if (canAccept) {
                val manager = MagneticraftApi.getHydraulicPressRecipeManager()
                val recipe = manager.findRecipe(stack) ?: return stack

                if (inv[0] == null) {
                    if (stack.stackSize == recipe.input.stackSize) {
                        return inv.insertItem(slot, stack, simulate)
                    } else if (stack.stackSize > recipe.input.stackSize) {
                        val copy = stack.copy()
                        val newStack = copy.splitStack(recipe.input.stackSize)
                        inv.insertItem(slot, newStack, simulate)
                        return copy
                    } else {
                        return inv.insertItem(slot, stack, simulate)
                    }
                } else {
                    val currentRecipe = manager.findRecipe(inv[0])
                    if (recipe != currentRecipe) return stack

                    if (stack.stackSize + inv[0]!!.stackSize == recipe.input.stackSize) {
                        return inv.insertItem(slot, stack, simulate)
                    } else if (stack.stackSize + inv[0]!!.stackSize > recipe.input.stackSize) {
                        val copy = stack.copy()
                        val newStack = copy.splitStack(recipe.input.stackSize - inv[0]!!.stackSize)
                        val ret = inv.insertItem(slot, newStack, simulate)
                        if (ret == null) {
                            return copy
                        } else {
                            return stack
                        }
                    } else {
                        return inv.insertItem(slot, stack, simulate)
                    }
                }
            }
            return stack
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack? {
            val input = inv[0] ?: return null
            val manager = MagneticraftApi.getHydraulicPressRecipeManager()

            if (manager.findRecipe(input) == null) {
                return inv.extractItem(slot, amount, simulate)
            }
            return null
        }
    }
}