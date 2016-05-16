package com.cout970.magneticraft.config

/**
 * Created by cout970 on 16/05/2016.
 */

const val CATEGORY_ORES = CATEGORY_GENERAL+".ores"

object Config {

//    @ConfigValue(comment = "Test value comment")
//    var testString : String = "undefault value"
//
//    @ConfigValue(comment = "Test value comment")
//    var testInt : Int = 26
//
//    @ConfigValue(comment = "Test value comment")
//    var testFloat : Float = 25f
//
//    @ConfigValue(comment = "Test value comment")
//    var testDouble : Double = 24.0
//
//    @ConfigValue(comment = "Test value comment")
//    var testBoolean : Boolean = true

    @ConfigValue(category = CATEGORY_ORES, comment = "Copper ore")
    var copperOre : OreConfig = OreConfig(10, 8, 80, 30)

    @ConfigValue(category = CATEGORY_ORES, comment = "Lead ore")
    var leadOre : OreConfig = OreConfig(9, 8, 80, 30)

    @ConfigValue(category = CATEGORY_ORES, comment = "Cobalt ore")
    var cobaltOre : OreConfig = OreConfig(8, 8, 80, 30)

    @ConfigValue(category = CATEGORY_ORES, comment = "Tungsten ore")
    var tungstenOre : OreConfig = OreConfig(7, 8, 80, 30)
}