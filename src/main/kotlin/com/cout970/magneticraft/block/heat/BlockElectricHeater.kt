package com.cout970.magneticraft.block.heat

import com.cout970.magneticraft.block.BlockMultiState
import com.cout970.magneticraft.tileentity.heat.TileElectricHeater
import com.cout970.magneticraft.util.get
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * Created by cout970 on 04/07/2016.
 */
object BlockElectricHeater : BlockMultiState(Material.IRON, "electric_heater"), ITileEntityProvider, IHeatBlock {

    lateinit var PROPERTY_WORKING: PropertyEnum<PropertyWorking>

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileElectricHeater()

    override fun getMetaFromState(state: IBlockState): Int {
        return state[PROPERTY_WORKING].ordinal
    }

    override fun getStateFromMeta(meta: Int): IBlockState? {
        return defaultState.withProperty(PROPERTY_WORKING, PropertyWorking.values()[meta])
    }

    override fun createBlockState(): BlockStateContainer {
        PROPERTY_WORKING = PropertyEnum.create("working", PropertyWorking::class.java)
        return BlockStateContainer(this, PROPERTY_WORKING)
    }

    override fun getLightValue(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Int {
        return super.getHeatLightValue(state, world, pos)
    }

    override fun tickRate(worldIn: World?): Int {
        return 1
    }

    override fun onNeighborChange(world: IBlockAccess?, pos: BlockPos?, neighbor: BlockPos?) {
        super.heatNeighborCheck(world, pos, neighbor)
        super.onNeighborChange(world, pos, neighbor)
    }

    enum class PropertyWorking : IStringSerializable {
        OFF,
        ON;

        override fun getName(): String = name.toLowerCase()
    }
}