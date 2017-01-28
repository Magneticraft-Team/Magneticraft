package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.util.FuelCache
import com.cout970.magneticraft.util.toCelsius
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * Created by Yurgen on 30/11/2016.
 */

class TooltipHandler {
    val fuelCache = FuelCache()

    @SubscribeEvent
    fun onTooltip(event: ItemTooltipEvent) {
        val stack = event.itemStack
        if (TileEntityFurnace.getItemBurnTime(stack) == 0) return
        event.toolTip.add(String.format("Max Temp: %.1fC", fuelCache.getOrChange(stack).toCelsius()))
    }
}