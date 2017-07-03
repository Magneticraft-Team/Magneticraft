package com.cout970.magneticraft.integration

import net.minecraftforge.fml.common.Loader

/**
 * Created by cout970 on 22/07/2016.
 * Allows compatibility with other mods like JEI.
 */
object IntegrationHandler {

    var JEI = false

    fun preInit(){
        // jei automatically loads MagneticraftPlugin because has @JEIPlugin
        JEI = Loader.isModLoaded("jei")
    }
}
