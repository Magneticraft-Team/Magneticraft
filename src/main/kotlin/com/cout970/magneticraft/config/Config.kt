package com.cout970.magneticraft.config

/**
 * Created by cout970 on 16/05/2016.
 */

const val CATEGORY_ORES = CATEGORY_GENERAL + ".ores"
const val CATEGORY_ENERGY = CATEGORY_GENERAL + ".energy"

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

    @ConfigValue(category = CATEGORY_ENERGY, comment = "Incendiary Generator")
    var incendiaryGeneratorMaxProduction = 80

    @ConfigValue(category = CATEGORY_GENERAL, comment = "Unit of Heat, Celsius or Fahrenheit")
    var heatUnitCelsius = true
}