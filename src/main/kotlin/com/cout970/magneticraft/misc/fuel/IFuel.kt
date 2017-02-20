package com.cout970.magneticraft.misc.fuel

import net.minecraftforge.fml.common.registry.IForgeRegistryEntry

interface IFuel<T> : IForgeRegistryEntry<T> {

    fun getBurnTime(): Int

}