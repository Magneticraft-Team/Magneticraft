package com.cout970.magneticraft.tileentity.multiblock

import coffee.cypher.mcextlib.extensions.aabb.to
import coffee.cypher.mcextlib.extensions.vectors.minus
import com.cout970.magneticraft.api.heat.IHeatHandler
import com.cout970.magneticraft.api.heat.IHeatNode
import com.cout970.magneticraft.api.internal.energy.HeatConnection
import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress.KilnRecipeManager
import com.cout970.magneticraft.api.registries.machines.kiln.IKilnRecipe
import com.cout970.magneticraft.block.PROPERTY_ACTIVE
import com.cout970.magneticraft.block.PROPERTY_DIRECTION
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.multiblock.IMultiblockCenter
import com.cout970.magneticraft.multiblock.Multiblock
import com.cout970.magneticraft.multiblock.impl.MultiblockKiln
import com.cout970.magneticraft.registry.NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.electric.TileHeatBase
import com.cout970.magneticraft.util.*
import com.cout970.magneticraft.util.misc.CraftingProcess
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
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
            dissipation = 0.025,
            specificHeat = IRON_HEAT_CAPACITY * 10, /*PLACEHOLDER*/
            maxHeat = ((IRON_HEAT_CAPACITY * 10) * Config.defaultMachineMaxTemp).toLong(),
            conductivity = 0.05
    )

    override val heatNodes: List<IHeatNode> get() = listOf(heatNode)

    val craftingProcess: CraftingProcess

    private var recipeCache: IKilnRecipe? = null
    private var inputCache: ItemStack? = null

    init {
        craftingProcess = CraftingProcess({//craft
            val recipe = getRecipe()!!
        }, { //can craft
            false
        }, { // use energy
        }, {
            getRecipe()?.duration ?: 120f
        })
    }

    override fun shouldRefresh(world: World?, pos: BlockPos?, oldState: IBlockState?, newSate: IBlockState?): Boolean {
        return oldState?.block !== newSate?.block
    }

    fun getRecipe(input: ItemStack): IKilnRecipe? {
        if (input === inputCache) return recipeCache
        val recipe = KilnRecipeManager.findRecipe(input)
        if (recipe != null) {
            recipeCache = recipe
            inputCache = input
        }
        return recipe
    }

    fun getRecipe(): IKilnRecipe? = null

    override fun update() {
        if (worldObj.isServer && active) {
            if (shouldTick(20)) sendUpdateToNearPlayers()
            craftingProcess.tick(worldObj, 1f)
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
        setTag("crafting", craftingProcess.serializeNBT())
        super.save()
    }

    override fun load(nbt: NBTTagCompound) = nbt.run {
        if (hasKey("direction")) multiblockFacing = getEnumFacing("direction")
        if (hasKey("crafting")) craftingProcess.deserializeNBT(getCompoundTag("crafting"))
        super.load(nbt)
    }

    override fun getRenderBoundingBox(): AxisAlignedBB = (pos - BlockPos(2, 0, 0)) to (pos + BlockPos(3, 3, 5))

    companion object {
        val HEAT_INPUT = BlockPos(-1, 1, 0)
        val POTENTIAL_CONNECTIONS = setOf(BlockPos(-2, 1, 0), BlockPos(-1, 1, 1), BlockPos(-1, 1, -1)) //Optimistation to stop multiblocks checking inside themselves for heat connections
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