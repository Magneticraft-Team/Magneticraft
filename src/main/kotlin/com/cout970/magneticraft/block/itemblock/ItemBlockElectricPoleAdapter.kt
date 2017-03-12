package com.cout970.magneticraft.block.itemblock


import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.block.BlockElectricPole
import com.cout970.magneticraft.block.BlockElectricPoleAdapter
import com.cout970.magneticraft.block.BlockElectricPoleBase
import com.cout970.magneticraft.block.ELECTRIC_POLE_PLACE
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.electric.TileElectricPole
import com.cout970.magneticraft.tileentity.electric.TileElectricPoleAdapter
import com.cout970.magneticraft.util.with
import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.base.block.ItemModBlock
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 19/07/2016.
 */
class ItemBlockElectricPoleAdapter(blockMod: BlockMod) : ItemModBlock(blockMod) {

    override fun onItemUse(stack: ItemStack, placer: EntityPlayer, worldIn: World, p: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        val state = worldIn.getBlockState(p)
        val block = state.block
        if (block is BlockElectricPole) {
            val main = block.getMainPos(state, p)
            val pos = main.add(0, -4, 0)
            val tile = worldIn.getTile<TileElectricPole>(main)
            val connections = mutableListOf<IElectricConnection>()
            if (tile != null) {
                connections.addAll(tile.traitElectricity.outputWiredConnections with tile.traitElectricity.inputWiredConnections)
            }
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
                        BlockElectricPoleBase.ElectricPoleStates.NORTH_EAST
                    } else if (yaw < -a * 2 + b && yaw >= -a * 3 + b) {
                        BlockElectricPoleBase.ElectricPoleStates.EAST
                    } else if (yaw < -a + b && yaw >= -a * 2 + b) {
                        BlockElectricPoleBase.ElectricPoleStates.SOUTH_EAST
                    } else if (yaw < 0 + b && yaw >= -a + b) {
                        BlockElectricPoleBase.ElectricPoleStates.SOUTH
                    } else if (yaw < a + b && yaw >= 0 + b) {
                        BlockElectricPoleBase.ElectricPoleStates.SOUTH_WEST
                    } else if (yaw < a * 2 + b && yaw >= a + b) {
                        BlockElectricPoleBase.ElectricPoleStates.WEST
                    } else if (yaw < a * 3 + b && yaw >= a * 2 + b) {
                        BlockElectricPoleBase.ElectricPoleStates.NORTH_WEST
                    } else if (yaw < a * 4 + b && yaw >= a * 3 + b) {
                        BlockElectricPoleBase.ElectricPoleStates.NORTH
                    } else {
                        BlockElectricPoleBase.ElectricPoleStates.NORTH
                    }

            worldIn.setBlockToAir(pos)
            worldIn.setBlockState(pos, BlockElectricPoleAdapter.defaultState.withProperty(ELECTRIC_POLE_PLACE, BlockElectricPoleBase.ElectricPoleStates.DOWN_4))
            worldIn.setBlockState(pos.offset(EnumFacing.UP, 1), BlockElectricPoleAdapter.defaultState.withProperty(ELECTRIC_POLE_PLACE, BlockElectricPoleBase.ElectricPoleStates.DOWN_3))
            worldIn.setBlockState(pos.offset(EnumFacing.UP, 2), BlockElectricPoleAdapter.defaultState.withProperty(ELECTRIC_POLE_PLACE, BlockElectricPoleBase.ElectricPoleStates.DOWN_2))
            worldIn.setBlockState(pos.offset(EnumFacing.UP, 3), BlockElectricPoleAdapter.defaultState.withProperty(ELECTRIC_POLE_PLACE, BlockElectricPoleBase.ElectricPoleStates.DOWN_1))
            worldIn.setBlockState(pos.offset(EnumFacing.UP, 4), BlockElectricPoleAdapter.defaultState.withProperty(ELECTRIC_POLE_PLACE, dir))
            worldIn.getTile<TileElectricPoleAdapter>(main)?.loadConnections(connections)

            stack.stackSize--
            return EnumActionResult.SUCCESS
        }
        return EnumActionResult.PASS
    }
}