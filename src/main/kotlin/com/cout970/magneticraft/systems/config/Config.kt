package com.cout970.magneticraft.systems.config

/**
 * Created by cout970 on 16/05/2016.
 */

const val CATEGORY_GENERAL = "general"
const val CATEGORY_ORES = "$CATEGORY_GENERAL.ores"
const val CATEGORY_ENERGY = "$CATEGORY_GENERAL.energy"
const val CATEGORY_GUI = "$CATEGORY_GENERAL.gui"
const val CATEGORY_PC = "$CATEGORY_GENERAL.pc"
const val CATEGORY_MACHINES = "$CATEGORY_GENERAL.machines"

object Config {

    @ConfigValue(category = CATEGORY_ORES, comment = "Copper ore")
    var copperOre = OreConfig(11, 8, 70, 10)

    @ConfigValue(category = CATEGORY_ORES, comment = "Lead ore")
    var leadOre = OreConfig(10, 8, 80, 2)

    @ConfigValue(category = CATEGORY_ORES, comment = "Tungsten ore")
    var tungstenOre = OreConfig(8, 8, 60, 20)

    @ConfigValue(category = CATEGORY_ORES, comment = "Pyrite ore")
    var pyriteOre = OreConfig(9, 9, 100, 30)

    @ConfigValue(category = CATEGORY_ORES, comment = "Limestone")
    var limestone = GaussOreConfig(0, 5, 0.9f, 3, 32, 64, 16)

    @ConfigValue(category = CATEGORY_ORES, comment = "Oil source")
    var oil = OilGenConfig(1 / 50f, 10, true)

    @ConfigValue(category = CATEGORY_GENERAL, comment = "Set players on fire when processing blaze" +
            " rods in the crushing table")
    var crushingTableCausesFire = true

    @ConfigValue(category = CATEGORY_GENERAL, comment = "Automatically focus the search bar in the shelving unit when you enter the GUI")
    var autoSelectSearchBar = true

    @ConfigValue(category = CATEGORY_GUI, comment = "Unit of Heat to display, Celsius or Fahrenheit")
    var heatUnitCelsius = true

    @ConfigValue(category = CATEGORY_GUI, comment = "Character used to separate number like , in 1,000,000. Only one character will be used")
    var thousandsSeparator: String = ","

    @ConfigValue(category = CATEGORY_GUI, comment = "Allow players to use the gui of the combustion generator")
    var allowCombustionChamberGui = true

    @ConfigValue(category = CATEGORY_GUI, comment = "Scale of the gui with respect of the background image")
    var guideBookScale: Double = 1.5

    @ConfigValue(category = CATEGORY_GUI, comment = "When you search something in the shelving unit the JEI search bar will update with the same search text")
    var syncShelvingUnitSearchWithJei: Boolean = false

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Conversion ratio between Watts and Forge Energy, " +
            "NOTE: all the values in the config about energy are in Watts")
    var wattsToFE = 1.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Conversion speed for the RF transformer in RF/t")
    var rfConversionSpeed = 100

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Conversion speed for the electric engine in RF/t")
    var electricEngineSpeed = 1000

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Max energy transmitted by the tesla tower per player/receiver (in joules), warning: if you put a high value the the machine may get negative voltages")
    var teslaTowerTransmissionRate = 500.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Electric Heater Max Production (in joules)")
    var electricHeaterMaxProduction = 80.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Combustion chamber max speed in Fuel per tick")
    var combustionChamberMaxSpeed = 4.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Big combustion chamber max speed in Fuel per tick")
    var bigCombustionChamberMaxSpeed = 40.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Electric Furnace Max Consumption")
    var electricFurnaceMaxConsumption = 20.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Big Electric Furnace Max Consumption")
    var bigElectricFurnaceMaxConsumption = 200.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Oil Heater Max Consumption")
    var oilHeaterMaxConsumption = 120.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Refinery Max Consumption in Steam mB")
    var refineryMaxConsumption = 20.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Gasification Unit Max Consumption")
    var gasificationUnitConsumption = 40.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Small boiler max steam production in mB")
    var boilerMaxProduction = 20

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Multiblock boiler max steam production in mB")
    var multiblockBoilerMaxProduction = 600

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Steam engine max production in W (J/t)")
    var steamEngineMaxProduction = 240

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Steam turbine max production in W (J/t)")
    var steamTurbineMaxProduction = 1200

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Airlock: maintenance cost per Air Bubble every " +
            "40 ticks (2 sec)")
    var airlockBubbleCost = 1.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Airlock: cost of removing a water block")
    var airlockAirCost = 2.0

    @ConfigValue(category = CATEGORY_ORES, comment = "Oil per stage in a oil source block, every block has 10 stages")
    var oilPerStage = 1000

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Hydraulic Press Max Consumption")
    var hydraulicPressMaxConsumption = 60.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Grinder Max Consumption")
    var grinderMaxConsumption = 40.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Sieve Max Consumption")
    var sieveMaxConsumption = 40.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Solar Panel Production")
    var solarPanelMaxProduction = 100.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Wind Turbine Production")
    var windTurbineMaxProduction = 200.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Thermopile production (approximated)")
    var thermopileProduction = 20.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Solar power heat generation per tick (in Joules)")
    var solarMirrorHeatProduction = 16

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Pumpjack Max Consumption")
    var pumpjackConsumption = 80.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Pumpjack Scan tries per tick, every scan checks 8 blocks")
    var pumpjackScanSpeed = 80

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Capacity of electric tools")
    var electricToolCapacity: Int = 512_000

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Energy used per attack with this tool")
    var electricToolAttackConsumption: Int = 2000

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Energy used per block mined with this tool")
    var electricToolBreakConsumption: Int = 1000

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Energy used per jump")
    var electricToolPistonConsumption: Int = 4000

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Item Low Battery Capacity")
    var batteryItemLowCapacity: Int = 250_000

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Item Medium Battery Capacity")
    var batteryItemMediumCapacity: Int = 2_500_000

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Battery Block Capacity")
    var blockBatteryCapacity: Int = 1_000_000

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Battery Block Item charge and discharge " +
            "speed in Joules/tick (Watts)")
    var blockBatteryTransferRate: Int = 500

    @ConfigValue(category = CATEGORY_MACHINES, comment = "Amount of water generated by a Water generator every tick")
    var waterGeneratorPerTickWater: Int = 20

    @ConfigValue(category = CATEGORY_MACHINES, comment = "Max amount of items that a container can drop when mined, it will delete the rest of items")
    var containerMaxItemDrops: Int = 128 * 64

    @ConfigValue(category = CATEGORY_MACHINES, comment = "Max amount of items that a container can store")
    var containerMaxItemStorage: Int = 1024 * 64

    @ConfigValue(category = CATEGORY_MACHINES, comment = "Enable/disable (1/0) particles in machine animations ")
    var enableMachineParticles: Int = 1

    @ConfigValue(category = CATEGORY_PC, comment = "Allow TCP connections in PCs")
    var allowTcpConnections: Boolean = true

    @ConfigValue(category = CATEGORY_PC, comment = "Max TCP connections in all PCs")
    var maxTcpConnections: Int = 8

    @ConfigValue(category = CATEGORY_PC, comment = "Energy consumed every tick by the mining robot")
    var miningRobotPassiveConsumption: Double = 1.0

    @ConfigValue(category = CATEGORY_PC, comment = "Color of text, valid values: 0 => amber 1, 1 => amber 2, 2 => white, 3 => green 1, 4 => apple 2, 5 => green 2, 6 => apple 2c, 7 => green 3, 8 => green 4")
    var computerTextColor: Int = 0
}
