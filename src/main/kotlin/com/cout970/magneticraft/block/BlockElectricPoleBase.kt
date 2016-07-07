package com.cout970.magneticraft.block

import coffee.cypher.mcextlib.extensions.aabb.to
import com.cout970.magneticraft.block.states.ElectricPoleTypes
import com.cout970.magneticraft.util.get
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * Created by cout970 on 05/07/2016.
 */
abstract class BlockElectricPoleBase(material: Material, name: String) : BlockBase(material, name) {

    val boundingBox by lazy {
        val size = 0.0625 * 3
        Vec3d(0.5 - size, 0.0, 0.5 - size) to Vec3d(0.5 + size, 1.0, 0.5 + size)
    }

    override fun getBoundingBox(state: IBlockState?, source: IBlockAccess?, pos: BlockPos?) = boundingBox

    override fun isFullBlock(state: IBlockState?) = false
    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun isVisuallyOpaque() = false

    override fun getMetaFromState(state: IBlockState): Int =
            ELECTRIC_POLE_PLACE[state].ordinal

    override fun getStateFromMeta(meta: Int): IBlockState? =
            defaultState.withProperty(ELECTRIC_POLE_PLACE, ElectricPoleTypes.values()[meta])

    override fun createBlockState(): BlockStateContainer = BlockStateContainer(this, ELECTRIC_POLE_PLACE)


    override fun onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase, stack: ItemStack?) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)

        val yaw = if (placer.rotationYaw >= 180) {
            placer.rotationYaw - 360
        } else if (placer.rotationYaw <= -180) {
            placer.rotationYaw + 360
        } else {
            placer.rotationYaw
        }
        val a = 45
        val b = 45 / 2
        val dir =
                if (yaw < -a * 3 + b && yaw >= -a * 4 + b) {
                    ElectricPoleTypes.NORTH_EAST
                } else if (yaw < -a * 2 + b && yaw >= -a * 3 + b) {
                    ElectricPoleTypes.EAST
                } else if (yaw < -a + b && yaw >= -a * 2 + b) {
                    ElectricPoleTypes.SOUTH_EAST
                } else if (yaw < 0 + b && yaw >= -a + b) {
                    ElectricPoleTypes.SOUTH
                } else if (yaw < a + b && yaw >= 0 + b) {
                    ElectricPoleTypes.SOUTH_WEST
                } else if (yaw < a * 2 + b && yaw >= a + b) {
                    ElectricPoleTypes.WEST
                } else if (yaw < a * 3 + b && yaw >= a * 2 + b) {
                    ElectricPoleTypes.NORTH_WEST
                } else if (yaw < a * 4 + b && yaw >= a * 3 + b) {
                    ElectricPoleTypes.NORTH
                } else {
                    ElectricPoleTypes.NORTH
                }

        worldIn.setBlockState(pos, defaultState.withProperty(ELECTRIC_POLE_PLACE, ElectricPoleTypes.DOWN_4))
        worldIn.setBlockState(pos.offset(EnumFacing.UP, 1), defaultState.withProperty(ELECTRIC_POLE_PLACE, ElectricPoleTypes.DOWN_3))
        worldIn.setBlockState(pos.offset(EnumFacing.UP, 2), defaultState.withProperty(ELECTRIC_POLE_PLACE, ElectricPoleTypes.DOWN_2))
        worldIn.setBlockState(pos.offset(EnumFacing.UP, 3), defaultState.withProperty(ELECTRIC_POLE_PLACE, ElectricPoleTypes.DOWN_1))
        worldIn.setBlockState(pos.offset(EnumFacing.UP, 4), defaultState.withProperty(ELECTRIC_POLE_PLACE, dir))
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.breakBlock(worldIn, pos, state)
        val place = ELECTRIC_POLE_PLACE[state]
        if (place == ElectricPoleTypes.DOWN_1 || place == ElectricPoleTypes.DOWN_2 || place == ElectricPoleTypes.DOWN_3 || place == ElectricPoleTypes.DOWN_4) {
            val newPos = getMainPos(state, pos)
            worldIn.setBlockToAir(newPos)
        } else {
            for (i in 1..4) {
                worldIn.setBlockToAir(pos.offset(EnumFacing.DOWN, i))
            }
        }
    }

    fun getMainPos(state: IBlockState, pos: BlockPos): BlockPos {
        return when (ELECTRIC_POLE_PLACE[state]) {
            ElectricPoleTypes.DOWN_1 -> pos.offset(EnumFacing.UP, 1)
            ElectricPoleTypes.DOWN_2 -> pos.offset(EnumFacing.UP, 2)
            ElectricPoleTypes.DOWN_3 -> pos.offset(EnumFacing.UP, 3)
            ElectricPoleTypes.DOWN_4 -> pos.offset(EnumFacing.UP, 4)
            else -> pos
        }
    }

    override fun getRenderType(state: IBlockState): EnumBlockRenderType {
        return when (ELECTRIC_POLE_PLACE[state]) {
            ElectricPoleTypes.DOWN_1 -> EnumBlockRenderType.INVISIBLE
            ElectricPoleTypes.DOWN_2 -> EnumBlockRenderType.INVISIBLE
            ElectricPoleTypes.DOWN_3 -> EnumBlockRenderType.INVISIBLE
            ElectricPoleTypes.DOWN_4 -> EnumBlockRenderType.INVISIBLE
            else -> super.getRenderType(state)
        }
    }
}