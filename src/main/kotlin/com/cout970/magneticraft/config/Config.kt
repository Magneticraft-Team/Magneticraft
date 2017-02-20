package com.cout970.magneticraft.config

import com.cout970.magneticraft.block.decoration.BlockWoodChip
import com.cout970.magneticraft.block.fuel.BlockCoke
import com.cout970.magneticraft.item.ItemCoke
import com.cout970.magneticraft.item.ItemCrushedCoal
import com.cout970.magneticraft.item.ItemPebblesCoal
import com.cout970.magneticraft.item.ItemWoodChip
import com.cout970.magneticraft.util.toKelvinFromCelsius
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 16/05/2016.
 */

const val CATEGORY_GENERAL = "general"
const val CATEGORY_ORES = CATEGORY_GENERAL + ".ores"
const val CATEGORY_ENERGY = CATEGORY_GENERAL + ".energy"
const val CATEGORY_PC = CATEGORY_GENERAL + ".pc"
const val CATEGORY_FUEL = CATEGORY_GENERAL + ".fuel"
const val CATEGORY_HEAT = CATEGORY_GENERAL + ".heat"

object Config {

    @ConfigValue(category = CATEGORY_ORES, comment = "Copper ore")
    var copperOre = OreConfig(10, 8, 80, 30)

    @ConfigValue(category = CATEGORY_ORES, comment = "Lead ore")
    var leadOre = OreConfig(9, 8, 80, 30)

    @ConfigValue(category = CATEGORY_ORES, comment = "Cobalt ore")
    var cobaltOre = OreConfig(8, 8, 80, 30)

    @ConfigValue(category = CATEGORY_ORES, comment = "Tungsten ore")
    var tungstenOre = OreConfig(7, 8, 80, 30)

    @ConfigValue(category = CATEGORY_ORES, comment = "Limestone")
    var limestone = GaussOreConfig(0, 5, 0.9f, 3, 50, 64, 16)

    // TODO implement this
    @ConfigValue(category = CATEGORY_GENERAL, comment = "Set players on fire when processing blaze" +
                                                        " rods in the crushing table")
    var crushingTableCausesFire = true

    @ConfigValue(category = CATEGORY_GENERAL, comment = "Unit of Heat, Celsius or Fahrenheit")
    var heatUnitCelsius = true

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Conversion ratio between Watts and Tesla, " +
                                                       "NOTE: all the values in the config about energy are in Watts")
    var wattsToTesla = 1.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Incendiary Generator Energy Production")
    var incendiaryGeneratorMaxProduction = 40.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Electric Heater Max Consumption")
    var electricHeaterMaxConsumption = 20.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Electric Furnace Max Consumption")
    var electricFurnaceMaxConsumption = 20.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Airlock: maintenance cost per Air Bubble every " +
                                                       "40 ticks (2 sec)")
    var airlockBubbleCost = 1.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Airlock: cost of removing a water block")
    var airlockAirCost = 2.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Hydraulic Press Consumption")
    var hydraulicPressConsumption = 40.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Grinder Consumption")
    var grinderConsumption = 40.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Grinder Consumption")
    var sifterConsumption = 10.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Solar Panel Production")
    var solarPanelMaxProduction = 10.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Item Battery Capacity")
    var itemBatteryCapacity: Double = 250000.0

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Battery Block Capacity")
    var blockBatteryCapacity: Int = 1000000

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Battery Block Item charge and discharge " +
                                                       "speed in Joules/tick (Watts)")
    var blockBatteryTransferRate: Int = 500

    @ConfigValue(category = CATEGORY_PC, comment = "Allow TCP connections in PCs")
    var allowTcpConnections: Boolean = true

    @ConfigValue(category = CATEGORY_PC, comment = "Max TCP connections in all PCs")
    var maxTcpConnections: Int = 8

    @ConfigValue(category = CATEGORY_FUEL, comment = "Coke burn time.")
    var cokeBurnTime: Int = 3200

    @ConfigValue(category = CATEGORY_FUEL, comment = "Default maximum fuel temperature in Kelvin")
    var defaultMaxTemp: Double = 400.toKelvinFromCelsius()

    @ConfigValue(category = CATEGORY_FUEL, comment = "Default machine max temperature in Kelvin")
    var defaultMachineMaxTemp: Double = 125.0.toKelvinFromCelsius()

    @ConfigValue(category = CATEGORY_FUEL, comment = "Default machine safe temperature in Kelvin")
    var defaultMachineSafeTemp: Double = 50.0.toKelvinFromCelsius()

    @ConfigValue(category = CATEGORY_FUEL, comment = "List of fuels and associated maximum temperatures " +
                                                     "which have higher maximum temperatures than the default.")
    var fuelTemps: FuelConfig = FuelConfig(mapOf(
            ItemStack(BlockWoodChip).item to 450.0.toKelvinFromCelsius(),
            ItemWoodChip to 450.0.toKelvinFromCelsius(),
            Items.COAL to 500.0.toKelvinFromCelsius(),
            ItemStack(Blocks.COAL_BLOCK).item to 500.0.toKelvinFromCelsius(),
            ItemCrushedCoal to 550.0.toKelvinFromCelsius(),
            ItemPebblesCoal to 550.0.toKelvinFromCelsius(),
            ItemCoke to 600.0.toKelvinFromCelsius(),
            ItemStack(BlockCoke).item to 600.0.toKelvinFromCelsius(),
            Items.LAVA_BUCKET to 800.0.toKelvinFromCelsius(),
            Items.BLAZE_ROD to 1800.0.toKelvinFromCelsius()
    ))

    @ConfigValue(category = CATEGORY_HEAT, comment = "Firebox Production")
    var fireboxMaxProduction = 40.0

    @ConfigValue(category = CATEGORY_HEAT, comment = "Icebox Consumption")
    var iceboxMaxConsumption = 20.0

    @ConfigValue(category = CATEGORY_FUEL, comment = "Crushed coal burn time.")
    var crushedCoalBurnTime: Int = 1700

    @ConfigValue(category = CATEGORY_FUEL, comment = "Coal pebbles burn time.")
    var coalPebbleBurnTime: Int = 1800

    @ConfigValue(category = CATEGORY_FUEL, comment = "Coal pebbles burn time.")
    var woodChipBurnTime: Int = 40

    @ConfigValue(category = CATEGORY_FUEL, comment = "Coal pebbles burn time.")
    var fiberboardBurnTime: Int = 150
}