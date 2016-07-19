package com.cout970.magneticraft.util.misc

import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 18/07/2016.
 */
class RenderCache {

    var id: Int = -1

    fun reset() {
        if (id != -1) {
            GlStateManager.glDeleteLists(id, 1)
        }
        id = -1
    }

    fun start() {
        GlStateManager.glNewList(id, GL11.GL_COMPILE)
    }

    fun end() {
        GlStateManager.glEndList()
    }

    fun update(function: () -> Unit) {
        if (id == -1) {
            id = GlStateManager.glGenLists(1)
            start()
            function.invoke()
            end()
        }
    }

    fun render() {
        GlStateManager.callList(id)
    }
}