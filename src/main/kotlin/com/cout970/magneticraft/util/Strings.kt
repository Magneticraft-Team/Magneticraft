package com.cout970.magneticraft.util

import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.client.Minecraft

/**
 * Created by cout970 on 08/07/2016.
 */

infix fun String.centeredAt(pos: Vec2d) = box.centeredAt(pos)

fun String.centeredAt(x: Int, y: Int) = centeredAt(Vec2d(x, y))

val String.box: Vec2d get() =  Minecraft.getMinecraft().fontRendererObj.run {
        Vec2d(getStringWidth(this@box), FONT_HEIGHT)
    }