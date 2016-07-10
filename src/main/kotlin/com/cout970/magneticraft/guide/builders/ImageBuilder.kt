package com.cout970.magneticraft.guide.builders

import com.cout970.magneticraft.guide.components.Image
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.util.ResourceLocation

class ImageBuilder {
    lateinit var position: Vec2d
    lateinit var size: Vec2d
    lateinit var location: String

    fun build() = Image(position, size, ResourceLocation(location))
}