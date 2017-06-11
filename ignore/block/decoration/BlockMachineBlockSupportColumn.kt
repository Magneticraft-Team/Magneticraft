package com.cout970.magneticraft.block.decoration

import com.cout970.magneticraft.block.BlockMultiState
import com.cout970.magneticraft.misc.block.get
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

object BlockMachineBlockSupportColumn : BlockMultiState(Material.IRON, "machine_block_support_column", *States.values().map { it.getName() }.toTypedArray()) {


    override fun getSubBlocks(itemIn: Item?, tab: CreativeTabs?, list: MutableList<ItemStack>?) {
        if (list == null || itemIn == null) {
            return
        }
        list += ItemStack(itemIn)
    }

    lateinit var PROPERTY_STATES: PropertyEnum<States>
        private set

    override fun getMetaFromState(state: IBlockState): Int = state[PROPERTY_STATES].ordinal

    override fun getStateFromMeta(meta: Int): IBlockState = defaultState.withProperty(PROPERTY_STATES, States.values()[meta])

    override fun createBlockState(): BlockStateContainer {
        PROPERTY_STATES = PropertyEnum.create("axis", States::class.java)
        return BlockStateContainer(this, PROPERTY_STATES)
    }

    override fun onBlockPlaced(worldIn: World?, pos: BlockPos?, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase?): IBlockState {
        return defaultState.withProperty(PROPERTY_STATES, States.fromAxis(facing.axis))
    }

    enum class States : IStringSerializable {
        LINES_Y,
        LINES_X,
        LINES_Z;

        override fun getName(): String = name.toLowerCase()

        companion object {

            fun fromAxis(axis: EnumFacing.Axis): States = when (axis) {
                EnumFacing.Axis.X -> LINES_X
                EnumFacing.Axis.Y -> LINES_Y
                EnumFacing.Axis.Z -> LINES_Z
            }
        }
    }
}
