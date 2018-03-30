package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.util.ResourceLocation

/**
 * Created by cout970 on 08/07/2016.
 */
class CompBackground(
        val texture: ResourceLocation,
        val textureSize: Vec2d = Vec2d(256, 256),
        override val size: Vec2d = Vec2d(176, 166)
) : IComponent {

    override val pos: IVector2 = Vec2d.ZERO

    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(texture)
        gui.drawTexture(DrawableBox(gui.pos, size, Vec2d.ZERO, size, textureSize))
    }
}