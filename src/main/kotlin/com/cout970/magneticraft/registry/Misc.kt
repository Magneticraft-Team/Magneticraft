package com.cout970.magneticraft.registry

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.api.internal.registries.tool.hammer.Hammer
import com.cout970.magneticraft.api.internal.registries.tool.hammer.HammerRegistry
import com.cout970.magneticraft.api.internal.registries.tool.wrench.WrenchRegistry
import com.cout970.magneticraft.item.ToolItems
import com.cout970.magneticraft.misc.inventory.stack
import net.minecraft.init.Items

fun registerMisc() {
    HammerRegistry.registerHammer(ToolItems.stoneHammer.stack(), Hammer(1, 8, 1))
    HammerRegistry.registerHammer(ToolItems.ironHammer.stack(), Hammer(2, 10, 1))
    HammerRegistry.registerHammer(ToolItems.steelHammer.stack(), Hammer(4, 15, 1))

    if(Debug.DEBUG){
        WrenchRegistry.registerWrench(Items.STICK.stack())
    }
    WrenchRegistry.registerWrench(ToolItems.wrench.stack())
}