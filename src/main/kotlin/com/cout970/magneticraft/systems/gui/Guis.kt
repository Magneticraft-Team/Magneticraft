package com.cout970.magneticraft.systems.gui

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.internal.registries.machines.gasificationunit.GasificationUnitRecipeManager
import com.cout970.magneticraft.features.automatic_machines.TileFilter
import com.cout970.magneticraft.features.automatic_machines.TileRelay
import com.cout970.magneticraft.features.electric_machines.TileBattery
import com.cout970.magneticraft.features.electric_machines.TileElectricFurnace
import com.cout970.magneticraft.features.electric_machines.TileThermopile
import com.cout970.magneticraft.features.electric_machines.TileWindTurbine
import com.cout970.magneticraft.features.heat_machines.*
import com.cout970.magneticraft.features.manual_machines.TileBox
import com.cout970.magneticraft.features.manual_machines.TileFabricator
import com.cout970.magneticraft.features.multiblocks.tileentities.*
import com.cout970.magneticraft.misc.gui.SlotType
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.FORGE_ENERGY
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.components.bars.*
import com.cout970.magneticraft.systems.tilemodules.ModulePumpjack.Status.*
import net.minecraft.init.Items
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraftforge.items.wrapper.InvWrapper

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

fun GuiBuilder.grinderGui(tile: TileGrinder) {
    container {
        slot(tile.inventory, 0, "input_slot", SlotType.INPUT)
        slot(tile.inventory, 1, "output_slot", SlotType.OUTPUT, blockInput = true)
        slot(tile.inventory, 2, "output2_slot", SlotType.OUTPUT, blockInput = true)
        region(0, 1, filter = { it, _ -> MagneticraftApi.getGrinderRecipeManager().findRecipe(it) != null })
        region(1, 1, filter = { _, _ -> false })
        region(2, 1, filter = { _, _ -> false })
        playerInventory()
    }
    bars {
        electricBar(tile.node)
        storageBar(tile.storageModule)
        consumptionBar(tile.processModule.consumption, Config.grinderMaxConsumption)
        progressBar(tile.processModule.timedProcess)
        drawable(vec2Of(40, 50), "arrow_offset", "arrow_size", "arrow_uv")
    }
}

fun GuiBuilder.sieveGui(tile: TileSieve) {
    container {
        slot(tile.inventory, 0, "input_slot", SlotType.INPUT)
        slot(tile.inventory, 1, "output_slot", SlotType.OUTPUT, blockInput = true)
        slot(tile.inventory, 2, "output2_slot", SlotType.OUTPUT, blockInput = true)
        slot(tile.inventory, 3, "output3_slot", SlotType.OUTPUT, blockInput = true)
        region(0, 1, filter = { it, _ -> MagneticraftApi.getSieveRecipeManager().findRecipe(it) != null })
        region(1, 1, filter = { _, _ -> false })
        region(2, 1, filter = { _, _ -> false })
        region(3, 1, filter = { _, _ -> false })
        playerInventory()
    }
    bars {
        electricBar(tile.node)
        storageBar(tile.storageModule)
        consumptionBar(tile.processModule.consumption, Config.sieveMaxConsumption)
        progressBar(tile.processModule.timedProcess)
        drawable(vec2Of(64, 50), "arrow_offset", "arrow_size", "arrow_uv")
    }
}

fun GuiBuilder.containerGui(tile: TileContainer) {
    container {
        val inv = tile.stackInventoryModule.getGuiInventory()
        slot(inv, 0, "input_slot", SlotType.INPUT)
        slot(inv, 1, "output_slot", SlotType.OUTPUT, blockInput = true)
        region(0, 1, filter = { _, i -> i != 1 })
        region(1, 1, filter = { _, _ -> false })
        playerInventory()
    }
    bars {
        val mod = tile.stackInventoryModule
        val callback = CallbackBarProvider(mod::amount, mod::maxItems, ZERO)
        genericBar(7, 3, callback) { listOf("Items: ${mod.amount}/${mod.maxItems}") }
        slotSpacer()
    }
}

fun GuiBuilder.pumpjackGui(tile: TilePumpjack) {
    container {
        playerInventory()
    }
    bars {
        electricBar(tile.node)
        storageBar(tile.storageModule)
        consumptionBar(tile.pumpjackModule.production, Config.pumpjackConsumption)

        val mod = tile.pumpjackModule
        val processCallback = CallbackBarProvider({
            when (mod.status) {
                SEARCHING_OIL, SEARCHING_DEPOSIT, DIGGING -> {
                    mod.processPercent
                }
                SEARCHING_SOURCE, EXTRACTING -> {
                    mod.depositLeft
                }
            }
        }, {
            when (mod.status) {
                SEARCHING_OIL, SEARCHING_DEPOSIT, DIGGING -> {
                    1.0
                }
                SEARCHING_SOURCE, EXTRACTING -> {
                    mod.depositSize
                }
            }
        }, ZERO)

        genericBar(6, 3, processCallback) {
            val percent = "%.2f".format(mod.processPercent * 100f)
            val amount = "${mod.depositLeft}/${mod.depositSize}"

            when (mod.status) {
                SEARCHING_OIL -> listOf("Searching for oil: $percent%")
                SEARCHING_DEPOSIT -> listOf("Scanning deposit size: $percent%")
                DIGGING -> listOf("Mining to deposit: $percent%")
                SEARCHING_SOURCE -> listOf("Oil deposit: $amount blocks", "Moving to next source: $percent%")
                EXTRACTING -> listOf("Oil deposit: $amount blocks", "Extracting...")
            }
        }

        tank(tile.tank, TankIO.OUT)
    }
}

fun GuiBuilder.oilHeaterGui(tile: TileOilHeater) {
    container {
        playerInventory()
    }
    bars {
        heatBar(tile.node)
        consumptionBar(tile.processModule.consumption, tile.processModule.costPerTick)
        tank(tile.inputTank, TankIO.IN)
        tank(tile.outputTank, TankIO.OUT)
    }
}

fun GuiBuilder.refineryGui(tile: TileRefinery) {
    container {
        playerInventory()
    }
    bars {
        consumptionBar(tile.processModule.consumption, Config.refineryMaxConsumption)
        tank(tile.steamTank, TankIO.IN)
        tank(tile.inputTank, TankIO.IN)
        tank(tile.outputTank0, TankIO.OUT)
        tank(tile.outputTank1, TankIO.OUT)
        tank(tile.outputTank2, TankIO.OUT)
    }
}

fun GuiBuilder.steamEngineGui(tile: TileSteamEngine) {
    container {
        playerInventory()
    }
    bars {
        electricBar(tile.node)
        storageBar(tile.storageModule)
        productionBar(tile.steamGeneratorModule.production, tile.steamGeneratorModule.maxProduction)
        tank(tile.tank, TankIO.IN)
    }
}

fun GuiBuilder.bigCombustionChamberGui(tile: TileBigCombustionChamber) {
    container {
        slot(tile.inventory, 0, "fuel_slot")
        region(0, 1) { it, _ -> TileEntityFurnace.isItemFuel(it) }
        playerInventory()
    }

    bars {
        heatBar(tile.node)
        fuelBar(tile.bigCombustionChamberModule)
        tank(tile.tank, TankIO.IN)
        slotSpacer()
    }
}

fun GuiBuilder.bigSteamBoilerGui(tile: TileBigSteamBoiler) {
    container {
        playerInventory()
    }

    bars {
        heatBar(tile.node)
        machineFluidBar(tile.boiler.production, tile.boiler.maxProduction)
        tank(tile.input, TankIO.IN)
        tank(tile.output, TankIO.OUT)
    }
}

fun GuiBuilder.steamTurbineGui(tile: TileSteamTurbine) {
    container {
        playerInventory()
    }

    bars {
        electricBar(tile.node)
        productionBar(tile.steamGeneratorModule.production, tile.steamGeneratorModule.maxProduction)
        tank(tile.tank, TankIO.IN)
    }
}

fun GuiBuilder.relayGui(tile: TileRelay) {
    container {
        slotGroup(3, 3, tile.inventory, 0, "inv_start")
        region(0, 9)
        playerInventory()
    }

    bars {
        slotSpacer(3, 3)
    }
}

fun GuiBuilder.filterGui(tile: TileFilter) {
    container {
        slotGroup(3, 3, tile.inventory, 0, "inv_start", SlotType.FILTER)
        region(0, 9) { _, _ -> false }
        playerInventory()
    }

    bars {
        slotSpacer(3, 3)
    }
}

fun GuiBuilder.fabricatorGui(tile: TileFabricator) {
    container {
        slotGroup(3, 3, InvWrapper(tile.fabricatorModule.recipeGrid), 0, "recipe_grid", SlotType.FILTER)
        slotButton(tile.fabricatorModule.craftingResult, 0, "recipe_result") { player, type ->
            if (player.world.isServer) {
                if (type == 1) {
                    tile.fabricatorModule.clearGrid()
                } else if (type == 0) {
                    tile.fabricatorModule.requestItemCraft()
                }
            }
        }
        slotGroup(3, 3, tile.inventory, 0, "inv_start")

        region(0, 9) { _, _ -> false }
        region(9, 1) { _, _ -> false }
        region(10, 9)
        playerInventory()
    }

    bars {
        slotSpacer(3, 3)
        slotSpacer()
        slotSpacer(3, 3)
    }
}

fun GuiBuilder.solarTowerGui(tile: TileSolarTower) {
    container {
        playerInventory()

        onClick("btn1") {
            sendToServer(IBD().setBoolean(0, true))
        }

        receiveDataFromClient {
            tile.solarTowerModule.searchMirrors = true
        }
    }
    bars {
        heatBar(tile.node)
        productionBar(tile.solarTowerModule.production, 500)
        clickButton("btn1", "button_offset")
        drawable(vec2Of(0), "icon_offset", "icon_size", "icon_uv")
    }
}
