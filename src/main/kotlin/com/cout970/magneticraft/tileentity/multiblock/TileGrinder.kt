package com.cout970.magneticraft.tileentity.multiblock

import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.heat.IHeatNodeHandler
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.api.internal.heat.HeatConnection
import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.api.internal.registries.machines.grinder.GrinderRecipeManager
import com.cout970.magneticraft.api.registries.machines.grinder.IGrinderRecipe
import com.cout970.magneticraft.block.PROPERTY_ACTIVE
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.block.isIn
import com.cout970.magneticraft.misc.crafting.CraftingProcess
import com.cout970.magneticraft.misc.damage.DamageSources
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.inventory.ItemInputHelper
import com.cout970.magneticraft.misc.inventory.ItemOutputHelper
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.set
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.misc.tileentity.TraitElectricity
import com.cout970.magneticraft.misc.tileentity.TraitHeat
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.multiblock.IMultiblockCenter
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.impl.MultiblockGrinder
import com.cout970.magneticraft.registry.HEAT_NODE_HANDLER
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.*
import com.cout970.magneticraft.util.vector.*
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLiving
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
@TileRegister("grinder")
class TileGrinder : TileBase(), IMultiblockCenter {

    override var multiblock: Multiblock? get() = MultiblockGrinder
        set(value) {/* ignored */}

    override var centerPos: BlockPos? get() = BlockPos.ORIGIN
        set(value) {/* ignored */}

    val heatNode = HeatContainer(
            worldGetter = { this.world },
            posGetter = { this.getPos() },
            dissipation = 0.025,
            specificHeat = IRON_HEAT_CAPACITY * 20, /*PLACEHOLDER*/
            maxHeat = (IRON_HEAT_CAPACITY * 20.0) * Config.defaultMachineMaxTemp,
            conductivity = DEFAULT_CONDUCTIVITY
    )

    val traitHeat: TraitHeat = TraitHeat(this, listOf(heatNode), this::updateHeatConnections)

    val node = ElectricNode({ worldObj }, { pos + direction.rotatePoint(BlockPos.ORIGIN, ENERGY_INPUT) })

    val traitElectricity = TraitElectricity(this, listOf(node))

    override val traits: List<ITileTrait> = listOf(traitHeat, traitElectricity)

    override var multiblockFacing: EnumFacing? = null

    val safeHeat: Long = ((IRON_HEAT_CAPACITY * 20 * Config.defaultMachineSafeTemp)).toLong()
    val efficiency = 0.9
    var overTemp = false
    val grinderDamage = 8f
    val in_inv_size = 4
    val inventory = ItemStackHandler(in_inv_size)
    val craftingProcess: CraftingProcess
    val production = ValueAverage()

    private var recipeCache: IGrinderRecipe? = null
    private var inputCache: ItemStack? = null

    init {
        craftingProcess = CraftingProcess({
            //craft
            val outputHelper = ItemOutputHelper(world, posTransform(ITEM_OUTPUT),
                    direction.rotatePoint(BlockPos(0, 0, 0), ITEM_OUTPUT_OFF).toVec3d())
            val recipe = getRecipe()!!
            var result = recipe.primaryOutput
            val secondary = if (recipe.probability > 0 && Random().nextFloat() <= recipe.probability) recipe.secondaryOutput else null
            result = outputHelper.ejectItems(result, false)
            if (secondary != null) outputHelper.ejectItems(secondary,
                    false) //This will lose secondaries if there's no space for them after adding the result
            if (result == null) {
                consumeInput()
                return@CraftingProcess
            }
        }, {
            //can craft
            node.voltage > ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE
            && checkOutputValid()
            && overTemp == false
        }, {
            // use energy
            val interp = interpolate(node.voltage, ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE,
                    ElectricConstants.TIER_1_MAX_VOLTAGE)
            val applied = node.applyPower(-Config.grinderConsumption * interp * 6, false)
            if (heatNode.applyHeat(applied * (1 - efficiency) * ENERGY_TO_HEAT,
                    false) > 0) { //If there's any heat leftover after we tried to push heat, the machine has overheated
                overTemp = true
            }
            production += applied
        }, {
            getRecipe()?.duration ?: 120f
        })
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newState: IBlockState): Boolean {
        return oldState.block !== newState.block
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
            val inputHelper = ItemInputHelper(world, direction.rotateBox(BlockPos.ORIGIN.toVec3d(),
                    (AxisAlignedBB(-1.0, 2.0, 0.0, 2.0, 4.0, 3.0))) + pos.toVec3d(), inventory)
            if (shouldTick(20)) {
                inputHelper.suckItems()
                sendUpdateToNearPlayers()
            }
            if (shouldTick(10)) {
                if (node.voltage > ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE) {
                    val entities = world.getEntitiesWithinAABB(EntityLiving::class.java,
                            direction.rotateBox(BlockPos.ORIGIN.toVec3d(), INTERNAL_AABB) + pos.toVec3d())
                    if (!entities.isEmpty()) {
                        val interp = interpolate(node.voltage, ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE,
                                ElectricConstants.TIER_1_MAX_VOLTAGE)
                        entities.forEach {
                            it.attackEntityFrom(DamageSources.damageSourceGrinder, (grinderDamage * interp).toFloat())
                            craftingProcess.useEnergy
                        }
                        sendUpdateToNearPlayers()
                    }
                }
            }
            craftingProcess.tick(worldObj, 1.0f)
            production.tick()
            if (heatNode.heat < safeHeat) overTemp = false
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
        getBlockState()[PROPERTY_DIRECTION] else EnumFacing.NORTH

    val active: Boolean get() = if (PROPERTY_ACTIVE.isIn(getBlockState()))
        getBlockState()[PROPERTY_ACTIVE] else false

    override fun save(): NBTTagCompound {
        val nbt = newNbt {
            if (multiblockFacing != null){
                add("direction", multiblockFacing!!)
            }
            add("inv", inventory.serializeNBT())
            add("crafting", craftingProcess.serializeNBT())
        }
        return super.save().also { it.merge(nbt) }
    }

    override fun load(nbt: NBTTagCompound) = nbt.run {
        if (hasKey("direction")) multiblockFacing = getEnumFacing("direction")
        if (hasKey("inv")) inventory.deserializeNBT(getCompoundTag("inv"))
        if (hasKey("crafting")) craftingProcess.deserializeNBT(getCompoundTag("crafting"))
        super.load(nbt)
    }

    override fun getRenderBoundingBox(): AxisAlignedBB = (pos - BlockPos(1, 2, 0)) toAABBWith (pos + BlockPos(2, 4, 3))

    companion object {
        val ENERGY_INPUT = BlockPos(0, 1, 1)
        val ITEM_INPUT = BlockPos(0, 3, 1)
        val ITEM_OUTPUT = BlockPos(0, 1, 2)
        val ITEM_OUTPUT_OFF = BlockPos(0, 0, 1)
        val HEAT_OUTPUT = BlockPos(0, 1, 1)
        val POTENTIAL_CONNECTIONS = setOf(
                BlockPos(-1, 1, 1)) //Optimistation to stop multiblocks checking inside themselves for heat connections
        val INTERNAL_AABB = AxisAlignedBB(-1.0, 2.0, 0.0, 2.0, 3.5, 3.0)
    }

    override fun onBreak() {
        super.onBreak()
        if (worldObj.isServer) {
            (0 until inventory.slots)
                    .mapNotNull { inventory[it] }
                    .forEach { dropItem(it, pos) }
        }
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?, relPos: BlockPos): Boolean {

        if(capability == HEAT_NODE_HANDLER){
            if(facing == null ||
               direction.rotatePoint(BlockPos.ORIGIN, ENERGY_INPUT) == relPos && facing == direction.rotateY() ||
               direction.rotatePoint(BlockPos.ORIGIN, HEAT_OUTPUT) == relPos && facing == direction.rotateYCCW()){
                return true
            }
        }

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
        if(capability == HEAT_NODE_HANDLER){
            if(facing == null ||
               direction.rotatePoint(BlockPos.ORIGIN, ENERGY_INPUT) == relPos && facing == direction.rotateY() ||
               direction.rotatePoint(BlockPos.ORIGIN, HEAT_OUTPUT) == relPos && facing == direction.rotateYCCW()){
                return traitHeat as T
            }
        }
        if (capability == ITEM_HANDLER) {
            if (facing == null)
                return inventory as T
            if (direction.rotatePoint(BlockPos.ORIGIN, ITEM_INPUT) == relPos && facing == EnumFacing.UP)
                return InInventory(inventory, in_inv_size) as T
            if (direction.rotatePoint(BlockPos.ORIGIN, ITEM_INPUT) == relPos && facing == EnumFacing.UP)
                return OutInventory() as T
        }
        return null
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability == ITEM_HANDLER) return true
        if (capability == HEAT_NODE_HANDLER) return true
        return super.hasCapability(capability, facing)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == ITEM_HANDLER) {
            if (facing == null)
                return inventory as T
            if (facing == EnumFacing.UP)
                return InInventory(inventory, in_inv_size) as T
        }
        if (capability == HEAT_NODE_HANDLER) return traitHeat as T
        return super.getCapability(capability, facing)
    }

    override fun shouldRenderInPass(pass: Int): Boolean {
        return if (active) super.shouldRenderInPass(pass) else pass == 1
    }

    //If the last crafting action empties the active slot,
    // take the last filled slot, and place it in the last empty slot below it in the queue.
    // Repeat until the queue is sorted
    fun consumeInput() {
        val recipe = getRecipe() ?: return
        inventory[0]!!.stackSize -= recipe.input.stackSize
        if (inventory[0]!!.stackSize == 0) inventory.setStackInSlot(0, null)
        else return
        for (i in (1 until in_inv_size).reversed()) {
            if (inventory[i] == null) continue
            for (j in (0 until i).reversed()) {
                if (inventory[j] != null) continue
                inventory[j] = inventory[i]
                inventory.setStackInSlot(i, null)
            }
        }
    }

    fun checkOutputValid(): Boolean {
        val outputHelper = ItemOutputHelper(world, posTransform(ITEM_OUTPUT),
                direction.rotatePoint(BlockPos(0, 0, 0), ITEM_OUTPUT_OFF).toVec3d())
        val recipe = getRecipe() ?: return false
        if (inventory[0]!!.stackSize < recipe.input.stackSize) return false
        if (outputHelper.ejectItems(recipe.primaryOutput, true) != null) return false
        if (outputHelper.ejectItems(recipe.secondaryOutput, true) != null) return false
        //Technically means that secondary output will be wasted if the output inventory only has room for the primary under certain circumstances
        return true
    }

    fun updateHeatConnections(traitHeat: TraitHeat) {
        traitHeat.apply {
            for (j in POTENTIAL_CONNECTIONS) {
                val relPos = posTransform(j)
                val tileOther = world.getTileEntity(relPos) ?: continue
                val handler = (HEAT_NODE_HANDLER!!.fromTile(tileOther) ?: continue) as? IHeatNodeHandler ?: continue
                for (otherNode in handler.nodes.filter { it is IHeatNode }.map { it as IHeatNode }) {
                    connections.add(HeatConnection(heatNode, otherNode))
                    handler.addConnection(HeatConnection(otherNode, heatNode))
                }
            }
        }
    }

    //TODO add an utility class for this
    class InInventory(val inv: IItemHandler, private val in_size: Int) : IItemHandler by inv {
        override fun getSlots(): Int {
            return in_size
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack? {
            return null
        }
    }

    class OutInventory : IItemHandler {
        override fun getSlots(): Int {
            return 0
        }

        override fun getStackInSlot(slot: Int): ItemStack? {
            return null
        }

        override fun insertItem(slot: Int, stack: ItemStack?, simulate: Boolean): ItemStack? {
            return stack
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack? {
            return null
        }
    }
}