package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.api.internal.heat.HeatNode
import com.cout970.magneticraft.block.ElectricMachines
import com.cout970.magneticraft.misc.block.getFacing
import com.cout970.magneticraft.misc.block.getOrientation
import com.cout970.magneticraft.misc.energy.RfNodeWrapper
import com.cout970.magneticraft.misc.energy.RfStorage
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.tileentity.DoNotRemove
import com.cout970.magneticraft.misc.tileentity.RegisterTileEntity
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.*
import com.cout970.magneticraft.util.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.util.fromCelsiusToKelvin
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterTileEntity("combustion_chamber")
class TileCombustionChamber : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getOrientation()
    val inventory = Inventory(1)
    val node = HeatNode(ref)

    val heatModule = ModuleHeat(listOf(node), capabilityFilter = { it == EnumFacing.UP })
    val invModule = ModuleInventory(inventory)
    val combustionChamberModule = ModuleCombustionChamber(node, inventory)

    init {
        initModules(invModule, combustionChamberModule, heatModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}

@RegisterTileEntity("steam_boiler")
class TileSteamBoiler : TileBase(), ITickable {

    val node = HeatNode(ref)

    val heatModule = ModuleHeat(listOf(node))

    val waterTank = Tank(
            capacity = 1000,
            allowInput = true,
            allowOutput = false
    ).apply { clientFluidName = "water" }

    val steamTank = Tank(
            capacity = 16000,
            allowInput = false,
            allowOutput = true
    ).apply { clientFluidName = "steam" }

    val fluidModule = ModuleFluidHandler(waterTank, steamTank)

    val boilerModule = ModuleSteamBoiler(node, waterTank, steamTank, 20)

    val fluidExportModule = ModuleFluidExporter(steamTank, { listOf(BlockPos(0, 1, 0) to EnumFacing.DOWN) })
    val openGui = ModuleOpenGui()

    init {
        initModules(fluidModule, boilerModule, fluidExportModule, openGui, heatModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}

@RegisterTileEntity("heat_pipe")
class TileHeatPipe : TileBase(), ITickable {

    val heatNode = HeatNode(ref)

    val heatModule = ModuleHeat(listOf(heatNode))

    init {
        initModules(heatModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()

        if (Debug.DEBUG) {
            sendUpdateToNearPlayers()
        }
    }
}

@RegisterTileEntity("insulated_heat_pipe")
class TileInsulatedHeatPipe : TileBase(), ITickable {

    val heatNode = HeatNode(ref)

    val heatModule = ModuleHeat(listOf(heatNode))

    init {
        initModules(heatModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()

        if (Debug.DEBUG) {
            sendUpdateToNearPlayers()
        }
    }
}

@RegisterTileEntity("heat_sink")
class TileHeatSink : TileBase(), ITickable {

    val facing: EnumFacing get() = getBlockState().getFacing()

    val heatNode = HeatNode(ref)

    val heatModule = ModuleHeat(listOf(heatNode), capabilityFilter = { it != facing.opposite })

    init {
        initModules(heatModule)
    }

    @DoNotRemove
    override fun update() {
        super.update()

        // Dissipate heat
        if (world.isServer && heatNode.temperature > STANDARD_AMBIENT_TEMPERATURE) {
            val diff = heatNode.temperature - STANDARD_AMBIENT_TEMPERATURE
            heatNode.applyHeat(-(1 / 20.0) * diff)
        }
    }
}

@RegisterTileEntity("electric_heater")
class TileElectricHeater : TileBase(), ITickable {

    val electricNode = ElectricNode(ref)
    val heatNode = HeatNode(ref)

    val electricModule = ModuleElectricity(listOf(electricNode))

    val moduleHeat = ModuleHeat(listOf(heatNode), capabilityFilter = { it == EnumFacing.UP })

    val storageModule = ModuleInternalStorage(capacity = 10000, mainNode = electricNode)

    val electricHeaterModule = ModuleElectricHeater(heatNode, storageModule)

    val updateBlockstate = ModuleUpdateBlockstate { currentState ->
        if (heatNode.temperature > 90.fromCelsiusToKelvin())
            currentState.withProperty(ElectricMachines.PROPERTY_WORKING_MODE, ElectricMachines.WorkingMode.ON)
        else
            currentState.withProperty(ElectricMachines.PROPERTY_WORKING_MODE, ElectricMachines.WorkingMode.OFF)
    }

    init {
        initModules(electricModule, storageModule, electricHeaterModule, updateBlockstate, moduleHeat)
    }

    override fun shouldRefresh(world: World?, pos: BlockPos?, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.block !== newSate.block
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}

@RegisterTileEntity("rf_heater")
class TileRfHeater : TileBase(), ITickable {

    val storage = RfStorage(80_000)
    val node = HeatNode(ref)

    val rfModule = ModuleRf(storage)
    val heatModule = ModuleHeat(listOf(node), capabilityFilter = { it == EnumFacing.UP })

    val electricHeaterModule = ModuleElectricHeater(node, RfNodeWrapper(storage))

    val updateBlockstate = ModuleUpdateBlockstate { currentState ->
        if (node.temperature > 90.fromCelsiusToKelvin())
            currentState.withProperty(ElectricMachines.PROPERTY_WORKING_MODE, ElectricMachines.WorkingMode.ON)
        else
            currentState.withProperty(ElectricMachines.PROPERTY_WORKING_MODE, ElectricMachines.WorkingMode.OFF)
    }

    init {
        initModules(electricHeaterModule, rfModule, updateBlockstate, heatModule)
    }

    override fun shouldRefresh(world: World?, pos: BlockPos?, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.block !== newSate.block
    }

    @DoNotRemove
    override fun update() {
        super.update()
    }
}