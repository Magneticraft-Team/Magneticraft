package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.misc.gui.Box
import com.cout970.magneticraft.tileentity.modules.ModuleInternalStorage
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 2017/07/01.
 */

class StorageBar(val parent: GuiBase, val storageModule: ModuleInternalStorage) : IComponent {

    override val box: Box = Box(Vec2d(69, 16) + parent.box.start, Vec2d(16, 48))
    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(MISC_TEXTURES)
        val level = (storageModule.energy * 48 / storageModule.capacity.toFloat()).toInt()
        gui.drawScaledTexture(
                this.box.pos.copy(y = this.box.pos.y + 48 - level.toDouble()),
                this.box.size.copy(y = level.toDouble()), Vec2d.ZERO + Vec2d(0, 48 - level),
                Vec2d(64, 64))
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        if (mouse in box) {
            gui.drawHoveringText(listOf("${storageModule.energy}J"), mouse)
        }
    }
}