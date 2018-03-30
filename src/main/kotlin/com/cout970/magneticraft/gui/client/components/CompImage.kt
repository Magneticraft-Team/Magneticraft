package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.util.ResourceLocation

/**
 * Created by cout970 on 2017/08/03.
 */
class CompImage(val texture: ResourceLocation, val box: DrawableBox) : IComponent {

    override val pos: IVector2 = box.screenPos
    override val size: IVector2 = box.screenSize
    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(texture)
//        gui.drawColor(gui.pos + box.screen.pos to box.screen.size, 0xFF00FF00.toInt())
        gui.drawTexture(box.offset(gui.pos))
    }
}