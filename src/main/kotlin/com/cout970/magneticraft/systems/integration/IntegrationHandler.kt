package com.cout970.magneticraft.systems.integration

import com.cout970.magneticraft.misc.info
import com.cout970.magneticraft.systems.integration.crafttweaker.CraftTweakerPlugin
import com.cout970.magneticraft.systems.integration.tinkersconstruct.TinkersConstruct

/**
 * Created by cout970 on 22/07/2016.
 * Allows compatibility with other mods like JEI.
 */
object IntegrationHandler {

    var jei = false
    var craftTweaker = false
    var tconstruct = false
    var buildcraftApi = false
    var industrialForegoing = false

    fun init() {
        // jei automatically loads MagneticraftPlugin because has @JEIPlugin
        jei = isModLoaded("jei")
        // also auto-loads classes with @ZenRegister
        craftTweaker = isModLoaded("crafttweaker")
        tconstruct = isModLoaded("tconstruct")
        buildcraftApi = isModLoaded("buildcraftapi_fuels") // TODO search a better way to check apis
        industrialForegoing = isModLoaded("industrialforegoing")

        // TODO
//        if (buildcraftApi) {
//            FluidFuelManager.FLUID_FUEL_MANAGER = BuildcraftFuelManager()
//        }
    }

    // TODO
    fun isModLoaded(modid: String) = false

    fun postInit() {
        if (craftTweaker) craftTweaker()
        if (tconstruct) {
            info("Starting tinkers construct integration")
            try {
                TinkersConstruct.registerOres()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            info("Tinkers construct integration done")
        }
    }

    private fun craftTweaker() {
        info("Starting CraftTweaker integration")
        try {
            Class.forName("com.blamejared.mtlib.helpers.InputHelper")
        } catch (e: NoClassDefFoundError) {
            info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
            info("++!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!++")
            info("++!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! IMPORTANT NOTICE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!++")
            info("++!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!++")
            info("++ Unable to find InputHelper from MtLib, this lib is required in order to use the CraftTweaker API ++")
            info("++!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!++")
            info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
            return
        }
        try {
            CraftTweakerPlugin.runActions()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        info("CraftTweaker integration done")
    }
}
