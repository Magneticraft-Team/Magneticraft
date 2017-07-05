package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.misc.gui.Box
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.util.ResourceLocation

/**
 * Created by cout970 on 08/07/2016.
 */
class CompBackground(
        tex: String,
        val texture: ResourceLocation = resource("textures/gui/$tex.png"),
        val textureSize: Vec2d = Vec2d(256, 256),
        size: Vec2d = Vec2d(176, 166)
) : IComponent {

    override val box: Box = Vec2d.ZERO to size
    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(texture)
        gui.drawScaledTexture(gui.box, box.start, box.end, textureSize)
    }
}