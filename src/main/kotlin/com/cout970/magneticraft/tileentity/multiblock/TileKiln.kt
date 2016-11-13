package com.cout970.magneticraft.tileentity.multiblock

import coffee.cypher.mcextlib.extensions.aabb.to
import coffee.cypher.mcextlib.extensions.vectors.minus
import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.api.heat.IHeatHandler
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.energy.HeatConnection
import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress.KilnRecipeManager
import com.cout970.magneticraft.api.registries.machines.kiln.IKilnRecipe
import com.cout970.magneticraft.block.PROPERTY_ACTIVE
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.multiblock.IMultiblockCenter
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.impl.MultiblockKiln
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.TileKilnShelf
import com.cout970.magneticraft.tileentity.electric.TileHeatBase
import com.cout970.magneticraft.util.*
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLiving
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by cout970 on 19/08/2016.
 */
class TileKiln : TileHeatBase(), IMultiblockCenter {

    override var multiblock: Multiblock?
        get() = MultiblockKiln
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
            dissipation = 0.0,
            specificHeat = LIMESTONE_HEAT_CAPACITY * 20, /*PLACEHOLDER*/
            maxHeat = ((LIMESTONE_HEAT_CAPACITY * 20) * LIMESTONE_MELTING_POINT).toLong(),
            conductivity = 0.05
    )

    override val heatNodes: List<IHeatNode> get() = listOf(heatNode)

    //Not using crafting process because I assumed that an array of lambdas will have horrible locality of reference
    data class craftingSlot(
            var craftingTime: Int = 0,
            var stateCache: IBlockState? = null,
            var inputCache: ItemStack? = null,
            var recipeCache: IKilnRecipe? = null,
            var stackCache: ItemStack? = null)

    object craftingSlots {
        var map: MutableMap<BlockPos, craftingSlot> = mutableMapOf()
        fun getCraftingTimesArray(): IntArray {
            var array = IntArray(map.size)
            var index = 0
            map.forEach { array.set(index, it.value.craftingTime); index++ }
            return array
        }

        fun setAllCraftingTimes(array: IntArray) {
            var index = 0
            map.forEach { it.value.craftingTime = array[index]; index++ }
        }
    }

    val updateFrequency: Int = 20
    var doorOpen: Boolean = false

    init {
        for (i in 0 until 3) {
            for (j in 0 until 2) {
                for (k in 0 until 3) {
                    craftingSlots.map.put(BlockPos(i, j, k), craftingSlot())
                }
            }
        }
    }

    override fun shouldRefresh(world: World?, pos: BlockPos?, oldState: IBlockState?, newSate: IBlockState?): Boolean {
        return oldState?.block !== newSate?.block
    }

    fun getRecipe(input: BlockPos, slot: craftingSlot): IKilnRecipe? {
        val state = world.getBlockState(input)
        var stack: ItemStack
        val tile = world.getTile<TileKilnShelf>(input)
        if (tile != null) {
            stack = tile.getStack() ?: return null
        } else {
            if (state !== slot.stateCache) { //Assuming getting state from block is potentially expensive, so two-stage caching
                stack = ItemStack(state.block.getItemDropped(state, null, 0) ?: return null, 1, state.block.damageDropped(state)) //Quantity should be irrelevant, since recipes shouldn't ask for it
                slot.stateCache = state
                slot.stackCache = stack
            } else {
                stack = slot.stackCache!!
            }
        }
        if (stack === slot.inputCache) return slot.recipeCache
        val recipe = KilnRecipeManager.findRecipe(stack)
        if (recipe != null) {
            slot.recipeCache = recipe
            slot.inputCache = stack
        }
        return recipe
    }

    fun canCraft(input: BlockPos, slot: craftingSlot): Boolean {
        val recipe = getRecipe(input, slot) ?: return false
        return !doorOpen &&
                heatNode.temperature > recipe.minTemp &&
                heatNode.temperature < recipe.maxTemp
    }

    override fun update() {
        if (worldObj.isServer && active) {
            if (shouldTick(10)) {
                if (heatNode.temperature > KILN_DAMAGE_TEMP) {
                    val entities = world.getEntitiesWithinAABB(EntityLiving::class.java, INTERNAL_AABB)
                    if (!entities.isEmpty()) {
                        entities.forEach {
                            if (!doorOpen) it.air -= 1
                            it.attackEntityFrom(DamageSource.inFire, (heatNode.temperature / KILN_DAMAGE_TEMP).toFloat())
                        }
                        if (heatNode.temperature > KILN_FIRE_TEMP)
                            entities.forEach {
                                it.setFire(5)
                            }
                        sendUpdateToNearPlayers()
                    }
                }
            }
            if (shouldTick(20)) {
                if (doorOpen) heatNode.dissipation = 0.1
                else heatNode.dissipation = 0.0
                sendUpdateToNearPlayers()
            }
            if (shouldTick(updateFrequency)) {
                craftingSlots.map.forEach {
                    if (!canCraft(it.key, it.value)) return@forEach
                    val recipe: IKilnRecipe = getRecipe(it.key, it.value) ?: return@forEach
                    if (it.value.craftingTime <= recipe.duration) {
                        it.value.craftingTime += 1
                        return@forEach
                    }
                    it.value.craftingTime = 0
                    val tile = world.getTile<TileKilnShelf>(it.key)
                    if (tile != null) {
                        if (recipe.isItemRecipe)
                            tile.setStack(recipe.itemOutput)
                    } else {
                        if (recipe.isBlockRecipe)
                            world.setBlockState(it.key, recipe.blockOutput)
                    }
                }
            }
        }
        super.update()
    }

    override fun onActivate() {
        getBlockState()
        sendUpdateToNearPlayers()
    }

    override fun onDeactivate() {
    }

    override fun updateHeatConnections() {
        for (j in POTENTIAL_CONNECTIONS) {
            val relPos = direction.rotatePoint(BlockPos.ORIGIN, j)
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

    val direction: EnumFacing get() = if (PROPERTY_DIRECTION.isIn(getBlockState()))
        PROPERTY_DIRECTION[getBlockState()] else EnumFacing.NORTH

    val active: Boolean get() = if (PROPERTY_ACTIVE.isIn(getBlockState()))
        PROPERTY_ACTIVE[getBlockState()] else false

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        if (multiblockFacing != null) setEnumFacing("direction", multiblockFacing!!)
        setIntArray("times", craftingSlots.getCraftingTimesArray())
        setBoolean("door", doorOpen)
        super.save()
    }

    override fun load(nbt: NBTTagCompound) = nbt.run {
        if (hasKey("direction")) multiblockFacing = getEnumFacing("direction")
        if (hasKey("times")) craftingSlots.setAllCraftingTimes(getIntArray("times"))
        if (hasKey("door")) doorOpen = getBoolean("door")
        super.load(nbt)
    }

    override fun getRenderBoundingBox(): AxisAlignedBB = (pos - BlockPos(2, 0, 0)) to (pos + BlockPos(3, 3, 5))

    companion object {
        val HEAT_INPUT = BlockPos(-1, 1, 0)
        val POTENTIAL_CONNECTIONS = setOf(
                BlockPos(-1, -1, 0),
                /******************/
                BlockPos(1, -1, 0),
                BlockPos(-2, -1, 1), BlockPos(-2, -1, 2), BlockPos(-2, -1, 3),
                BlockPos(2, -1, 1), BlockPos(2, -1, 2), BlockPos(2, -1, 3),
                BlockPos(-1, -1, 4), BlockPos(0, -1, 4), BlockPos(1, -1, 4)) //Optimistation to stop multiblocks checking inside themselves for heat connections
        val INTERNAL_AABB = AxisAlignedBB(BlockPos(3, 2, 2))
    }

    override fun onBreak() {
        super.onBreak()
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?, relPos: BlockPos): Boolean {
        if (capability == NODE_HANDLER) {
            if (direction.rotatePoint(BlockPos.ORIGIN, HEAT_INPUT) == relPos)
                return true
        }
        return false
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?, relPos: BlockPos): T? {
        if (capability == NODE_HANDLER)
            if (direction.rotatePoint(BlockPos.ORIGIN, HEAT_INPUT) == relPos)
                return this as T
        return null
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        if (capability == NODE_HANDLER) return true
        return super.hasCapability(capability, facing)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
        if (capability == NODE_HANDLER) return this as T
        return super.getCapability(capability, facing)
    }

    override fun shouldRenderInPass(pass: Int): Boolean {
        return if (active) super.shouldRenderInPass(pass) else pass == 1
    }
}