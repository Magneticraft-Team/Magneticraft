package com.cout970.magneticraft.integration.tinkersconstruct

import com.cout970.magneticraft.block.Ores
import com.cout970.magneticraft.item.EnumMetal
import com.cout970.magneticraft.util.info
import net.minecraftforge.fluids.FluidRegistry
import slimeknights.tconstruct.library.TinkerRegistry.registerMelting


object TinkersConstruct {

    fun registerOres() {
        val ingotAmount = 144

        info("Registering chunk and rocky chunk smelting recipes into Tinkers construct")
        EnumMetal.values().forEach {
            val fluid = FluidRegistry.getFluid(it.name.toLowerCase()) ?: run {
                info("Ignoring ${it.name.toLowerCase().capitalize()}, no fluid found with name '${it.name.toLowerCase()}'")
                return@forEach
            }
            registerMelting(it.getChunk(), fluid, ingotAmount * 2)
            registerMelting(it.getRockyChunk(), fluid, ingotAmount * 2)
            registerMelting(it.getLightPlate(), fluid, ingotAmount)
            registerMelting(it.getHeavyPlate(), fluid, ingotAmount * 2)
        }

        info("Registering galena ores smelting recipe into Tinkers construct")
        registerMelting(Ores.OreType.LEAD.stack(1), FluidRegistry.getFluid("silver"), ingotAmount * 2)
    }
}