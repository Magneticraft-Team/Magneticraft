package com.cout970.magneticraft.systems.integration.tinkersconstruct

import com.cout970.magneticraft.features.items.EnumMetal
import com.cout970.magneticraft.features.ores.Blocks
import com.cout970.magneticraft.misc.info
import net.minecraftforge.fluids.FluidRegistry
import slimeknights.tconstruct.library.TinkerRegistry.registerMelting


object TinkersConstruct {

    fun registerOres() {
        val ingotAmount = 144
        val ingotAndAHalfAmount = 216

        info("Registering chunk and rocky chunk smelting recipes into Tinkers construct")
        EnumMetal.values().forEach {
            val fluid = FluidRegistry.getFluid(it.name.toLowerCase()) ?: run {
                info("Ignoring ${it.name.toLowerCase().capitalize()}, no fluid found with name '${it.name.toLowerCase()}'")
                return@forEach
            }
            registerMelting(it.getChunk(), fluid, ingotAmount * 2)
            registerMelting(it.getRockyChunk(), fluid, ingotAmount * 2)
            registerMelting(it.getLightPlate(), fluid, ingotAndAHalfAmount)
            registerMelting(it.getHeavyPlate(), fluid, ingotAmount * 4)
        }

        info("Registering galena ores smelting recipe into Tinkers construct")
        registerMelting(Blocks.OreType.LEAD.stack(1), FluidRegistry.getFluid("lead"), ingotAmount * 2)
    }
}