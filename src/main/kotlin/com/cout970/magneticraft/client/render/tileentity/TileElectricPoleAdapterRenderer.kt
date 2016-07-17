package com.cout970.magneticraft.client.render.tileentity

import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.tileentity.electric.TileElectricPoleAdapter
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 06/07/2016.
 */
object TileElectricPoleAdapterRenderer : TileEntityRenderer<TileElectricPoleAdapter>() {

    override fun renderTileEntityAt(te: TileElectricPoleAdapter, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {

        if (te.renderCache == -1) {
            te.renderCache = GlStateManager.glGenLists(1)
            GlStateManager.glNewList(te.renderCache, GL11.GL_COMPILE)
            for (i in te.connections) {
                if (te.firstNode == i.firstNode && te.secondNode == i.firstNode) continue
                renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector)
            }
            GlStateManager.glEndList()
        }

        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, z)
        bindTexture(WIRE_TEXTURE)
        GlStateManager.callList(te.renderCache)
        GlStateManager.popMatrix()
    }

    override fun isGlobalRenderer(te: TileElectricPoleAdapter?): Boolean = true
}