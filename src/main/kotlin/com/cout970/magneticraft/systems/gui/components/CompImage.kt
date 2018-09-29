package com.cout970.magneticraft.systems.gui.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.systems.gui.render.DrawableBox
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
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
        gui.drawTexture(box.offset(gui.pos))
    }
}