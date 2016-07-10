package com.cout970.magneticraft.client.render.tileentity

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.tileentity.electric.TileElectricPoleAdapter
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 06/07/2016.
 */
object TileElectricPoleAdapterRenderer : TileEntitySpecialRenderer<TileElectricPoleAdapter>() {

    override fun renderTileEntityAt(te: TileElectricPoleAdapter, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {

        if (Debug.DEBUG) {
            GlStateManager.pushMatrix()
            GlStateManager.color(1f, 1f, 1f)
            renderFloatingLabel("%.1fV".format(te.node.voltage), Vec3d(x + 0.5, y + 0.5 + 1, z + 0.5))
            renderFloatingLabel("%.2fA".format(te.node.amperage), Vec3d(x + 0.5, y + 0.25 + 1, z + 0.5))
            renderFloatingLabel("%.2fW".format(te.node.voltage * te.node.amperage), Vec3d(x + 0.5, y + 1, z + 0.5))
            GlStateManager.popMatrix()
        }

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
}