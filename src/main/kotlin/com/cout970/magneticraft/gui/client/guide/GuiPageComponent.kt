package com.cout970.magneticraft.gui.client.guide

import com.cout970.magneticraft.util.vector.Vec2d

interface GuiPageComponent {
    val position: Vec2d
    val size: Vec2d
    fun initGui()
    fun isMouseInside(mouse: Vec2d): Boolean
    fun onLeftClick(mouse: Vec2d): Boolean
    fun draw(mouse: Vec2d, time: Double)
    fun postDraw(mouse: Vec2d, time: Double)
}