package com.cout970.magneticraft.block.multiblock

import coffee.cypher.mcextlib.extensions.aabb.plus
import com.cout970.magneticraft.block.BlockMultiState
import com.cout970.magneticraft.multiblock.ITileMultiblock
import com.cout970.magneticraft.multiblock.MultiblockContext
import com.cout970.magneticraft.multiblock.MultiblockManager
import com.cout970.magneticraft.util.rotateBox
import com.cout970.magneticraft.util.*
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 21/08/2016.
 */
abstract class BlockMultiblock(material: Material, name: String) : BlockMultiState(material, name) {

    init {
        lightOpacity = 0
    }

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
            val blockReachDistance = Minecraft.getMinecraft().playerController.blockReachDistance
            val end = start.addVector(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance)

            boxes.forEach { list.add(it.offset(relPos)) }
            val res = list
                    .associate { it to rayTrace(pos, start, end, it) }
                    .filter { it.value != null }
                    .map { it.key to it.value }
                    .sortedBy { it.second!!.hitVec.distanceTo(start) }
                    .firstOrNull()?.first
            return res?.offset(pos).getOr(EMPTY_AABB)
        }
        return super.getSelectedBoundingBox(state, worldIn, pos)
    }

    fun activateMultiblock(context: MultiblockContext) {
        val errors = MultiblockManager.checkMultiblockStructure(context)
        val playerIn = context.player!!
        if (errors.isNotEmpty()) {
            playerIn.sendMessage("text.magneticraft.multiblock.error_count", errors.size)
            if (errors.size > 2) {
                playerIn.sendMessage("text.magneticraft.multiblock.first_errors", 2)
                errors.stream().limit(2).forEach {
                    playerIn.addChatComponentMessage(it)
                }
            } else {
                playerIn.sendMessage("text.magneticraft.multiblock.all_errors")
                errors.forEach {
                    playerIn.addChatComponentMessage(it)
                }
            }
        } else {
            MultiblockManager.activateMultiblockStructure(context)
            playerIn.sendMessage("text.magneticraft.multiblock.activate")
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