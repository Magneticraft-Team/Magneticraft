package com.cout970.magneticraft.fuel

import net.minecraftforge.fml.common.registry.IForgeRegistryEntry

interface FuelProvider<T> : IForgeRegistryEntry<T> {

    fun getBurnTime(): Int

}