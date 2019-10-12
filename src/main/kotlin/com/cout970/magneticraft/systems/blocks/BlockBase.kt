@file:Suppress("DEPRECATION")

package com.cout970.magneticraft.systems.blocks

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.vector.EMPTY_AABB
import com.cout970.magneticraft.misc.vector.cut
import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.tileentities.TileBase
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.IStateMapper
import net.minecraft.client.renderer.block.statemap.StateMapperBase
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

@Suppress("OverridingDeprecatedMember")
open class BlockBase(material: Material) : Block(material), ICapabilityProvider {

    companion object {
        // Because Mojang is stupid and createBlockState is called BEFORE the constructor
        var states_: List<IStatesEnum>? = null
    }

    val states: List<IStatesEnum> = states_!!
    var forceModelBake = false
    var customModels: List<Pair<String, ResourceLocation>> = emptyList()
    var enableOcclusionOptimization = true
    var translucent_ = false
    var generateDefaultItemModel = true
    var alwaysDropDefault = false
    var blockLayer_ = BlockRenderLayer.SOLID
    var tickRate_ = 10
    var dropWithTileNBT = false

    // Methods
    var aabb: ((BoundingBoxArgs) -> List<AABB>)? = null
    var onActivated: ((OnActivatedArgs) -> Boolean)? = null
    var stateMapper: ((IBlockState) -> ModelResourceLocation)? = null
    var onBlockPlaced: ((OnBlockPlacedArgs) -> IBlockState)? = null
    var onBlockPostPlaced: ((OnBlockPostPlacedArgs) -> Unit)? = null
    var onBlockBreak: ((BreakBlockArgs) -> Unit)? = null
    var pickBlock: ((PickBlockArgs) -> ItemStack)? = null
    var canPlaceBlockOnSide: ((CanPlaceBlockOnSideArgs) -> Boolean)? = null
    var capabilityProvider: ICapabilityProvider? = null
    var onNeighborChanged: ((OnNeighborChangedArgs) -> Unit)? = null
    var blockStatesToPlace: ((BlockStatesToPlaceArgs) -> List<Pair<BlockPos, IBlockState>>)? = null
    var onDrop: ((DropsArgs) -> List<ItemStack>)? = null
    var onUpdateTick: ((OnUpdateTickArgs) -> Unit)? = null
    var collisionBox: ((CollisionBoxArgs) -> AABB?)? = null
    var shouldSideBeRendered_: ((ShouldSideBeRendererArgs) -> Boolean)? = null
    var onEntityCollidedWithBlock: ((OnEntityCollidedWithBlockArgs) -> Unit)? = null
    var canConnectRedstone: ((CanConnectRedstoneArgs) -> Boolean)? = null
    var redstonePower: ((RedstonePowerArgs) -> Int)? = null
    var getBlockFaceShape: ((GetBlockFaceShapeArgs) -> BlockFaceShape)? = null
    var addInformation: ((AddInformationArgs) -> Unit)? = null

    // ItemBlock stuff
    val inventoryVariants: Map<Int, String> = run {
        val map = mutableMapOf<Int, String>()
        states.filter { it.isVisible }.forEach { value ->
            map += value.ordinal to value.stateName
        }
        map
    }

    fun getItemName(stack: ItemStack) = "${unlocalizedName}_${states.getOrNull(stack.metadata)?.stateName}"

    // metadata and block state stuff
    override fun getMetaFromState(state: IBlockState): Int = states.find {
        it.getBlockState(this) == state
    }?.ordinal ?: 0

    override fun getStateFromMeta(meta: Int): IBlockState = states[meta].getBlockState(this)

    override fun createBlockState(): BlockStateContainer =
            BlockStateContainer(this, *states_!![0].properties.toTypedArray())

    // event stuff
    override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AABB {
        return aabb?.invoke(BoundingBoxArgs(state, source, pos))?.reduce { acc, aabb ->
            acc.union(aabb)
        } ?: FULL_BLOCK_AABB
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB,
                                       collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?,
                                       p_185477_7_: Boolean) {
        aabb?.let {
            val list = it.invoke(BoundingBoxArgs(state, worldIn, pos))
            val boxes = list.mapNotNull { it.cut(FULL_BLOCK_AABB)?.offset(pos) }
            boxes.filterTo(collidingBoxes) { entityBox.intersects(it) }
            return
        }
        super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, p_185477_7_)
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun getSelectedBoundingBox(state: IBlockState, worldIn: World, pos: BlockPos): AxisAlignedBB {
        aabb?.let { aabb ->
            val rel = aabb.invoke(BoundingBoxArgs(state, worldIn, pos))

            val player = Minecraft.getMinecraft().player
            val start = player.getPositionEyes(0f)
            val look = player.getLook(0f)
            val blockReachDistance = Minecraft.getMinecraft().playerController!!.blockReachDistance
            val end = start.addVector(
                    look.x * blockReachDistance,
                    look.y * blockReachDistance,
                    look.z * blockReachDistance
            )

            val res = rel
                    .mapNotNull { it.cut(FULL_BLOCK_AABB) }
                    .associate { it to rayTrace(pos, start, end, it) }
                    .filter { it.value != null }
                    .map { it.key to it.value }
                    .minBy { it.second!!.hitVec.distanceTo(start) }
                    ?.first

            return res?.offset(pos) ?: EMPTY_AABB
        }
        return super.getSelectedBoundingBox(state, worldIn, pos)
    }

    @Suppress("OverridingDeprecatedMember")
    override fun collisionRayTrace(blockState: IBlockState, worldIn: World, pos: BlockPos, start: Vec3d,
                                   end: Vec3d): RayTraceResult? {

        aabb?.let { aabb ->
            val rel = aabb.invoke(BoundingBoxArgs(blockState, worldIn, pos))
            return rel
                    .mapNotNull { it.cut(FULL_BLOCK_AABB) }
                    .associate { it to rayTrace(pos, start, end, it) }
                    .filter { it.value != null }
                    .map { it.key to it.value }
                    .minBy { it.second!!.hitVec.distanceTo(start) }
                    ?.second
        }
        return this.rayTrace(pos, start, end, blockState.getBoundingBox(worldIn, pos))
    }

    override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer,
                                  hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float,
                                  hitZ: Float): Boolean {
        val heldItem = playerIn.getHeldItem(hand)

        return onActivated?.invoke(
                OnActivatedArgs(worldIn, pos, state, playerIn, hand, heldItem, side,
                        vec3Of(hitX, hitY, hitZ)))
                ?: super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ)
    }

    override fun damageDropped(state: IBlockState): Int {
        return if (alwaysDropDefault) 0 else getMetaFromState(state)
    }

    override fun getPickBlock(state: IBlockState, target: RayTraceResult, world: World, pos: BlockPos,
                              player: EntityPlayer): ItemStack {
        val default = super.getPickBlock(state, target, world, pos, player)
        return pickBlock?.invoke(PickBlockArgs(state, target, world, pos, player, default)) ?: default
    }

    // Called in server and client
    override fun removedByPlayer(state: IBlockState, world: World, pos: BlockPos, player: EntityPlayer,
                                 willHarvest: Boolean): Boolean {
        if (world.isClient) {
            world.getTile<TileBase>(pos)?.onBreak()
        }
        // Remove TileEntity that is kept longer to have it at getDrops
        if (dropWithTileNBT && (player.capabilities.isCreativeMode || world.isClient)) {
            world.removeTileEntity(pos)
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest)
    }

    // Only called in the server
    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        onBlockBreak?.invoke(BreakBlockArgs(worldIn, pos, state))
        worldIn.getTile<TileBase>(pos)?.onBreak()
        if (!dropWithTileNBT) {
            worldIn.removeTileEntity(pos)
            super.breakBlock(worldIn, pos, state)
        }
    }

    override fun toString(): String = "BlockBase($registryName)"

    @SideOnly(Side.CLIENT)
    fun getCustomStateMapper(): IStateMapper? = object : StateMapperBase() {
        override fun getModelResourceLocation(state: IBlockState): ModelResourceLocation {
            stateMapper?.let { return it.invoke(state) }
            val variant = states.find { it.getBlockState(this@BlockBase) == state }?.stateName ?: "normal"
            return ModelResourceLocation(registryName!!, variant)
        }
    }

    override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing,
                                      hitX: Float, hitY: Float, hitZ: Float, meta: Int,
                                      placer: EntityLivingBase, hand: EnumHand): IBlockState {

        val state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand)
        onBlockPlaced?.let {
            return it.invoke(
                    OnBlockPlacedArgs(world, pos, facing, vec3Of(hitX, hitY, hitZ), meta, placer, hand, defaultState)
            )
        }
        return state
    }

    override fun canPlaceBlockOnSide(worldIn: World, pos: BlockPos, side: EnumFacing): Boolean {
        val default = super.canPlaceBlockOnSide(worldIn, pos, side)
        return canPlaceBlockOnSide?.invoke(CanPlaceBlockOnSideArgs(worldIn, pos, side, default)) ?: default
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block,
                                 fromPos: BlockPos) {
        onNeighborChanged?.invoke(OnNeighborChangedArgs(state, worldIn, pos, blockIn, fromPos))
    }

    override fun isFullBlock(state: IBlockState?): Boolean = !translucent_
    override fun isOpaqueCube(state: IBlockState?) = enableOcclusionOptimization
    override fun isFullCube(state: IBlockState?) = !translucent_

    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? =
            capabilityProvider?.getCapability(capability, facing)

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean =
            capabilityProvider?.hasCapability(capability, facing) ?: false

    fun getBlockStatesToPlace(worldIn: World, pos: BlockPos,
                              facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float,
                              meta: Int, player: EntityPlayer, hand: EnumHand): List<Pair<BlockPos, IBlockState>> {

        val default = getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, player, hand)
        return blockStatesToPlace?.invoke(
                BlockStatesToPlaceArgs(worldIn, pos, facing, vec3Of(hitX, hitY, hitZ), meta, player, hand, default)
        ) ?: listOf(BlockPos.ORIGIN to default)
    }

    override fun harvestBlock(worldIn: World, player: EntityPlayer, pos: BlockPos, state: IBlockState, te: TileEntity?, stack: ItemStack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack)
        if (dropWithTileNBT) {
            // Make sure that after breaking the block there is no TileEntity remaining
            worldIn.removeTileEntity(pos)
        }
    }

    override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess,
                          pos: BlockPos, state: IBlockState, fortune: Int) {

        onDrop?.let {
            val default = NonNullList.create<ItemStack>()
            super.getDrops(default, world, pos, state, fortune)

            val list = it.invoke(DropsArgs(world, pos, state, fortune, default))
            drops.addAll(list)
            return
        }

        super.getDrops(drops, world, pos, state, fortune)
        // Save TileNBT
        if (dropWithTileNBT) {
            if (drops.isNotEmpty()) {
                val stack = drops[0]
                val tile = world.getTileEntity(pos)
                val nbt = tile?.serializeNBT()

                if (nbt != null) {
                    stack.setTagInfo("BlockEntityTag", nbt)
                }
            }
        }
    }

    override fun getBlockLayer(): BlockRenderLayer {
        return blockLayer_
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        onUpdateTick?.invoke(OnUpdateTickArgs(worldIn, pos, state, rand))
    }

    override fun getCollisionBoundingBox(blockState: IBlockState, worldIn: IBlockAccess,
                                         pos: BlockPos): AxisAlignedBB? {

        val default = super.getCollisionBoundingBox(blockState, worldIn, pos)
        collisionBox?.let {
            return it(CollisionBoxArgs(blockState, worldIn, pos, default))
        }
        return default
    }

    override fun shouldSideBeRendered(state: IBlockState, blockAccess: IBlockAccess, pos: BlockPos,
                                      side: EnumFacing): Boolean {

        return shouldSideBeRendered_?.invoke(ShouldSideBeRendererArgs(state, blockAccess, pos, side))
                ?: super.shouldSideBeRendered(state, blockAccess, pos, side)
    }

    override fun tickRate(worldIn: World?): Int {
        return tickRate_
    }

    override fun onEntityCollidedWithBlock(worldIn: World, pos: BlockPos, state: IBlockState, entityIn: Entity) {
        onEntityCollidedWithBlock?.invoke(OnEntityCollidedWithBlockArgs(worldIn, pos, state, entityIn))
    }

    override fun canConnectRedstone(state: IBlockState, world: IBlockAccess, pos: BlockPos, side: EnumFacing?): Boolean {
        return canConnectRedstone?.invoke(CanConnectRedstoneArgs(state, world, pos, side))
                ?: super.canConnectRedstone(state, world, pos, side)
    }

    override fun getStrongPower(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Int {
        return redstonePower?.invoke(RedstonePowerArgs(blockState, blockAccess, pos, side))
                ?: super.getStrongPower(blockState, blockAccess, pos, side)
    }

    override fun getWeakPower(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Int {
        return redstonePower?.invoke(RedstonePowerArgs(blockState, blockAccess, pos, side))
                ?: super.getWeakPower(blockState, blockAccess, pos, side)
    }

    override fun getBlockFaceShape(worldIn: IBlockAccess, state: IBlockState, pos: BlockPos, face: EnumFacing): BlockFaceShape {
        return getBlockFaceShape?.invoke(GetBlockFaceShapeArgs(worldIn, state, pos, face)) ?: BlockFaceShape.SOLID
    }

    override fun addInformation(stack: ItemStack, player: World?, tooltip: MutableList<String>, advanced: ITooltipFlag) {
        addInformation?.invoke(AddInformationArgs(stack, player, tooltip, advanced))
    }
}

data class BoundingBoxArgs(val state: IBlockState, val source: IBlockAccess, val pos: BlockPos)

data class OnActivatedArgs(val worldIn: World, val pos: BlockPos, val state: IBlockState, val playerIn: EntityPlayer,
                           val hand: EnumHand, val heldItem: ItemStack, val side: EnumFacing, val hit: IVector3)

data class OnBlockPlacedArgs(val world: World, val pos: BlockPos, val facing: EnumFacing,
                             val hit: IVector3, val itemMetadata: Int,
                             val placer: EntityLivingBase?, val hand: EnumHand, val defaultValue: IBlockState)

data class PickBlockArgs(val state: IBlockState, val target: RayTraceResult, val world: World, val pos: BlockPos,
                         val player: EntityPlayer, val default: ItemStack)

data class CanPlaceBlockOnSideArgs(val worldIn: World, val pos: BlockPos, val side: EnumFacing, val default: Boolean)

data class OnNeighborChangedArgs(val state: IBlockState, val worldIn: World, val pos: BlockPos, val blockIn: Block,
                                 val fromPos: BlockPos)

data class BlockStatesToPlaceArgs(val worldIn: World, val pos: BlockPos, val facing: EnumFacing, val hit: IVector3,
                                  val meta: Int, val player: EntityPlayer, val hand: EnumHand, val default: IBlockState)

data class BreakBlockArgs(val worldIn: World, val pos: BlockPos, val state: IBlockState)

data class DropsArgs(val world: IBlockAccess, val pos: BlockPos, val state: IBlockState, val fortune: Int,
                     val default: List<ItemStack>)

data class OnBlockPostPlacedArgs(val world: World, val pos: BlockPos, val facing: EnumFacing,
                                 val hit: IVector3, val placer: EntityLivingBase?, val hand: EnumHand,
                                 val relPos: BlockPos)

data class OnUpdateTickArgs(val world: World, val pos: BlockPos, val state: IBlockState, val rand: Random)

data class CollisionBoxArgs(val state: IBlockState, val world: IBlockAccess, val pos: BlockPos, val default: AABB?)

data class ShouldSideBeRendererArgs(val state: IBlockState, val blockAccess: IBlockAccess, val pos: BlockPos,
                                    val side: EnumFacing)

data class OnEntityCollidedWithBlockArgs(val worldIn: World, val pos: BlockPos, val state: IBlockState,
                                         val entityIn: Entity)

data class CanConnectRedstoneArgs(val state: IBlockState, val world: IBlockAccess, val pos: BlockPos,
                                  val side: EnumFacing?)

data class RedstonePowerArgs(val state: IBlockState, val world: IBlockAccess, val pos: BlockPos,
                             val side: EnumFacing)

data class GetBlockFaceShapeArgs(val worldIn: IBlockAccess, val state: IBlockState, val pos: BlockPos,
                                 val face: EnumFacing)

data class AddInformationArgs(val stack: ItemStack, val world: World?, val tooltip: MutableList<String>,
                              val advanced: ITooltipFlag)