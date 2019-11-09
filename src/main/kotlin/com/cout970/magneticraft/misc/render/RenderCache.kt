package com.cout970.magneticraft.misc.render

import com.mojang.blaze3d.platform.GlStateManager.callList
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*

/**
 * Created by cout970 on 18/07/2016.
 */
class RenderCache {

    var id: Int = -1

    fun reset() {
        if (id != -1) {
            glDeleteLists(id, 1)
        }
        id = -1
    }

    fun start() {
        glNewList(id, GL11.GL_COMPILE)
    }

    fun end() {
        glEndList()
    }

    fun update(function: () -> Unit) {
        if (id == -1) {
            id = glGenLists(1)
            start()
            function.invoke()
            end()
        }
    }

    fun render() {
        callList(id)
    }
}