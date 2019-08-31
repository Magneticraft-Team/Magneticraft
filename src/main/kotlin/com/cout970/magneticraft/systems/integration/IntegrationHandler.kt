package com.cout970.magneticraft.systems.integration

import com.blamejared.mtlib.helpers.InputHelper
import com.cout970.magneticraft.api.internal.registries.fuel.FluidFuelManager
import com.cout970.magneticraft.misc.info
import com.cout970.magneticraft.systems.integration.buildcraft.BuildcraftFuelManager
import com.cout970.magneticraft.systems.integration.crafttweaker.CraftTweakerPlugin
import com.cout970.magneticraft.systems.integration.tinkersconstruct.TinkersConstruct
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.ModAPIManager

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

    fun preInit() {
        // jei automatically loads MagneticraftPlugin because has @JEIPlugin
        jei = Loader.isModLoaded("jei")
        // also auto-loads classes with @ZenRegister
        craftTweaker = Loader.isModLoaded("crafttweaker")
        tconstruct = Loader.isModLoaded("tconstruct")
        buildcraftApi = ModAPIManager.INSTANCE.hasAPI("buildcraftapi_fuels")
        industrialForegoing = Loader.isModLoaded("industrialforegoing")

        if (buildcraftApi) {
            FluidFuelManager.FLUID_FUEL_MANAGER = BuildcraftFuelManager()
        }
    }

    fun init() {
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
            InputHelper::class.java
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
