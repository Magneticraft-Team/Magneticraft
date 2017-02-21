package com.cout970.magneticraft.integration

import net.minecraftforge.fml.common.Loader

/**
 * Created by cout970 on 22/07/2016.
 * Allows compatibility with other mods such as JEI or TESLA.
 */
object IntegrationHandler {

    var JEI = false
    var TESLA = false

    fun preInit(){
        JEI = Loader.isModLoaded("JEI") || Loader.isModLoaded("jei")
        TESLA = Loader.isModLoaded("tesla")
    }
}
