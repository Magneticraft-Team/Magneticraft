@file:Suppress("DEPRECATION")

package com.cout970.magneticraft.systems.blocks

import com.cout970.magneticraft.*
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilerenderers.d
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.client.renderer.model.ModelResourceLocation
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.fluid.IFluidState
import net.minecraft.item.BlockItemUseContext
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUseContext
import net.minecraft.state.StateContainer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.shapes.VoxelShape
import net.minecraft.util.math.shapes.VoxelShapes
import net.minecraft.util.text.ITextComponent
import net.minecraft.world.IBlockReader
import net.minecraft.world.IWorldReader
import net.minecraft.world.World
import net.minecraft.world.storage.loot.LootContext
import net.minecraft.world.storage.loot.LootParameters
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import java.util.*

@Suppress("OverridingDeprecatedMember")
open class BlockBase(props: Block.Properties) : Block(props), ICapabilityProvider {

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
    var blockLayer_ = BlockRenderLayer.SOLID
    var tickRate_ = 10
    var dropWithTileNBT = false
    var creativeTab: ItemGroup = ItemGroup.MISC

    // Methods
    var aabb: ((BoundingBoxArgs) -> List<AABB>)? = null
    var onActivated: ((OnActivatedArgs) -> Boolean)? = null
    var stateMapper: ((IBlockState) -> ModelResourceLocation)? = null
    var onBlockPlaced: ((OnBlockPlacedArgs) -> IBlockState)? = null
    var onBlockPostPlaced: ((OnBlockPostPlacedArgs) -> Unit)? = null
    var onBlockBreak: ((BreakBlockArgs) -> Unit)? = null
    var pickBlock: ((PickBlockArgs) -> ItemStack)? = null
    var capabilityProvider: ICapabilityProvider? = null
    var onNeighborChanged: ((OnNeighborChangedArgs) -> Unit)? = null
    var blockStatesToPlace: ((BlockStatesToPlaceArgs) -> List<Pair<BlockPos, IBlockState>>)? = null
    var onDrop: ((DropsArgs) -> List<ItemStack>)? = null
    var onUpdateTick: ((OnUpdateTickArgs) -> Unit)? = null
    var collisionBox: ((CollisionBoxArgs) -> AABB?)? = null
    var onEntityCollidedWithBlock: ((OnEntityCollidedWithBlockArgs) -> Unit)? = null
    var canConnectRedstone: ((CanConnectRedstoneArgs) -> Boolean)? = null
    var redstonePower: ((RedstonePowerArgs) -> Int)? = null
    var addInformation: ((AddInformationArgs) -> Unit)? = null

    // ItemBlock stuff
    val inventoryVariants: Map<Int, String> = run {
        val map = mutableMapOf<Int, String>()
        states.filter { it.isVisible }.forEach { value ->
            map += value.ordinal to value.stateName
        }
        map
    }

    override fun fillStateContainer(builder: StateContainer.Builder<Block, BlockState>) {
        states_!![0].properties.forEach { builder.add(it) }
    }

    // event stuff
//    override fun getBoundingBox(state: IBlockState, source: IBlockReader, pos: BlockPos): AABB {
//        return aabb?.invoke(BoundingBoxArgs(state, source, pos))?.reduce { acc, aabb ->
//            acc.union(aabb)
//        } ?: FULL_BLOCK_AABB
//    }
//
//    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
//    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB,
//                                       collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?,
//                                       p_185477_7_: Boolean) {
//        aabb?.let {
//            val list = it.invoke(BoundingBoxArgs(state, worldIn, pos))
//            val boxes = list.mapNotNull { it.cut(FULL_BLOCK_AABB)?.offset(pos) }
//            boxes.filterTo(collidingBoxes) { entityBox.intersects(it) }
//            return
//        }
//        super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, p_185477_7_)
//    }
//
//    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
//    override fun getSelectedBoundingBox(state: IBlockState, worldIn: World, pos: BlockPos): AxisAlignedBB {
//        aabb?.let { aabb ->
//            val rel = aabb.invoke(BoundingBoxArgs(state, worldIn, pos))
//
//            val player = Minecraft.getMinecraft().player
//            val start = player.getPositionEyes(0f)
//            val look = player.getLook(0f)
//            val blockReachDistance = Minecraft.getMinecraft().playerController!!.blockReachDistance
//            val end = start.addVector(
//                look.x * blockReachDistance,
//                look.y * blockReachDistance,
//                look.z * blockReachDistance
//            )
//
//            val res = rel
//                .mapNotNull { it.cut(FULL_BLOCK_AABB) }
//                .associate { it to rayTrace(pos, start, end, it) }
//                .filter { it.value != null }
//                .map { it.key to it.value }
//                .minBy { it.second!!.hitVec.distanceTo(start) }
//                ?.first
//
//            return res?.offset(pos) ?: EMPTY_AABB
//        }
//        return super.getSelectedBoundingBox(state, worldIn, pos)
//    }
//
//    @Suppress("OverridingDeprecatedMember")
//    override fun collisionRayTrace(blockState: IBlockState, worldIn: World, pos: BlockPos, start: Vec3d,
//                                   end: Vec3d): RayTraceResult? {
//
//        aabb?.let { aabb ->
//            val rel = aabb.invoke(BoundingBoxArgs(blockState, worldIn, pos))
//            return rel
//                .mapNotNull { it.cut(FULL_BLOCK_AABB) }
//                .associate { it to rayTrace(pos, start, end, it) }
//                .filter { it.value != null }
//                .map { it.key to it.value }
//                .minBy { it.second!!.hitVec.distanceTo(start) }
//                ?.second
//        }
//        return this.rayTrace(pos, start, end, blockState.getBoundingBox(worldIn, pos))
//    }

    override fun onBlockActivated(state: BlockState, worldIn: World, pos: BlockPos, player: PlayerEntity, handIn: Hand, hit: BlockRayTraceResult): Boolean {
        onActivated?.let {
            return it.invoke(OnActivatedArgs(worldIn, pos, state, player, handIn, player.getHeldItem(handIn), hit.face, hit.hitVec))
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit)
    }

    override fun getPickBlock(state: BlockState, target: RayTraceResult, world: IBlockReader, pos: BlockPos, player: PlayerEntity): ItemStack {
        val default = super.getPickBlock(state, target, world, pos, player)
        return pickBlock?.invoke(PickBlockArgs(state, target, world, pos, player, default)) ?: default
    }

    override fun removedByPlayer(state: IBlockState, world: World, pos: BlockPos, player: EntityPlayer,
                                 willHarvest: Boolean, fluid: IFluidState): Boolean {
        if (world.isClient) {
            world.getTile<TileBase>(pos)?.onBreak()
        }
        // Remove TileEntity that is kept longer to have it at getDrops
        if (dropWithTileNBT && (player.isCreative || world.isClient)) {
            world.removeTileEntity(pos)
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid)
    }


    // Only called in the server
    override fun onReplaced(state: IBlockState, worldIn: World, pos: BlockPos, newState: IBlockState, moving: Boolean) {
        onBlockBreak?.invoke(BreakBlockArgs(worldIn, pos, state, newState, moving))
        worldIn.getTile<TileBase>(pos)?.onBreak()
        if (!dropWithTileNBT) {
            worldIn.removeTileEntity(pos)
            super.onReplaced(state, worldIn, pos, newState, moving)
        }
    }

    override fun toString(): String = "BlockBase($registryName)"

    override fun getStateForPlacement(ctx: BlockItemUseContext): BlockState? {
        onBlockPlaced?.let {
            return it.invoke(OnBlockPlacedArgs(
                ctx.world,
                ctx.pos,
                ctx.face,
                ctx.hitVec,
                0,
                ctx.player,
                ctx.hand,
                defaultState
            ))
        }
        return super.getStateForPlacement(ctx)
    }

    // TODO
//    override fun canPlaceBlockOnSide(worldIn: World, pos: BlockPos, side: EnumFacing): Boolean {
//        val default = super.canPlaceBlockOnSide(worldIn, pos, side)
//        return canPlaceBlockOnSide?.invoke(CanPlaceBlockOnSideArgs(worldIn, pos, side, default)) ?: default
//    }

    override fun onNeighborChange(state: BlockState, world: IWorldReader, pos: BlockPos, neighbor: BlockPos) {
        onNeighborChanged?.invoke(OnNeighborChangedArgs(state, world, pos, state.block, neighbor))
        super.onNeighborChange(state, world, pos, neighbor)
    }

    override fun propagatesSkylightDown(state: BlockState, reader: IBlockReader, pos: BlockPos): Boolean {
        return translucent_
    }

    override fun causesSuffocation(state: BlockState, worldIn: IBlockReader, pos: BlockPos): Boolean {
        return !translucent_
    }

    override fun isNormalCube(state: BlockState, worldIn: IBlockReader, pos: BlockPos): Boolean {
        return enableOcclusionOptimization
    }

    override fun canEntitySpawn(state: BlockState, worldIn: IBlockReader, pos: BlockPos, type: EntityType<*>): Boolean {
        return super.canEntitySpawn(state, worldIn, pos, type) && !translucent_
    }

    override fun getRenderShape(state: BlockState, worldIn: IBlockReader, pos: BlockPos): VoxelShape {
        return if (enableOcclusionOptimization) super.getRenderShape(state, worldIn, pos) else VoxelShapes.empty()
    }

    // TODO
//    override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? =
//        capabilityProvider?.getCapability(capability, facing)

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        TODO()
    }

    fun getBlockStatesToPlace(worldIn: World, pos: BlockPos,
                              facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float,
                              player: EntityPlayer, hand: Hand): List<Pair<BlockPos, IBlockState>> {

        val default = getStateForPlacement(BlockItemUseContext(
            ItemUseContext(player, hand, BlockRayTraceResult(Vec3d(hitX.d, hitY.d, hitZ.d), facing, pos, true))
        ))!!

        blockStatesToPlace?.let {
            return it.invoke(BlockStatesToPlaceArgs(worldIn, pos, facing, vec3Of(hitX, hitY, hitZ), 0, player, hand, default))
        }

        return listOf(BlockPos.ZERO to default)
    }

    override fun harvestBlock(worldIn: World, player: EntityPlayer, pos: BlockPos, state: IBlockState, te: TileEntity?, stack: ItemStack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack)
        if (dropWithTileNBT) {
            // Make sure that after breaking the block there is no TileEntity remaining
            worldIn.removeTileEntity(pos)
        }
    }

    override fun getDrops(state: BlockState, builder: LootContext.Builder): MutableList<ItemStack> {
        val default = NonNullList.create<ItemStack>()
        val pos = builder.get(LootParameters.POSITION) ?: BlockPos.ZERO
        onDrop?.let {
            default += super.getDrops(state, builder)

            return it.invoke(DropsArgs(builder.world, pos, state, 0, default)).toMutableList()
        }

        // Save TileNBT
        if (dropWithTileNBT) {
            if (default.isNotEmpty()) {
                val stack = default[0]
                val tile = builder.world.getTileEntity(pos)
                val nbt = tile?.serializeNBT()

                if (nbt != null) {
                    stack.setTagInfo("BlockEntityTag", nbt)
                }
            }
        }
        return default
    }

    override fun getRenderLayer(): BlockRenderLayer {
        return blockLayer_
    }

    override fun tick(state: BlockState, worldIn: World, pos: BlockPos, random: Random) {
        onUpdateTick?.invoke(OnUpdateTickArgs(worldIn, pos, state, random))
    }

    // TODO
//    override fun getCollisionBoundingBox(blockState: IBlockState, worldIn: IBlockReader,
//                                         pos: BlockPos): AxisAlignedBB? {
//
//        val default = super.getCollisionBoundingBox(blockState, worldIn, pos)
//        collisionBox?.let {
//            return it(CollisionBoxArgs(blockState, worldIn, pos, default))
//        }
//        return default
//    }
//
//    override fun shouldSideBeRendered(state: IBlockState, blockAccess: IBlockReader, pos: BlockPos,
//                                      side: EnumFacing): Boolean {
//
//        return shouldSideBeRendered_?.invoke(ShouldSideBeRendererArgs(state, blockAccess, pos, side))
//            ?: super.shouldSideBeRendered(state, blockAccess, pos, side)
//    }

    override fun tickRate(worldIn: IWorldReader): Int {
        return tickRate_
    }

    override fun onEntityCollision(state: BlockState, worldIn: World, pos: BlockPos, entityIn: Entity) {
        onEntityCollidedWithBlock?.invoke(OnEntityCollidedWithBlockArgs(worldIn, pos, state, entityIn))
    }

    override fun canConnectRedstone(state: IBlockState, world: IBlockReader, pos: BlockPos, side: EnumFacing?): Boolean {
        return canConnectRedstone?.invoke(CanConnectRedstoneArgs(state, world, pos, side))
            ?: super.canConnectRedstone(state, world, pos, side)
    }

    override fun getStrongPower(blockState: IBlockState, blockAccess: IBlockReader, pos: BlockPos, side: EnumFacing): Int {
        return redstonePower?.invoke(RedstonePowerArgs(blockState, blockAccess, pos, side))
            ?: super.getStrongPower(blockState, blockAccess, pos, side)
    }

    override fun getWeakPower(blockState: IBlockState, blockAccess: IBlockReader, pos: BlockPos, side: EnumFacing): Int {
        return redstonePower?.invoke(RedstonePowerArgs(blockState, blockAccess, pos, side))
            ?: super.getWeakPower(blockState, blockAccess, pos, side)
    }

    override fun addInformation(stack: ItemStack, worldIn: IBlockReader?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        addInformation?.invoke(AddInformationArgs(stack, worldIn, tooltip, flagIn))
    }
}

data class BoundingBoxArgs(val state: IBlockState, val source: IBlockReader, val pos: BlockPos)

data class OnActivatedArgs(val worldIn: World, val pos: BlockPos, val state: IBlockState, val playerIn: EntityPlayer,
                           val hand: Hand, val heldItem: ItemStack, val side: EnumFacing, val hit: IVector3){
    val raytraceResult: BlockRayTraceResult get() = BlockRayTraceResult(hit, side, pos, true)
}

data class OnBlockPlacedArgs(val world: World, val pos: BlockPos, val facing: EnumFacing,
                             val hit: IVector3, val itemMetadata: Int,
                             val placer: PlayerEntity?, val hand: Hand, val defaultValue: IBlockState)

data class PickBlockArgs(val state: IBlockState, val target: RayTraceResult, val world: IBlockReader, val pos: BlockPos,
                         val player: EntityPlayer, val default: ItemStack)

data class CanPlaceBlockOnSideArgs(val worldIn: World, val pos: BlockPos, val side: EnumFacing, val default: Boolean)

data class OnNeighborChangedArgs(val state: IBlockState, val worldIn: IWorldReader, val pos: BlockPos, val blockIn: Block,
                                 val fromPos: BlockPos)

data class BlockStatesToPlaceArgs(val worldIn: World, val pos: BlockPos, val facing: EnumFacing, val hit: IVector3,
                                  val meta: Int, val player: EntityPlayer, val hand: Hand, val default: IBlockState)

data class BreakBlockArgs(val worldIn: World, val pos: BlockPos, val state: IBlockState, val newState: IBlockState,
                          val moving: Boolean)

data class DropsArgs(val world: IBlockReader, val pos: BlockPos, val state: IBlockState, val fortune: Int,
                     val default: List<ItemStack>)

data class OnBlockPostPlacedArgs(val world: World, val pos: BlockPos, val facing: EnumFacing,
                                 val hit: IVector3, val placer: EntityPlayer?, val hand: Hand,
                                 val relPos: BlockPos)

data class OnUpdateTickArgs(val world: World, val pos: BlockPos, val state: IBlockState, val rand: Random)

data class CollisionBoxArgs(val state: IBlockState, val world: IBlockReader, val pos: BlockPos, val default: AABB?)

data class ShouldSideBeRendererArgs(val state: IBlockState, val blockAccess: IBlockReader, val pos: BlockPos,
                                    val side: EnumFacing)

data class OnEntityCollidedWithBlockArgs(val worldIn: World, val pos: BlockPos, val state: IBlockState,
                                         val entityIn: Entity)

data class CanConnectRedstoneArgs(val state: IBlockState, val world: IBlockReader, val pos: BlockPos,
                                  val side: EnumFacing?)

data class RedstonePowerArgs(val state: IBlockState, val world: IBlockReader, val pos: BlockPos,
                             val side: EnumFacing)

data class GetBlockFaceShapeArgs(val worldIn: IBlockReader, val state: IBlockState, val pos: BlockPos,
                                 val face: EnumFacing)

data class AddInformationArgs(val stack: ItemStack, val world: IBlockReader?, val tooltip: MutableList<ITextComponent>,
                              val advanced: ITooltipFlag)