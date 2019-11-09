package com.cout970.magneticraft.features.heat_machines

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.EnumFacing
import com.cout970.magneticraft.TileType
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.api.internal.heat.HeatNode
import com.cout970.magneticraft.features.electric_machines.Blocks
import com.cout970.magneticraft.misc.RegisterTileEntity
import com.cout970.magneticraft.misc.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.misc.block.getFacing
import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.misc.block.getOrientationActive
import com.cout970.magneticraft.misc.crafting.FurnaceCraftingProcess
import com.cout970.magneticraft.misc.crafting.GasificationCraftingProcess
import com.cout970.magneticraft.misc.energy.RfNodeWrapper
import com.cout970.magneticraft.misc.energy.RfStorage
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.fromCelsiusToKelvin
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.InventoryCapabilityFilter
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.FluidHolder
import com.cout970.magneticraft.systems.blocks.CommonMethods
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilemodules.*
import com.cout970.magneticraft.withProperty
import net.minecraft.fluid.Fluids
import net.minecraft.tileentity.ITickableTileEntity
import net.minecraft.util.Direction
import net.minecraft.util.math.BlockPos

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterTileEntity("combustion_chamber")
class TileCombustionChamber(type: TileType) : TileBase(type), ITickableTileEntity {

    val facing: EnumFacing get() = blockState.getOrientation()
    val inventory = Inventory(1)
    val node = HeatNode(ref)

    val heatModule = ModuleHeat(node, capabilityFilter = { it == EnumFacing.UP })
    val invModule = ModuleInventory(inventory)
    val combustionChamberModule = ModuleCombustionChamber(node, inventory)

    init {
        initModules(invModule, combustionChamberModule, heatModule)
    }

    override fun tick() {
        super.update()
    }
}

@RegisterTileEntity("steam_boiler")
class TileSteamBoiler(type: TileType) : TileBase(type), ITickableTileEntity {

    val node = HeatNode(ref)

    val heatModule = ModuleHeat(node)

    val waterTank = Tank(
        capacity = 1000,
        allowInput = true,
        allowOutput = false,
        fluidFilter = { it.fluid == Fluids.WATER }
    ).apply { clientFluidName = "water" }

    val steamTank = Tank(
        capacity = 16000,
        allowInput = false,
        allowOutput = true,
        fluidFilter = { it.fluid == FluidHolder.STEAM }
    ).apply { clientFluidName = "steam" }

    val fluidModule = ModuleFluidHandler(waterTank, steamTank)

    val boilerModule = ModuleSteamBoiler(node, waterTank, steamTank, Config.boilerMaxProduction)

    val fluidExportModule = ModuleFluidExporter(steamTank, { listOf(BlockPos(0, 1, 0) to EnumFacing.DOWN) })
    val openGui = ModuleOpenGui()

    init {
        initModules(fluidModule, boilerModule, fluidExportModule, openGui, heatModule)
    }

    override fun tick() {
        super.update()
    }
}

@RegisterTileEntity("heat_pipe")
class TileHeatPipe(type: TileType) : TileBase(type), ITickableTileEntity {

    val heatNode = HeatNode(ref)
    val heatModule = ModuleHeat(heatNode)
    val heatPipeConnections = ModuleHeatPipeConnections(heatModule)

    init {
        initModules(heatModule, heatPipeConnections)
    }

    override fun tick() {
        super.update()

        if (Debug.DEBUG) {
            sendUpdateToNearPlayers()
        }
    }
}

@RegisterTileEntity("insulated_heat_pipe")
class TileInsulatedHeatPipe(type: TileType) : TileBase(type), ITickableTileEntity {

    val heatNode = HeatNode(ref)
    val heatModule = ModuleHeat(heatNode)
    val heatPipeConnections = ModuleHeatPipeConnections(heatModule)

    init {
        initModules(heatModule, heatPipeConnections)
    }

    override fun tick() {
        super.update()

        if (Debug.DEBUG) {
            sendUpdateToNearPlayers()
        }
    }
}

@RegisterTileEntity("heat_sink")
class TileHeatSink(type: TileType) : TileBase(type), ITickableTileEntity {

    val facing: EnumFacing get() = blockState.getFacing()
    val heatNode = HeatNode(ref)
    val heatModule = ModuleHeat(heatNode, capabilityFilter = { it != facing.opposite })

    init {
        initModules(heatModule)
    }

    override fun tick() {
        super.update()

        // Dissipate heat
        if (theWorld.isServer && heatNode.temperature > STANDARD_AMBIENT_TEMPERATURE) {
            val diff = heatNode.temperature - STANDARD_AMBIENT_TEMPERATURE
            heatNode.applyHeat(-diff)
        }
    }
}

@RegisterTileEntity("electric_heater")
class TileElectricHeater(type: TileType) : TileBase(type), ITickableTileEntity {

    val electricNode = ElectricNode(ref)
    val heatNode = HeatNode(ref)

    val electricModule = ModuleElectricity(listOf(electricNode))

    val moduleHeat = ModuleHeat(heatNode, capabilityFilter = { it?.axis == Direction.Axis.Y })

    val storageModule = ModuleInternalStorage(capacity = 10000, mainNode = electricNode)

    val electricHeaterModule = ModuleElectricHeater(heatNode, storageModule)

    val updateBlockstate = ModuleUpdateBlockstate { currentState ->
        if (heatNode.temperature > 90.fromCelsiusToKelvin())
            currentState.with(Blocks.PROPERTY_WORKING_MODE, Blocks.WorkingMode.ON)
        else
            currentState.with(Blocks.PROPERTY_WORKING_MODE, Blocks.WorkingMode.OFF)
    }

    init {
        initModules(electricModule, storageModule, electricHeaterModule, updateBlockstate, moduleHeat)
    }


    override fun tick() {
        super.update()
    }
}

@RegisterTileEntity("rf_heater")
class TileRfHeater(type: TileType) : TileBase(type), ITickableTileEntity {

    val storage = RfStorage(80_000)
    val node = HeatNode(ref)

    val rfModule = ModuleRf(storage)
    val heatModule = ModuleHeat(node, capabilityFilter = { it?.axis == Direction.Axis.Y })

    val electricHeaterModule = ModuleElectricHeater(node, RfNodeWrapper(storage))

    val updateBlockstate = ModuleUpdateBlockstate { currentState ->
        if (node.temperature > 90.fromCelsiusToKelvin())
            currentState.withProperty(Blocks.PROPERTY_WORKING_MODE, Blocks.WorkingMode.ON)
        else
            currentState.withProperty(Blocks.PROPERTY_WORKING_MODE, Blocks.WorkingMode.OFF)
    }

    init {
        initModules(electricHeaterModule, rfModule, updateBlockstate, heatModule)
    }

    override fun tick() {
        super.update()
    }
}

@RegisterTileEntity("gasification_unit")
class TileGasificationUnit(type: TileType) : TileBase(type), ITickableTileEntity {

    val tank = Tank(4_000)
    val heatNode = HeatNode(ref)
    val inv = Inventory(2)

    val fluidModule = ModuleFluidHandler(tank)
    val heatModule = ModuleHeat(heatNode)
    val inventoryModule = ModuleInventory(inv, capabilityFilter = { InventoryCapabilityFilter(it, listOf(0), listOf(1)) })

    val exporter = ModuleFluidExporter(tank, { listOf(BlockPos(0, 1, 0) to EnumFacing.UP) })
    val bucketModule = ModuleBucketIO(tank, input = false, output = true)

    val openGui = ModuleOpenGui()
    val process = ModuleHeatProcessing(
        craftingProcess = GasificationCraftingProcess(tank, inv, 0, 1),
        node = heatNode,
        costPerTick = Config.gasificationUnitConsumption.toFloat(),
        workingRate = 1f
    )

    init {
        initModules(heatModule, fluidModule, exporter, bucketModule, process, inventoryModule, openGui)
    }

    override fun tick() {
        super.update()
    }
}

@RegisterTileEntity("brick_furnace")
class TileBrickFurnace(type: TileType) : TileBase(type), ITickableTileEntity {

    val facing: EnumFacing get() = blockState.getOrientationActive()
    val node = HeatNode(ref)

    val heatModule = ModuleHeat(node)

    val invModule = ModuleInventory(Inventory(2), capabilityFilter = {
        InventoryCapabilityFilter(it, inputSlots = listOf(0), outputSlots = listOf(1))
    })

    val updateBlockModule = ModuleUpdateBlockstate { oldState ->
        val state = CommonMethods.OrientationActive.of(facing, processModule.working)
        oldState.withProperty(CommonMethods.PROPERTY_ORIENTATION_ACTIVE, state)
    }

    val processModule = ModuleHeatProcessing(
        craftingProcess = FurnaceCraftingProcess(invModule, 0, 1),
        node = node,
        workingRate = 1f,
        costPerTick = Config.electricFurnaceMaxConsumption.toFloat()
    )

    init {
        initModules(heatModule, invModule, processModule, updateBlockModule)
    }

    override fun tick() {
        super.update()
    }
}