package com.cout970.magneticraft.guide.builders

import com.cout970.magneticraft.gui.Coords
import com.cout970.magneticraft.guide.components.Image
import net.minecraft.util.ResourceLocation

class ImageBuilder {
    lateinit var position: Coords
    lateinit var size: Coords
    lateinit var location: String

    fun build() = Image(position, size, ResourceLocation(location))
}