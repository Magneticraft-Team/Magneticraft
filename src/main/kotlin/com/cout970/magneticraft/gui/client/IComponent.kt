package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.misc.gui.Box
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 20/05/2016.
 */
interface IComponent {

    val box: Box

    var gui: IGui

    fun drawFirstLayer(mouse: Vec2d, partialTicks: Float)

    fun drawSecondLayer(mouse: Vec2d) = Unit

    //returns true if this should block the event in others components
    fun onMouseClick(mouse: Vec2d, mouseButton: Int): Boolean = false

    //called when the mouse moves while one button is pressed
    //returns true if this should block the event in others components
    fun onMouseClickMove(mouse: Vec2d, clickedMouseButton: Int, timeSinceLastClick: Long): Boolean = false

    fun onMouseReleased(mouse: Vec2d, state: Int) = Unit

    //returns true if this should block the event in others components
    fun onKeyTyped(typedChar: Char, keyCode: Int): Boolean = false

    fun isMouseInside(mouse: Vec2d): Boolean = mouse in box

    fun onGuiClosed() = Unit
}