package com.cout970.magneticraft.gui.client.components.bars

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.tileentity.modules.ModuleInternalStorage
import com.cout970.magneticraft.util.guiTexture
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.contains
import com.cout970.magneticraft.util.vector.vec2Of
import net.minecraft.client.renderer.GlStateManager
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
                gui.pos + pos + vec2Of(0, size.yi - level),
                vec2Of(size.x, level),
                tex + vec2Of(0, size.yi - level),
                vec2Of(size.x, level),
                textureSize
        ))
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in (gui.pos + pos to size)) {
            val numberFormat = DecimalFormat("#,###")
            gui.drawHoveringText(listOf("${numberFormat.format(storageModule.energy)}J"), mouse)
        }
    }
}

class CompStorageBar2(
        val storageModule: ModuleInternalStorage,
        override val pos: IVector2
) : IComponent {

    override val size: IVector2 = vec2Of(13, 50)
    override lateinit var gui: IGui

    lateinit var back: DrawableBox
    lateinit var color: DrawableBox

    override fun init() {
        back = DrawableBox(
                screenPos = gui.pos + pos,
                screenSize = vec2Of(13, 50),
                texturePos = vec2Of(10, 61),
                textureSize = vec2Of(13, 50),
                textureScale = vec2Of(256)
        )
        color = DrawableBox(
                screenPos = gui.pos + pos + vec2Of(1),
                screenSize = vec2Of(11, 48),
                texturePos = vec2Of(24, 61),
                textureSize = vec2Of(11, 48),
                textureScale = vec2Of(256)
        )
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        val level = (storageModule.energy * 48f / storageModule.capacity).toInt()
        val front = DrawableBox(
                screenPos = gui.pos + pos + vec2Of(1, 1 + 48 - level),
                screenSize = vec2Of(11, level),
                texturePos = vec2Of(24, 61 + 48 - level),
                textureSize = vec2Of(11, level),
                textureScale = vec2Of(256)
        )

        gui.bindTexture(guiTexture("misc"))
        gui.drawTexture(back)

        GlStateManager.disableAlpha()
        GlStateManager.color(1f, 1f, 1f, 0.2f)
        gui.drawTexture(color)

        GlStateManager.color(1f, 1f, 1f, 1f)
        gui.drawTexture(front)
        GlStateManager.enableAlpha()
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in (gui.pos + pos to size)) {
            val numberFormat = DecimalFormat("#,###")
            gui.drawHoveringText(listOf("${numberFormat.format(storageModule.energy)}J"), mouse)
        }
    }
}