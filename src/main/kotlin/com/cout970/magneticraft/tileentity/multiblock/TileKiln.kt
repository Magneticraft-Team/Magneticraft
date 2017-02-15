package com.cout970.magneticraft.tileentity.multiblock

import coffee.cypher.mcextlib.extensions.aabb.plus
import coffee.cypher.mcextlib.extensions.aabb.to
import coffee.cypher.mcextlib.extensions.vectors.toDoubleVec
import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.api.heat.IHeatHandler
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.heat.HeatConnection
import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.api.internal.registries.machines.kiln.KilnRecipeManager
import com.cout970.magneticraft.api.registries.machines.kiln.IKilnRecipe
import com.cout970.magneticraft.block.PROPERTY_ACTIVE
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.multiblock.IMultiblockCenter
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.impl.MultiblockKiln
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.TileKilnShelf
import com.cout970.magneticraft.tileentity.heat.TileHeatBase
import com.cout970.magneticraft.util.*
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLiving
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumParticleTypes
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
            worldGetter = { this.world },
            posGetter = { this.getPos() },
            dissipation = 0.0,
            specificHeat = LIMESTONE_HEAT_CAPACITY * 20, /*PLACEHOLDER*/
            maxHeat = ((LIMESTONE_HEAT_CAPACITY * 20) * LIMESTONE_MELTING_POINT).toLong(),
            conductivity = DEFAULT_CONDUCTIVITY
    )

    override val heatNodes: List<IHeatNode> get() = listOf(heatNode)

    //Not using crafting process because I assumed that an array of lambdas will have horrible locality of reference
    data class CraftingSlot(
            var craftingTime: Int = 0,
            var isCrafting: Boolean = false,
            var stateCache: IBlockState? = null,
            var inputCache: ItemStack? = null,
            var recipeCache: IKilnRecipe? = null,
            var stackCache: ItemStack? = null)

    object CraftingSlots {
        var map: MutableMap<BlockPos, CraftingSlot> = mutableMapOf()
        fun getCraftingTimesArray(): IntArray {
            val array = IntArray(map.size)
            var index = 0
            map.forEach { array.set(index, it.value.craftingTime); index++ }
            return array
        }

        fun getIsCraftingArray(): ByteArray {
            val array = ByteArray(map.size)
            var index = 0
            map.forEach { array.set(index, if (it.value.isCrafting) 1 else 0); index++ }
            return array
        }

        fun setAllCraftingTimes(array: IntArray) {
            var index = 0
            map.forEach { it.value.craftingTime = array[index]; index++ }
        }

        fun setAllIsCrafting(array: ByteArray) {
            var index = 0
            map.forEach { it.value.isCrafting = array[index] != 0.toByte(); index++ }
        }
    }

    val updateFrequency: Int = 20
    var doorOpen: Boolean = false

    init {
        for (i in 0 until 3) {
            for (j in 0 until 2) {
                for (k in 0 until 3) {
                    CraftingSlots.map.put(BlockPos(i, j, k) + BlockPos(-1, 0, 1), CraftingSlot())
                }
            }
        }
    }

    override fun shouldRefresh(world: World?, pos: BlockPos?, oldState: IBlockState?, newSate: IBlockState?): Boolean {
        return oldState?.block !== newSate?.block
    }

    fun getRecipe(input: BlockPos, slot: CraftingSlot): IKilnRecipe? {
        val state = world.getBlockState(input)
        val stack: ItemStack
        val tile = world.getTile<TileKilnShelf>(input)
        if (tile != null) {
            stack = tile.getStack() ?: return null
        } else {
            if (state !== slot.stateCache) { //Assuming getting state from block is potentially expensive, so two-stage caching
                stack = ItemStack(Item.getItemFromBlock(state.block) ?: return null, 1, state.block.damageDropped(state)) //Quantity should be irrelevant, since recipes shouldn't ask for it
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

    fun canCraft(input: BlockPos, slot: CraftingSlot): Boolean {
        val recipe = getRecipe(input, slot) ?: return false
        return !doorOpen &&
                heatNode.temperature > recipe.minTemp &&
                heatNode.temperature < recipe.maxTemp
    }

    override fun update() {
        if (worldObj.isServer) {
            if (active) {
                if (shouldTick(10)) {
                    if (heatNode.temperature > KILN_DAMAGE_TEMP) {
                        val entities = world.getEntitiesWithinAABB(EntityLiving::class.java, direction.rotateBox(BlockPos.ORIGIN.toDoubleVec(), INTERNAL_AABB) + pos.toDoubleVec())
                        if (!entities.isEmpty()) {
                            entities.forEach {
                                if (!doorOpen) it.air -= 1
                                val damage = (heatNode.temperature / KILN_DAMAGE_TEMP).toFloat()
                                if (it.health < damage) it.setFire(1)
                                it.attackEntityFrom(DamageSources.damageSourceKiln, damage)
                            }
                            if (heatNode.temperature > KILN_FIRE_TEMP && doorOpen)
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
                    CraftingSlots.map.forEach {
                        if (!canCraft(posTransform(it.key), it.value)) {
                            it.value.isCrafting = false
                            return@forEach
                        }
                        val recipe: IKilnRecipe = getRecipe(posTransform(it.key), it.value) ?: return@forEach
                        if (it.value.craftingTime <= recipe.duration) {
                            it.value.craftingTime += 1
                            it.value.isCrafting = true
                            return@forEach
                        }
                        it.value.craftingTime = 0
                        it.value.isCrafting = false
                        val tile = world.getTile<TileKilnShelf>(posTransform(it.key))
                        if (tile != null) {
                            if (recipe.isItemRecipe)
                                tile.setStack(recipe.itemOutput)
                        } else {
                            if (recipe.isBlockRecipe)
                                world.setBlockState(posTransform(it.key), recipe.blockOutput)
                        }
                    }
                }
            }
        } else {
            if (shouldTick(5)) {
                CraftingSlots.map.forEach {
                    if (!it.value.isCrafting) return@forEach
                    val partPos = posTransform(it.key)
                    val d3 = (partPos.x.toFloat() + world.rand.nextFloat()).toDouble()
                    val d4 = (partPos.y.toFloat() + world.rand.nextFloat()).toDouble()
                    val d5 = (partPos.z.toFloat() + world.rand.nextFloat()).toDouble()
                    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d3, d4, d5, 0.0, 0.0, 0.0, *IntArray(0))
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
        heatNode.setAmbientTemp(guessAmbientTemp(world, pos)) //This might be unnecessary
    }

    val direction: EnumFacing get() = if (PROPERTY_DIRECTION.isIn(getBlockState()))
        PROPERTY_DIRECTION[getBlockState()] else EnumFacing.NORTH

    fun posTransform(worldPos: BlockPos): BlockPos = direction.rotatePoint(BlockPos.ORIGIN, worldPos) + pos

    val active: Boolean get() = if (PROPERTY_ACTIVE.isIn(getBlockState()))
        PROPERTY_ACTIVE[getBlockState()] else false

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        if (multiblockFacing != null) setEnumFacing("direction", multiblockFacing!!)
        setIntArray("times", CraftingSlots.getCraftingTimesArray())
        setByteArray("isCrafting", CraftingSlots.getIsCraftingArray())
        setBoolean("door", doorOpen)
        super.save()
    }

    override fun load(nbt: NBTTagCompound) = nbt.run {
        if (hasKey("direction")) multiblockFacing = getEnumFacing("direction")
        if (hasKey("times")) CraftingSlots.setAllCraftingTimes(getIntArray("times"))
        if (hasKey("isCrafting")) CraftingSlots.setAllIsCrafting(getByteArray("isCrafting"))
        if (hasKey("door")) doorOpen = getBoolean("door")
        super.load(nbt)
    }

    override fun getRenderBoundingBox(): AxisAlignedBB = (BlockPos.ORIGIN to direction.rotatePoint(BlockPos.ORIGIN,
            multiblock!!.size)).offset(direction.rotatePoint(BlockPos.ORIGIN, -multiblock!!.center)).offset(pos)


    companion object {
        val HEAT_INPUTS = setOf(BlockPos(-2, 0, 1), BlockPos(-2, 0, 2), BlockPos(-2, 0, 3),
                BlockPos(-1, 0, 4), BlockPos(0, 0, 4), BlockPos(1, 0, 4),
                BlockPos(2, 0, 1), BlockPos(2, 0, 2), BlockPos(2, 0, 3))
        val POTENTIAL_CONNECTIONS = setOf(
                BlockPos(-2, -1, 1), BlockPos(-2, -1, 2), BlockPos(-2, -1, 3),
                BlockPos(2, -1, 1), BlockPos(2, -1, 2), BlockPos(2, -1, 3),
                BlockPos(-1, -1, 4), BlockPos(0, -1, 4), BlockPos(1, -1, 4)) //Optimistation to stop multiblocks checking inside themselves for heat connections
        val INTERNAL_AABB = AxisAlignedBB(-0.25, 0.0, -0.25, 3.25, 2.5, 3.25)
    }

    override fun onBreak() {
        super.onBreak()
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?, relPos: BlockPos): Boolean {
        if (capability == NODE_HANDLER && facing == EnumFacing.DOWN) {
            val transPos = direction.rotatePoint(BlockPos.ORIGIN, relPos)
            HEAT_INPUTS.forEach {
                if (it == transPos)
                    return true
            }
        }
        return false
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?, relPos: BlockPos): T? {
        if (capability == NODE_HANDLER && facing == EnumFacing.DOWN) {
            val transPos = direction.rotatePoint(BlockPos.ORIGIN, relPos)
            HEAT_INPUTS.forEach {
                if (it == transPos) return this as T
            }
        }
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