package com.cout970.magneticraft.registry

import com.cout970.magneticraft.block.BlockCoke
import com.cout970.magneticraft.fuel.FuelHandler
import com.cout970.magneticraft.fuel.IFuel
import com.cout970.magneticraft.item.ItemCoke
import net.minecraftforge.fml.common.registry.GameRegistry

val fuels = listOf<IFuel<*>>(
        ItemCoke,
        BlockCoke
)

fun registerFuelHandler() {
    GameRegistry.registerFuelHandler(FuelHandler(fuels))
}