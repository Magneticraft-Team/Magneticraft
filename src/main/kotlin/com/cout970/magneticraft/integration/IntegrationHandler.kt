package com.cout970.magneticraft.integration

import com.cout970.magneticraft.integration.crafttweaker.CraftTweakerPlugin
import net.minecraftforge.fml.common.Loader

/**
 * Created by cout970 on 22/07/2016.
 * Allows compatibility with other mods like JEI.
 */
object IntegrationHandler {

    var jei = false
    var craftTweaker = false
    var tconstruct = false

    fun preInit(){
        // jei automatically loads MagneticraftPlugin because has @JEIPlugin
        jei = Loader.isModLoaded("jei")
        // also auto-loads classes with @ZenRegister
        craftTweaker = Loader.isModLoaded("crafttweaker")

        tconstruct = Loader.isModLoaded("tconstruct")
    }

    fun init(){
        CraftTweakerPlugin.runActions()
    }
}
