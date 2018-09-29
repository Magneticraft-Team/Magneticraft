package com.cout970.magneticraft.registry

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.api.internal.registries.tool.hammer.Hammer
import com.cout970.magneticraft.api.internal.registries.tool.hammer.HammerRegistry
import com.cout970.magneticraft.api.internal.registries.tool.wrench.WrenchRegistry
import com.cout970.magneticraft.features.items.ToolItems
import com.cout970.magneticraft.misc.info
import com.cout970.magneticraft.misc.inventory.stack
import net.minecraft.init.Items
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries

fun registerMisc() {
    HammerRegistry.registerHammer(ToolItems.stoneHammer.stack(), Hammer(1, 8, 1))
    HammerRegistry.registerHammer(ToolItems.ironHammer.stack(), Hammer(2, 10, 1))
    HammerRegistry.registerHammer(ToolItems.steelHammer.stack(), Hammer(4, 15, 1))

    if (Debug.DEBUG) {
        WrenchRegistry.registerWrench(Items.STICK.stack())
    }
    WrenchRegistry.registerWrench(ToolItems.wrench.stack())
    registerWrench("bigreactors:wrench")
    registerWrench("pneumaticcraft:pneumatic_wrench")
    registerWrench("rftools:smartwrench")
    registerWrench("refinedstorage:wrench")
    registerWrench("thermalfoundation:wrench")
    registerWrench("immersiveengineering:tool")
    registerWrench("pneumaticcraft:logistics_configurator")
    registerWrench("mekanism:configurator")
    registerWrench("buildcraftcore:wrench")
    registerWrench("teslacorelib:wrench")
}

private fun registerWrench(resource: String, meta: Int = 0) {
    ForgeRegistries.ITEMS.getValue(ResourceLocation(resource))?.let {
        info("Adding item: $it as valid wrench")
        WrenchRegistry.registerWrench(it.stack(meta = meta))
    }
}