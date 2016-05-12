package com.cout970.magneticraft.guide.builders

import com.cout970.magneticraft.gui.Coords
import com.cout970.magneticraft.guide.components.Icon
import net.minecraft.item.ItemStack

class IconBuilder {
    lateinit var position: Coords
    lateinit var stack: ItemStack

    fun build() = Icon(position, stack)
}