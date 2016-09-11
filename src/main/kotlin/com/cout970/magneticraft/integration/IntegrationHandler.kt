package com.cout970.magneticraft.integration

import net.minecraftforge.fml.common.Loader

/**
 * Created by cout970 on 22/07/2016.
 */
object IntegrationHandler {

    var JEI = false
    var TESLA = false

    fun preInit(){
        JEI = Loader.isModLoaded("JEI")
        TESLA = Loader.isModLoaded("tesla")
    }
}