package com.cout970.magneticraft.registry

import com.cout970.magneticraft.block.BlockCoke
import com.cout970.magneticraft.block.decoration.BlockFiberboard
import com.cout970.magneticraft.block.decoration.BlockWoodChip
import com.cout970.magneticraft.fuel.FuelHandler
import com.cout970.magneticraft.fuel.IFuel
import com.cout970.magneticraft.item.ItemCoke
import com.cout970.magneticraft.item.ItemCrushedCoal
import com.cout970.magneticraft.item.ItemPebblesCoal
import com.cout970.magneticraft.item.ItemWoodChip
import net.minecraftforge.fml.common.registry.GameRegistry

val fuels = listOf<IFuel<*>>(
        ItemCoke,
        BlockCoke,
        ItemCrushedCoal,
        ItemPebblesCoal,
        ItemWoodChip,
        BlockWoodChip,
        BlockFiberboard
)

fun registerFuelHandler() {
    GameRegistry.registerFuelHandler(FuelHandler(fuels))
}