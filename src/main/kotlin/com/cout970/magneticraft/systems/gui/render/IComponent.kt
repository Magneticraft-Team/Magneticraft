package com.cout970.magneticraft.systems.gui.render

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.Vec2d

/**
 * Created by cout970 on 20/05/2016.
 */
interface IComponent {

    val pos: IVector2
    val size: IVector2

    val collisionBox get() = pos to size

    var gui: IGui

    fun init() = Unit

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

    fun onKeyReleased(typedChar: Char, keyCode: Int): Boolean = false

    fun onWheel(amount: Int) = Unit

    fun onGuiClosed() = Unit
}