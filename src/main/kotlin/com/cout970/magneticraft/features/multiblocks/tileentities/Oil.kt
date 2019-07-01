package com.cout970.magneticraft.features.multiblocks.tileentities

import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.api.internal.heat.HeatNode
import com.cout970.magneticraft.features.multiblocks.structures.MultiblockOilHeater
import com.cout970.magneticraft.features.multiblocks.structures.MultiblockPumpjack
import com.cout970.magneticraft.features.multiblocks.structures.MultiblockRefinery
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.RegisterTileEntity
import com.cout970.magneticraft.misc.crafting.OilHeaterCraftingProcess
import com.cout970.magneticraft.misc.crafting.RefineryCraftingProcess
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.fluid.TankCapabilityFilter
import com.cout970.magneticraft.misc.tileentity.DoNotRemove
import com.cout970.magneticraft.misc.vector.rotatePoint
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.registry.HEAT_NODE_HANDLER
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.multiblocks.Multiblock
import com.cout970.magneticraft.systems.tilemodules.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos

@RegisterTileEntity("pumpjack")
class TilePumpjack : TileMultiblock(), ITickable {

    override fun getMultiblock(): Multiblock = MultiblockPumpjack

    val tank = Tank(64000).apply { clientFluidName = "oil" }
    val node = ElectricNode(ref)

    val fluidModule = ModuleFluidHandler(tank,
        capabilityFilter = { it, side -> if (side == facing.opposite) it else null })

    val storageModule = ModuleInternalStorage(
        mainNode = node,
        capacity = 10_000,
        lowerVoltageLimit = ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE,
        upperVoltageLimit = ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE
    )

    val openGuiModule = ModuleOpenGui()

    val fluidExportModule = ModuleFluidExporter(tank, {
        listOf(facing.rotatePoint(BlockPos.ORIGIN, BlockPos(0, 0, 1)) to facing.opposite)
    })

    val ioModule: ModuleMultiblockIO = ModuleMultiblockIO(
        facing = { facing },
        connectionSpots = listOf(ConnectionSpot(
            capability = ELECTRIC_NODE_HANDLER!!,
            pos = BlockPos(-1, 0, 0),
            side = EnumFacing.UP,
            getter = { if (active) energyModule else null }
        ))
    )

    val energyModule = ModuleElectricity(
        electricNodes = listOf(node),
        canConnectAtSide = ioModule::canConnectAtSide,
        connectableDirections = ioModule::getElectricConnectPoints
    )

    val pumpjackModule = ModulePumpjack(
        energy = storageModule,
        tank = tank,
        ref = { pos.offset(facing, 5) },
        active = ::active
    )

    override val multiblockModule = ModuleMultiblockCenter(
        multiblockStructure = getMultiblock(),
        facingGetter = { facing },
        capabilityGetter = ioModule::getCapability
    )

    init {
        initModules(multiblockModule, fluidModule, energyModule, storageModule, ioModule, pumpjackModule,
            openGuiModule, fluidExportModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}


@RegisterTileEntity("oil_heater")
class TileOilHeater : TileMultiblock(), ITickable {

    override fun getMultiblock(): Multiblock = MultiblockOilHeater

    val node = HeatNode(ref)
    val inputTank = Tank(16_000)
    val outputTank = Tank(16_000)

    val openGuiModule = ModuleOpenGui()

    val fluidModule = ModuleFluidHandler(inputTank, outputTank,
        capabilityFilter = ModuleFluidHandler.ALLOW_NONE
    )

    val ioModule: ModuleMultiblockIO = ModuleMultiblockIO(
        facing = { facing },
        connectionSpots = listOf(ConnectionSpot(
            capability = FLUID_HANDLER!!,
            pos = BlockPos(0, 1, -2),
            side = EnumFacing.NORTH,
            getter = { if (active) TankCapabilityFilter(inputTank) else null }
        ), ConnectionSpot(
            capability = FLUID_HANDLER!!,
            pos = BlockPos(0, 2, -1),
            side = EnumFacing.UP,
            getter = { if (active) TankCapabilityFilter(outputTank) else null }
        )) + ModuleMultiblockIO.connectionArea(
            capability = HEAT_NODE_HANDLER!!,
            side = EnumFacing.DOWN,
            start = BlockPos(-1, 0, -2),
            end = BlockPos(1, 0, 0),
            getter = { if (active) heatModule else null }
        )
    )

    val heatModule = ModuleHeat(node,
        capabilityFilter = { false },
        connectableDirections = ioModule::getHeatConnectPoints
    )

    val processModule = ModuleHeatProcessing(
        costPerTick = Config.oilHeaterMaxConsumption.toFloat(),
        workingRate = 1f,
        node = node,
        craftingProcess = OilHeaterCraftingProcess(
            inputTank = inputTank,
            outputTank = outputTank
        )
    )

    override val multiblockModule = ModuleMultiblockCenter(
        multiblockStructure = getMultiblock(),
        facingGetter = { facing },
        capabilityGetter = ioModule::getCapability
    )

    init {
        initModules(multiblockModule, ioModule, heatModule, openGuiModule, fluidModule, processModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}

@RegisterTileEntity("refinery")
class TileRefinery : TileMultiblock(), ITickable {

    override fun getMultiblock(): Multiblock = MultiblockRefinery

    val steamTank = Tank(64_000)
    val inputTank = Tank(16_000)
    val outputTank0 = Tank(16_000)
    val outputTank1 = Tank(16_000)
    val outputTank2 = Tank(16_000)

    val openGuiModule = ModuleOpenGui()

    val fluidModule = ModuleFluidHandler(inputTank, outputTank0, outputTank1, outputTank2, steamTank,
        capabilityFilter = ModuleFluidHandler.ALLOW_NONE)

    val ioModule: ModuleMultiblockIO = ModuleMultiblockIO(
        facing = { facing },
        connectionSpots = listOf(ConnectionSpot(
            capability = FLUID_HANDLER!!,
            pos = BlockPos(0, 1, -2),
            side = EnumFacing.NORTH,
            getter = { if (active) TankCapabilityFilter(inputTank) else null }

        ), ConnectionSpot(
            capability = FLUID_HANDLER!!,
            pos = BlockPos(1, 1, -1),
            side = EnumFacing.EAST,
            getter = { if (active) TankCapabilityFilter(steamTank) else null }

        ), ConnectionSpot(
            capability = FLUID_HANDLER!!,
            pos = BlockPos(-1, 1, -1),
            side = EnumFacing.WEST,
            getter = { if (active) TankCapabilityFilter(steamTank) else null }

        )) + ModuleMultiblockIO.connectionCross(
            capability = FLUID_HANDLER!!,
            start = BlockPos(0, 3, -1), dist = 1,
            getter = { if (active) TankCapabilityFilter(outputTank0, canFill = false) else null }

        ) + ModuleMultiblockIO.connectionCross(
            capability = FLUID_HANDLER!!,
            start = BlockPos(0, 5, -1), dist = 1,
            getter = { if (active) TankCapabilityFilter(outputTank1, canFill = false) else null }

        ) + ModuleMultiblockIO.connectionCross(
            capability = FLUID_HANDLER!!,
            start = BlockPos(0, 7, -1), dist = 1,
            getter = { if (active) TankCapabilityFilter(outputTank2, canFill = false) else null }
        )
    )

    val processModule = ModuleSteamProcessing(
        costPerTick = Config.refineryMaxConsumption.toFloat(),
        workingRate = 1f,
        storage = steamTank,
        craftingProcess = RefineryCraftingProcess(
            inputTank = inputTank,
            outputTank0 = outputTank0,
            outputTank1 = outputTank1,
            outputTank2 = outputTank2
        )
    )

    override val multiblockModule = ModuleMultiblockCenter(
        multiblockStructure = getMultiblock(),
        facingGetter = { facing },
        capabilityGetter = ioModule::getCapability
    )

    init {
        initModules(multiblockModule, ioModule, openGuiModule, fluidModule, processModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}

