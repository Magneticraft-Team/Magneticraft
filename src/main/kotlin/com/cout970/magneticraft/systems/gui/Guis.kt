package com.cout970.magneticraft.systems.gui

import com.cout970.magneticraft.api.internal.registries.machines.gasificationunit.GasificationUnitRecipeManager
import com.cout970.magneticraft.api.internal.registries.machines.grinder.GrinderRecipeManager
import com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress.HydraulicPressRecipeManager
import com.cout970.magneticraft.api.internal.registries.machines.sieve.SieveRecipeManager
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.HydraulicPressMode
import com.cout970.magneticraft.features.automatic_machines.TileFilter
import com.cout970.magneticraft.features.automatic_machines.TileInserter
import com.cout970.magneticraft.features.automatic_machines.TileRelay
import com.cout970.magneticraft.features.automatic_machines.TileTransposer
import com.cout970.magneticraft.features.computers.ContainerComputer
import com.cout970.magneticraft.features.computers.ContainerMiningRobot
import com.cout970.magneticraft.features.computers.TileComputer
import com.cout970.magneticraft.features.computers.TileMiningRobot
import com.cout970.magneticraft.features.electric_conductors.TileTeslaTower
import com.cout970.magneticraft.features.electric_machines.*
import com.cout970.magneticraft.features.fluid_machines.TileSmallTank
import com.cout970.magneticraft.features.heat_machines.*
import com.cout970.magneticraft.features.items.Upgrades
import com.cout970.magneticraft.features.manual_machines.ContainerFabricator
import com.cout970.magneticraft.features.manual_machines.TileBox
import com.cout970.magneticraft.features.manual_machines.TileFabricator
import com.cout970.magneticraft.features.multiblocks.ContainerShelvingUnit
import com.cout970.magneticraft.features.multiblocks.tileentities.*
import com.cout970.magneticraft.misc.gui.SlotType
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.t
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.FORGE_ENERGY
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.components.CompFabricatorMatches
import com.cout970.magneticraft.systems.gui.components.ComponentShelvingUnit
import com.cout970.magneticraft.systems.gui.components.MonitorComponent
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
        storageBar(tile.storageModule)
        consumptionBar(tile.processModule.consumption, Config.electricFurnaceMaxConsumption)
        progressBar(tile.processModule.timedProcess)
        slotSpacer()
    }
}

fun GuiBuilder.bigElectricFurnaceGui(tile: TileBigElectricFurnace) {
    container {
        slot(tile.invModule.inventory, 0, "input_slot", SlotType.INPUT)
        slot(tile.invModule.inventory, 1, "output_slot", SlotType.OUTPUT, blockInput = true)
        region(0, 1, filter = { it, _ -> FurnaceRecipes.instance().getSmeltingResult(it).isNotEmpty })
        region(1, 1, filter = { _, _ -> false })
        playerInventory()
    }
    bars {
        electricBar(tile.node)
        storageBar(tile.storageModule)
        consumptionBar(tile.processModule.consumption, Config.bigElectricFurnaceMaxConsumption)
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
        region(0, 1, filter = { it, _ -> GrinderRecipeManager.findRecipe(it) != null })
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
        region(0, 1, filter = { it, _ -> SieveRecipeManager.findRecipe(it) != null })
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
        drawable(vec2Of(62, 50), "arrow_offset", "arrow_size", "arrow_uv")
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
        machineFluidBar(tile.processModule.consumption, Config.refineryMaxConsumption)
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

fun GuiBuilder.transposerGui(tile: TileTransposer) {
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
    containerClass = ::ContainerFabricator

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

    components { custom { CompFabricatorMatches(tile) } }

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

fun GuiBuilder.inserterGui(tile: TileInserter) {
    container {
        slot(tile.inventory, 0, "grabbed")
        slot(tile.inventory, 1, "upgrade1")
        slot(tile.inventory, 2, "upgrade2")
        slotGroup(3, 3, tile.filters, 0, "filters", SlotType.FILTER)
        region(0, 1) { stack, _ -> stack.item != Upgrades.inserterUpgrade }
        region(1, 2) { stack, _ -> stack.item == Upgrades.inserterUpgrade }
        region(3, 9) { _, _ -> false }
        playerInventory()

        switchButtonState("btn0") { tile.inserterModule.whiteList }
        switchButtonState("btn1") { tile.inserterModule.useOreDictionary }
        switchButtonState("btn2") { tile.inserterModule.useMetadata }
        switchButtonState("btn3") { tile.inserterModule.useNbt }
        switchButtonState("btn4") { tile.inserterModule.canDropItems }
        switchButtonState("btn5") { tile.inserterModule.canGrabItems }

        onClick("btn0") { sendToServer(IBD().setInteger(0, 0)) }
        onClick("btn1") { sendToServer(IBD().setInteger(0, 1)) }
        onClick("btn2") { sendToServer(IBD().setInteger(0, 2)) }
        onClick("btn3") { sendToServer(IBD().setInteger(0, 3)) }
        onClick("btn4") { sendToServer(IBD().setInteger(0, 4)) }
        onClick("btn5") { sendToServer(IBD().setInteger(0, 5)) }

        receiveDataFromClient {
            it.getInteger(0) { prop ->
                val mod = tile.inserterModule
                when (prop) {
                    0 -> mod.whiteList = !mod.whiteList
                    1 -> mod.useOreDictionary = !mod.useOreDictionary
                    2 -> mod.useMetadata = !mod.useMetadata
                    3 -> mod.useNbt = !mod.useNbt
                    4 -> mod.canDropItems = !mod.canDropItems
                    5 -> mod.canGrabItems = !mod.canGrabItems
                }
            }
        }
    }

    bars {
        slotSpacer(3, 3)
        slotSpacer(1, 2)

        group(vec2Of(38, 58)) {
            switchButton("btn0", "btn0_offset", "btn0_on", "btn0_off", t("gui.magneticraft.inserter.btn0"), t("gui.magneticraft.inserter.btn0_off"))
            switchButton("btn1", "btn1_offset", "btn1_on", "btn1_off", t("gui.magneticraft.inserter.btn1"), t("gui.magneticraft.inserter.btn1_off"))
            switchButton("btn2", "btn2_offset", "btn2_on", "btn2_off", t("gui.magneticraft.inserter.btn2"), t("gui.magneticraft.inserter.btn2_off"))
            switchButton("btn3", "btn3_offset", "btn3_on", "btn3_off", t("gui.magneticraft.inserter.btn3"), t("gui.magneticraft.inserter.btn3_off"))
            switchButton("btn4", "btn4_offset", "btn4_on", "btn4_off", t("gui.magneticraft.inserter.btn4"), t("gui.magneticraft.inserter.btn4_off"))
            switchButton("btn5", "btn5_offset", "btn5_on", "btn5_off", t("gui.magneticraft.inserter.btn5"), t("gui.magneticraft.inserter.btn5_off"))
        }
    }
}

fun GuiBuilder.hydraulicPressGui(tile: TileHydraulicPress) {
    container {
        slot(tile.inventory, 0, "input_slot", SlotType.INPUT)
        slot(tile.inventory, 1, "output_slot", SlotType.OUTPUT, blockInput = true)
        region(0, 1, filter = { it, _ -> HydraulicPressRecipeManager.findRecipe(it, tile.hydraulicPressModule.mode) != null })
        region(1, 1, filter = { _, _ -> false })
        playerInventory()

        receiveDataFromClient { data ->
            data.getInteger(0) {
                tile.hydraulicPressModule.mode = HydraulicPressMode.values()[it]
            }
        }

        onClick("btn0_0") { sendToServer(IBD().setInteger(0, 0)) }
        onClick("btn0_1") { sendToServer(IBD().setInteger(0, 1)) }
        onClick("btn0_2") { sendToServer(IBD().setInteger(0, 2)) }

        selectButtonState("btn0") {
            tile.hydraulicPressModule.mode.ordinal
        }
    }

    bars {
        electricBar(tile.node)
        storageBar(tile.storageModule)
        consumptionBar(tile.processModule.consumption, Config.hydraulicPressMaxConsumption)
        progressBar(tile.processModule.timedProcess)

        drawable(Vec2d.ZERO, "arrow_offset", "arrow_size", "arrow_uv")
        slotSpacer()
        selectButton(vec2Of(18, 68), "btn0") {
            option("opt0_offset", "opt0_background", t("gui.magneticraft.hydraulic_press.opt0"))
            option("opt1_offset", "opt1_background", t("gui.magneticraft.hydraulic_press.opt1"))
            option("opt2_offset", "opt2_background", t("gui.magneticraft.hydraulic_press.opt2"))
        }
    }
}

fun GuiBuilder.shelvingUnitGui(@Suppress("UNUSED_PARAMETER") tile: TileShelvingUnit) {
    containerClass = ::ContainerShelvingUnit

    container {
        onClick("btn1") { sendToServer(IBD().setBoolean(DATA_ID_SHELVING_UNIT_SORT, true)) }
        onClick("btn0_0") { (container as ContainerShelvingUnit).levelButton(0) }
        onClick("btn0_1") { (container as ContainerShelvingUnit).levelButton(1) }
        onClick("btn0_2") { (container as ContainerShelvingUnit).levelButton(2) }

        selectButtonState("btn0") {
            (container as ContainerShelvingUnit).currentLevel.ordinal
        }
    }

    components {
        searchBar("search0", "search_pos")
        scrollBar("scroll0", "scroll_pos", 19)
        custom { ComponentShelvingUnit() }

        clickButton("btn1", "button_offset")
        drawable("button_icon_pos", "button_icon_size", "button_icon_uv")
        selectButton("btn0") {
            option("opt0_offset", "opt0_background", t("gui.magneticraft.shelving_unit.opt0"))
            option("opt1_offset", "opt1_background", t("gui.magneticraft.shelving_unit.opt1"))
            option("opt2_offset", "opt2_background", t("gui.magneticraft.shelving_unit.opt2"))
        }
    }
}

fun GuiBuilder.rfTransformerGui(tile: TileRfTransformer) {
    container {
        playerInventory()
    }

    bars {
        electricBar(tile.node)
        rfBar(tile.storage)
    }
}

fun GuiBuilder.electricEngineGui(tile: TileElectricEngine) {
    container {
        playerInventory()
    }

    bars {
        electricBar(tile.node)
        rfBar(tile.storage)
    }
}

fun GuiBuilder.airlockGui(tile: TileAirLock) {
    container {
        playerInventory()
    }

    bars {
        electricBar(tile.node)
    }
}

fun GuiBuilder.smallTankGui(tile: TileSmallTank) {
    container {
        playerInventory()
    }

    bars {
        tank(tile.tank, TankIO.INOUT)
    }
}

fun GuiBuilder.teslaTowerGui(tile: TileTeslaTower) {
    container {
        playerInventory()
    }

    bars {
        electricBar(tile.node)
    }
}

fun GuiBuilder.solarPanelGui(tile: TileSolarPanel) {
    container {
        playerInventory()
    }

    bars {
        electricBar(tile.node)
    }
}

fun GuiBuilder.computerGui(tile: TileComputer) {
    containerClass = ::ContainerComputer
    val motherboard = tile.computerModule.motherboard

    container {
        onClick("btn0") { sendToServer(IBD().setInteger(DATA_ID_COMPUTER_BUTTON, 0)) }
    }

    components {
        custom { MonitorComponent(tile.ref, tile.monitor, tile.keyboard) }
        clickButton("btn0", "button_offset")
        light("button_icon_offset", "button_icon_size", "button_icon_on_uv") { motherboard.isOnline }
        light("button_icon_offset", "button_icon_size", "button_icon_off_uv") { !motherboard.isOnline }
    }
}

fun GuiBuilder.miningRobotGui(tile: TileMiningRobot) {
    containerClass = ::ContainerMiningRobot
    val motherboard = tile.computerModule.motherboard

    container {
        onClick("btn0") { sendToServer(IBD().setInteger(DATA_ID_COMPUTER_BUTTON, 0)) }
    }

    components {
        custom { MonitorComponent(tile.ref, tile.monitor, tile.keyboard) }
        clickButton("btn0", "button_offset")
        light("button_icon_offset", "button_icon_size", "button_icon_on_uv") { motherboard.isOnline }
        light("button_icon_offset", "button_icon_size", "button_icon_off_uv") { !motherboard.isOnline }
        electricBar("electric_bar_offset", tile.node)
        storageBar("storage_bar_offset", tile.energyStorage)
    }
}