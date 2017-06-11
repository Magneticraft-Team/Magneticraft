package com.cout970.magneticraft.block.multiblock


import com.cout970.magneticraft.block.BlockMultiState
import com.cout970.magneticraft.misc.player.sendMessage
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.multiblock.ITileMultiblock
import com.cout970.magneticraft.multiblock.MultiblockContext
import com.cout970.magneticraft.multiblock.MultiblockManager
import com.cout970.magneticraft.util.vector.*
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World

/**
 * Created by cout970 on 21/08/2016.
 */
abstract class BlockMultiblock(material: Material, name: String) : BlockMultiState(material, name) {

    init {
        lightOpacity = 0
    }

    @Suppress("OverridingDeprecatedMember", "DEPRECATION")
    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, entityBox: AxisAlignedBB?, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?) {
        if (entityBox != null) {
            val tile = worldIn.getTileEntity(pos)
            if (tile is ITileMultiblock && tile.multiblock != null && tile.multiblockFacing != null) {

                val relPos = -tile.centerPos!!
                val global = tile.multiblock!!.getGlobalCollisionBox().map { tile.multiblockFacing!!.rotateBox(vec3Of(0.5, 0.5, 0.5), it) }

                val thisBox = FULL_BLOCK_AABB + tile.centerPos!!
                val boxes = global.mapNotNull { it.cut(thisBox)?.offset(relPos)?.offset(pos) }
                for (i in boxes) {
                    if (entityBox.intersectsWith(i)) {
                        collidingBoxes.add(i)
                    }
                }
                return
            }
        }
        return super.addCollisionBoxToList(state, worldIn, pos, entityBox, collidingBoxes, entityIn)
    }

    override fun getSelectedBoundingBox(state: IBlockState, worldIn: World, pos: BlockPos): AxisAlignedBB {
        val tile = worldIn.getTileEntity(pos)
        if (tile is ITileMultiblock && tile.multiblock != null && tile.multiblockFacing != null) {

            val relPos = -tile.centerPos!!
            val global = tile.multiblock!!.getGlobalCollisionBox().map { tile.multiblockFacing!!.rotateBox(vec3Of(0.5, 0.5, 0.5), it) }

            val thisBox = FULL_BLOCK_AABB + tile.centerPos!!
            val boxes = global.mapNotNull { it.cut(thisBox) }
            val list = mutableListOf<AxisAlignedBB>()
            val player = Minecraft.getMinecraft().thePlayer

            val start = player.getPositionEyes(0f)
            val look = player.getLook(0f)
            val blockReachDistance = Minecraft.getMinecraft().playerController!!.blockReachDistance
            val end = start.addVector(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance)

            boxes.forEach { list.add(it.offset(relPos)) }
            val res = list
                    .associate { it to rayTrace(pos, start, end, it) }
                    .filter { it.value != null }
                    .map { it.key to it.value }
                    .sortedBy { it.second!!.hitVec.distanceTo(start) }
                    .firstOrNull()?.first
            return res?.offset(pos) ?: EMPTY_AABB
        }
        return super.getSelectedBoundingBox(state, worldIn, pos)
    }

    override fun collisionRayTrace(blockState: IBlockState, worldIn: World, pos: BlockPos, start: Vec3d, end: Vec3d): RayTraceResult? {
        val tile = worldIn.getTileEntity(pos)
        if (tile is ITileMultiblock && tile.multiblock != null && tile.multiblockFacing != null) {

            val relPos = -tile.centerPos!!
            val global = tile.multiblock!!.getGlobalCollisionBox().map { tile.multiblockFacing!!.rotateBox(vec3Of(0.5, 0.5, 0.5), it) }

            val thisBox = FULL_BLOCK_AABB + tile.centerPos!!
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
        return this.rayTrace(pos, start, end, blockState.getBoundingBox(worldIn, pos))
    }

    fun activateMultiblock(context: MultiblockContext) {
        val errors = MultiblockManager.checkMultiblockStructure(context)
        val playerIn = context.player!!
        if (errors.isNotEmpty()) {
            playerIn.sendMessage("text.magneticraft.multiblock.error_count", errors.size, color = TextFormatting.AQUA)
            if (errors.size > 2) {
                playerIn.sendMessage("text.magneticraft.multiblock.first_errors", 2, color = TextFormatting.AQUA)
                var count = 0
                errors.forEach {
                    if (count >= 2) return@forEach
                    playerIn.addChatComponentMessage(it.apply { style.color = TextFormatting.DARK_RED })
                    count++
                }
            } else {
                playerIn.sendMessage("text.magneticraft.multiblock.all_errors", color = TextFormatting.DARK_AQUA)
                errors.forEach {
                    playerIn.addChatComponentMessage(it.apply { style.color = TextFormatting.DARK_RED })
                }
            }
        } else {
            MultiblockManager.activateMultiblockStructure(context)
            playerIn.sendMessage("text.magneticraft.multiblock.activate", color = TextFormatting.GREEN)
        }
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        if (worldIn.isServer) {
            val tile = worldIn.getTileEntity(pos)
            if (tile is ITileMultiblock) {
                if (tile.multiblock != null && tile.multiblockFacing != null) {
                    MultiblockManager.deactivateMultiblockStructure(MultiblockContext(tile.multiblock!!, worldIn, pos.subtract(tile.centerPos!!), tile.multiblockFacing!!, null))
                }
            }
        }
        super.breakBlock(worldIn, pos, state)
    }
}