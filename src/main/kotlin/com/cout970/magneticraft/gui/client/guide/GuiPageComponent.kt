package com.cout970.magneticraft.gui.client.guide

import com.cout970.magneticraft.gui.Coords

interface GuiPageComponent {
    val position: Coords
    val size: Coords
    fun initGui()
    fun isMouseInside(mouse: Coords): Boolean
    fun onLeftClick(mouse: Coords): Boolean
    fun draw(mouse: Coords, time: Double)
    fun postDraw(mouse: Coords, time: Double)
}