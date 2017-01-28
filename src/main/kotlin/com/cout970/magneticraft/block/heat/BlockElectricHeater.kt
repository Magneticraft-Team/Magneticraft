package com.cout970.magneticraft.block.heat

import com.cout970.magneticraft.block.BlockWithStates
import com.cout970.magneticraft.block.IStatesEnum
import com.cout970.magneticraft.block.PROPERTY_ACTIVE
import com.cout970.magneticraft.tileentity.heat.TileElectricHeater
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * Created by cout970 on 04/07/2016.
 */
object BlockElectricHeater : BlockWithStates(Material.IRON, "electric_heater"), ITileEntityProvider, IHeatBlock {

    override lateinit var states: Array<IStatesEnum>
        private set

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileElectricHeater()

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

    override fun initProperties() {
        states = States.values().map { it as IStatesEnum }.toTypedArray()
    }

    enum class States(
            override val isVisible: Boolean,
            override val stateName: String,
            val active: Boolean
    ) : IStatesEnum {
        OFF(true, "off", false),
        ON(false, "on", true);

        override val blockState: IBlockState get() = defaultState.withProperty(PROPERTY_ACTIVE, active)

        override val properties: List<IProperty<*>> = listOf(PROPERTY_ACTIVE)
    }
}