package com.cout970.magneticraft.client.render.tileentity

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.tileentity.electric.TileElectricConnector
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 29/06/2016.
 */
object TileElectricConnectorRenderer : TileEntitySpecialRenderer<TileElectricConnector>() {

    override fun renderTileEntityAt(te: TileElectricConnector, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {

        if (Debug.DEBUG) {
            pushMatrix()
            color(1f, 1f, 1f)
            renderFloatingLabel("%.1fV".format(te.node.voltage), Vec3d(x + 0.5, y + 1, z + 0.5))
            renderFloatingLabel("%.2fA".format(te.node.amperage), Vec3d(x + 0.5, y + 0.75, z + 0.5))
            renderFloatingLabel("%.2fW".format(te.node.voltage * te.node.amperage), Vec3d(x + 0.5, y + 0.5, z + 0.5))
            popMatrix()

            if (te.renderCache == -1) {
                te.renderCache = glGenLists(1)
                glNewList(te.renderCache, GL11.GL_COMPILE)
                for (i in te.connections) {
                    if (i.firstNode != te.node) continue
                    if (i.secondNode !is IWireConnector)continue
                    renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector)
                }
                glEndList()
            }

            pushMatrix()
            translate(x, y, z)
            bindTexture(WIRE_TEXTURE)
            callList(te.renderCache)
            popMatrix()
        }
    }
}