package com.cout970.magneticraft.misc.fuel

import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.IFuelHandler

class FuelHandler(val fuels: List<IFuel<*>>) : IFuelHandler {

    override fun getBurnTime(fuel: ItemStack?): Int {
        fuel?.let {
            val name = it.item.registryName

            fuels.firstOrNull { it.registryName == name }?.let {
                return it.getBurnTime()
            }
        }

        return 0
    }
}