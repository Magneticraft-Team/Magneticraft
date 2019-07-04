package com.cout970.magneticraft.systems.gui.components.bars

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.contains
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.GuiBase
import com.cout970.magneticraft.systems.gui.render.DrawableBox
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import com.cout970.magneticraft.systems.tilemodules.ModuleInternalStorage
import net.minecraft.util.ResourceLocation
import java.text.DecimalFormat

/**
 * Created by cout970 on 2017/07/01.
 */

class BatteryStorageBar(val parent: GuiBase, val texture: ResourceLocation,
                        val storageModule: ModuleInternalStorage) : IComponent {

    override val pos: IVector2 = vec2Of(69, 16) + parent.pos
    override val size: IVector2 = vec2Of(16, 48)
    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        val level = (storageModule.energy * 48 / storageModule.capacity.toFloat()).toInt()

        gui.bindTexture(texture)
        gui.drawTexture(DrawableBox(pos + vec2Of(0, 48 - level), vec2Of(size.x, level),
            vec2Of(0, 166 + 48 - level)))
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in (pos to size)) {
            val numberFormat = DecimalFormat("#,###")
            gui.drawHoveringText(listOf("${numberFormat.format(storageModule.energy)}J"), mouse)
        }
    }
}