package com.cout970.magneticraft.gui.client.components.bars

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.tileentity.modules.ModuleInternalStorage
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.contains
import com.cout970.magneticraft.util.vector.vec2Of
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
        gui.bindTexture(texture)
        val level = (storageModule.energy * 48 / storageModule.capacity.toFloat()).toInt()
        gui.drawTexture(DrawableBox(
                screen = pos + vec2Of(0, 48 - level) to vec2Of(size.x, level),
                texture = vec2Of(0, 166 + 48 - level) to vec2Of(size.x, level),
                textureSize = vec2Of(256)
        ))
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in (pos to size)) {
            val numberFormat = DecimalFormat("#,###")
            gui.drawHoveringText(listOf("${numberFormat.format(storageModule.energy)}J"), mouse)
        }
    }
}