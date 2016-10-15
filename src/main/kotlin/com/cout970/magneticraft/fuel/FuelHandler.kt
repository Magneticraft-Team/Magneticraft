package com.cout970.magneticraft.fuel

import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.IFuelHandler

class FuelHandler(val providers: List<FuelProvider<*>>) : IFuelHandler {
    override fun getBurnTime(fuel: ItemStack?): Int {
        fuel?.let {
            val name = it.item.registryName

            providers.firstOrNull { it.registryName == name }?.let {
                return it.getBurnTime()
            }
        }

        return 0
    }
}