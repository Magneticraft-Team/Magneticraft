package com.cout970.magneticraft.tilerenderer.core

import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/06/16.
 */
class ModelCache(val func: () -> Unit) {
    private var id: Int = -1

    fun render() {
        if(id == -1){
            id = GlStateManager.glGenLists(1)
            GlStateManager.glNewList(id, GL11.GL_COMPILE)
            func()
            GlStateManager.glEndList()
        }
        GlStateManager.callList(id)
    }

    fun clear(){
        if(id != -1){
            GlStateManager.glDeleteLists(id, 1)
        }
        id = -1
    }
}