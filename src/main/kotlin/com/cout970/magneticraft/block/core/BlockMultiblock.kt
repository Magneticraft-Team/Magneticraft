package com.cout970.magneticraft.block.core

import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.multiblock.core.IMultiblockModule
import com.cout970.magneticraft.multiblock.core.MultiblockContext
import com.cout970.magneticraft.multiblock.core.MultiblockManager
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.util.vector.*
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/07/03.
 */
class BlockMultiblock(
        factory: (World, IBlockState) -> TileEntity?,
        filter: ((IBlockState) -> Boolean)?,
        material: Material
) : BlockTileBase(factory, filter, material) {

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB,
                                       collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?,
                                       p_185477_7_: Boolean) {
        val active = state[Multiblocks.PROPERTY_MULTIBLOCK_ORIENTATION]?.active ?: true
        if (active) {
            val tile = worldIn.getTile<TileBase>(pos)
            val module = tile?.container?.modules?.find { it is IMultiblockModule } as? IMultiblockModule

            if (module != null && module.multiblock != null) {

                val relPos = -module.centerPos!!
                val global = module.multiblock!!.getGlobalCollisionBox().map {
                    val origin = EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it)
                    module.multiblockFacing!!.rotateBox(vec3Of(0.5), origin)
                }

                val thisBox = FULL_BLOCK_AABB + module.centerPos!!
                val boxes = global.mapNotNull { it.cut(thisBox)?.offset(relPos)?.offset(pos) }
                boxes.filterTo(collidingBoxes) { entityBox.intersects(it) }
                return
            }
        }
        super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn, p_185477_7_)
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun getSelectedBoundingBox(state: IBlockState, worldIn: World, pos: BlockPos): AxisAlignedBB {
        val active = state[Multiblocks.PROPERTY_MULTIBLOCK_ORIENTATION]?.active ?: true
        if (active) {
            val tile = worldIn.getTile<TileBase>(pos)
            val module = tile?.container?.modules?.find { it is IMultiblockModule } as? IMultiblockModule
            if (module != null && module.multiblock != null) {

                val relPos = -module.centerPos!!
                val global = module.multiblock!!.getGlobalCollisionBox().map {
                    val origin = EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it)
                    module.multiblockFacing!!.rotateBox(vec3Of(0.5), origin)
                }

                val thisBox = FULL_BLOCK_AABB + module.centerPos!!
                val boxes = global.mapNotNull { it.cut(thisBox) }
                val list = mutableListOf<AxisAlignedBB>()
                val player = Minecraft.getMinecraft().player

                val start = player.getPositionEyes(0f)
                val look = player.getLook(0f)
                val blockReachDistance = Minecraft.getMinecraft().playerController!!.blockReachDistance
                val end = start.addVector(
                        look.x * blockReachDistance,
                        look.y * blockReachDistance,
                        look.z * blockReachDistance
                )

                boxes.forEach { list.add(it.offset(relPos)) }
                val res = list
                        .associate { it to rayTrace(pos, start, end, it) }
                        .filter { it.value != null }
                        .map { it.key to it.value }
                        .sortedBy { it.second!!.hitVec.distanceTo(start) }
                        .firstOrNull()?.first

                return res?.offset(pos) ?: EMPTY_AABB
            }
        }
        return super.getSelectedBoundingBox(state, worldIn, pos)
    }

    @Suppress("OverridingDeprecatedMember")
    override fun collisionRayTrace(blockState: IBlockState, worldIn: World, pos: BlockPos, start: Vec3d,
                                   end: Vec3d): RayTraceResult? {
        val active = blockState[Multiblocks.PROPERTY_MULTIBLOCK_ORIENTATION]?.active ?: true
        if (active) {
            val tile = worldIn.getTile<TileBase>(pos)
            val module = tile?.container?.modules?.find { it is IMultiblockModule } as? IMultiblockModule
            if (module != null && module.multiblock != null) {

                val relPos = -module.centerPos!!
                val global = module.multiblock!!.getGlobalCollisionBox().map {
                    val origin = EnumFacing.SOUTH.rotateBox(vec3Of(0.5), it)
                    module.multiblockFacing!!.rotateBox(vec3Of(0.5), origin)
                }

                val thisBox = FULL_BLOCK_AABB + module.centerPos!!
                val boxes = global.mapNotNull { it.cut(thisBox) }
                val list = mutableListOf<AxisAlignedBB>()

                boxes.forEach { list.add(it.offset(relPos)) }
                val res = list
                        .associate { it to rayTrace(pos, start, end, it) }
                        .filter { it.value != null }
                        .map { it.key to it.value }
                        .sortedBy { it.second!!.hitVec.distanceTo(start) }
                        .firstOrNull()?.second
                return res
            }
        }
        return this.rayTrace(pos, start, end, blockState.getBoundingBox(worldIn, pos))
    }

    //removedByPlayer

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        val active = state[Multiblocks.PROPERTY_MULTIBLOCK_ORIENTATION]?.active ?: true
        return if (active) EnumBlockRenderType.INVISIBLE else super.getRenderType(state)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        if (worldIn.isServer) {
            val active = state[Multiblocks.PROPERTY_MULTIBLOCK_ORIENTATION]?.active ?: true
            if (active) {
                val tile = worldIn.getTile<TileBase>(pos)
                val module = tile?.container?.modules?.find { it is IMultiblockModule } as? IMultiblockModule
                if (module != null && module.multiblock != null) {
                    if (module.multiblock != null && module.multiblockFacing != null) {
                        MultiblockManager.deactivateMultiblockStructure(
                                MultiblockContext(
                                        multiblock = module.multiblock!!,
                                        world = worldIn,
                                        center = pos.subtract(module.centerPos!!),
                                        facing = module.multiblockFacing!!,
                                        player = null
                                )
                        )
                    }
                }
            }
        }
        super.breakBlock(worldIn, pos, state)
    }
}