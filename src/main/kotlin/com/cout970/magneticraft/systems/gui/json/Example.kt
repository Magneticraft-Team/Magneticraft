package com.cout970.magneticraft.systems.gui.json

import com.cout970.magneticraft.api.internal.registries.machines.gasificationunit.GasificationUnitRecipeManager
import com.cout970.magneticraft.features.electric_machines.TileBattery
import com.cout970.magneticraft.features.electric_machines.TileElectricFurnace
import com.cout970.magneticraft.features.electric_machines.TileThermopile
import com.cout970.magneticraft.features.electric_machines.TileWindTurbine
import com.cout970.magneticraft.features.heat_machines.*
import com.cout970.magneticraft.features.manual_machines.TileBox
import com.cout970.magneticraft.misc.gui.SlotType
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.registry.FORGE_ENERGY
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.components.bars.StaticBarProvider
import com.cout970.magneticraft.systems.gui.components.bars.toIntText
import com.cout970.magneticraft.systems.gui.components.bars.toPercentText
import com.cout970.magneticraft.systems.gui.render.TankIO
import net.minecraft.init.Items
import net.minecraft.item.crafting.FurnaceRecipes

fun GuiBuilder.boxGui(tile: TileBox) {
    container {
        slotGroup(3, 9, tile.inventory, 0, "inv_start")
        region(0, 27)
        playerInventory("player_inv_offset")
    }
}

fun GuiBuilder.electricHeaterGui(tile: TileElectricHeater) {
    container {
        playerInventory()
    }
    bars {
        electricBar(tile.electricNode)
        heatBar(tile.heatNode)
        consumptionBar(tile.electricHeaterModule.consumption, Config.electricHeaterMaxProduction)
        productionBar(tile.electricHeaterModule.production, Config.electricHeaterMaxProduction)
    }
}

fun GuiBuilder.rfHeaterGui(tile: TileRfHeater) {
    container {
        playerInventory()
    }
    bars {
        rfBar(tile.rfModule.storage)
        heatBar(tile.node)
        consumptionBar(tile.electricHeaterModule.consumption, Config.electricHeaterMaxProduction)
        productionBar(tile.electricHeaterModule.production, Config.electricHeaterMaxProduction)
    }
}

fun GuiBuilder.combustionChamberGui(tile: TileCombustionChamber) {
    container {
        slot(tile.invModule.inventory, 0, "fuel_slot", SlotType.INPUT)
        region(0, 1, filter = { it, _ -> it.isNotEmpty && it.item == Items.COAL })
        playerInventory()
    }
    bars {
        heatBar(tile.node)
        fuelBar(tile.combustionChamberModule)
        slotSpacer()
    }
}

fun GuiBuilder.steamBoilerGui(tile: TileSteamBoiler) {
    container {
        playerInventory()
    }
    bars {
        heatBar(tile.node)
        machineFluidBar(tile.boilerModule.production, tile.boilerModule.maxProduction)
        tank(tile.waterTank, TankIO.IN)
        tank(tile.steamTank, TankIO.OUT)
    }
}

fun GuiBuilder.batteryBlockGui(tile: TileBattery) {
    container {
        slot(tile.invModule.inventory, 0, "input_slot", SlotType.INPUT)
        slot(tile.invModule.inventory, 1, "output_slot", SlotType.OUTPUT)
        region(0, 1, filter = { it, _ -> FORGE_ENERGY!!.fromItem(it)?.canReceive() ?: false })
        region(1, 1, filter = { it, _ -> FORGE_ENERGY!!.fromItem(it)?.canExtract() ?: false })
        playerInventory()
    }
    bars {
        electricBar(tile.node)
        electricTransferBar(tile.storageModule.chargeRate, -tile.storageModule.maxChargeSpeed, tile.storageModule.maxChargeSpeed)
        storageBar(tile.storageModule)
        electricTransferBar(tile.itemChargeModule.itemChargeRate, -tile.itemChargeModule.transferRate, tile.itemChargeModule.transferRate)
        slotSpacer()
    }
}

fun GuiBuilder.electricFurnaceGui(tile: TileElectricFurnace) {
    container {
        slot(tile.invModule.inventory, 0, "input_slot", SlotType.INPUT)
        slot(tile.invModule.inventory, 1, "output_slot", SlotType.OUTPUT, blockInput = true)
        region(0, 1, filter = { it, _ -> FurnaceRecipes.instance().getSmeltingResult(it).isNotEmpty })
        region(1, 1, filter = { _, _ -> false })
        playerInventory()
    }
    bars {
        electricBar(tile.node)
        consumptionBar(tile.processModule.consumption, Config.electricFurnaceMaxConsumption)
        progressBar(tile.processModule.timedProcess)
        slotSpacer()
    }
}

fun GuiBuilder.brickFurnaceGui(tile: TileBrickFurnace) {
    container {
        slot(tile.invModule.inventory, 0, "input_slot", SlotType.INPUT)
        slot(tile.invModule.inventory, 1, "output_slot", SlotType.OUTPUT, blockInput = true)
        region(0, 1, filter = { it, _ -> FurnaceRecipes.instance().getSmeltingResult(it).isNotEmpty })
        region(1, 1, filter = { _, _ -> false })
        playerInventory()
    }
    bars {
        heatBar(tile.node)
        consumptionBar(tile.processModule.consumption, Config.electricFurnaceMaxConsumption)
        progressBar(tile.processModule.timedProcess)
        slotSpacer()
    }
}

fun GuiBuilder.thermopileGui(tile: TileThermopile) {
    container {
        playerInventory()
    }
    bars {
        electricBar(tile.node)
        storageBar(tile.storage)
        productionBar(tile.thermopileModule.production, Config.thermopileProduction)

        StaticBarProvider(0.0, 10_000.0, tile.thermopileModule::totalFlux).let { prov ->
            genericBar(2, 4, prov, prov.toIntText(postfix = " Heat Flux/t"))
        }
    }
}

fun GuiBuilder.windTurbineGui(tile: TileWindTurbine) {
    container {
        playerInventory()
    }
    bars {
        electricBar(tile.node)
        storageBar(tile.storageModule)
        productionBar(tile.windTurbineModule.production, Config.windTurbineMaxProduction)

        StaticBarProvider(0.0, 1.0, tile.windTurbineModule::openSpace).let { prov ->
            genericBar(8, 5, prov, prov.toPercentText("Wind clearance: "))
        }

        StaticBarProvider(0.0, 1.0, tile.windTurbineModule::currentWind).let { prov ->
            genericBar(9, 7, prov, prov.toPercentText("Wind speed: ", "%"))
        }
    }
}

fun GuiBuilder.gasificationUnitGui(tile: TileGasificationUnit) {
    container {
        slot(tile.inv, 0, "input_slot", SlotType.INPUT)
        slot(tile.inv, 1, "output_slot", SlotType.OUTPUT, blockInput = true)
        region(0, 1, filter = { it, _ -> GasificationUnitRecipeManager.findRecipe(it) != null })
        region(1, 1, filter = { _, _ -> false })
        playerInventory()
    }
    bars {
        heatBar(tile.heatNode)
        consumptionBar(tile.process.consumption, Config.gasificationUnitConsumption)
        progressBar(tile.process.timedProcess)
        slotSpacer()
        tank(tile.tank, TankIO.OUT)
    }
}