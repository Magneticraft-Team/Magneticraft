package com.cout970.magneticraft.gui.client.components.bars

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.tileentity.modules.ModuleInternalStorage
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.contains
import com.cout970.magneticraft.util.vector.vec2Of
import net.minecraft.util.ResourceLocation
import java.text.DecimalFormat

/**
 * Created by cout970 on 2017/08/24.
 */

class CompStorageBar(
        val storageModule: ModuleInternalStorage,
        override val pos: IVector2,
        val tex: IVector2,
        val texture: ResourceLocation,
        val textureSize: IVector2 = vec2Of(256)
) : IComponent {


    override val size: IVector2 = vec2Of(11, 48)
    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(texture)
        val level = (storageModule.energy * size.yi / storageModule.capacity.toFloat()).toInt()
        gui.drawTexture(DrawableBox(
                screen = gui.pos + pos + vec2Of(0, size.yi - level) to vec2Of(size.x, level),
                texture = tex + vec2Of(0, size.yi - level) to vec2Of(size.x, level),
                textureSize = textureSize
        ))
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in (gui.pos + pos to size)) {
            val numberFormat = DecimalFormat("#,###")
            gui.drawHoveringText(listOf("${numberFormat.format(storageModule.energy)}J"), mouse)
        }
    }
}