package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.gui.client.IComponent
import com.cout970.magneticraft.gui.client.IGui
import com.cout970.magneticraft.misc.gui.Box
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.util.ResourceLocation

/**
 * Created by cout970 on 08/07/2016.
 */
class CompBackground(tex:String, val texture: ResourceLocation = resource("textures/gui/$tex.png"), val textureSize: Vec2d = Vec2d(256, 256), size: Vec2d = Vec2d(176, 166)) : IComponent {

    override val box: Box = Vec2d.ZERO to size
    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.run {
            bindTexture(texture)
            drawScaledTexture(gui.box, this@CompBackground.box.start, this@CompBackground.box.end, this@CompBackground.textureSize)
        }
    }
}