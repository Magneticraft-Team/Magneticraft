package com.cout970.magneticraft.client.render.tileentity

import coffee.cypher.mcextlib.extensions.vectors.toDoubleVec
import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.tileentity.electric.TileElectricPole
import com.cout970.magneticraft.util.resource
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.math.Vec3d
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 29/06/2016.
 */
object TileElectricPoleRenderer : TileEntitySpecialRenderer<TileElectricPole>() {

    val WIRE_TEXTURE = resource("textures/models/wire_texture.png")

    override fun renderTileEntityAt(te: TileElectricPole, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {

        if (Debug.DEBUG) {
            pushMatrix()
            color(1f, 1f, 1f)
            renderFloatingLabel("%.1fV".format(te.node.voltage), Vec3d(x + 0.5, y + 0.5 + 1, z + 0.5))
            renderFloatingLabel("%.2fA".format(te.node.amperage), Vec3d(x + 0.5, y + 0.25 + 1, z + 0.5))
            renderFloatingLabel("%.2fW".format(te.node.voltage * te.node.amperage), Vec3d(x + 0.5, y + 1, z + 0.5))
            popMatrix()
        }

        if (te.renderCache == -1) {
            te.renderCache = glGenLists(1)
            glNewList(te.renderCache, GL11.GL_COMPILE)
            for (i in te.connections) {
                if (i.firstNode != te.node) continue
                val origins = (i.firstNode as IWireConnector).connections
                val destinations = (i.secondNode as IWireConnector).connections
                val direction = i.secondNode.pos.subtract(i.firstNode.pos)
                for ((c, start) in origins.withIndex()) {
                    val order = (i.secondNode as IWireConnector).getConnectionIndex(c, i.firstNode as IWireConnector, i)
                    val end = direction.toDoubleVec().add(destinations[order])

                    val tes = Tessellator.getInstance()
                    val buffer = tes.buffer

                    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL)

                    val points = interpolateWire(start, end)

                    for (p in 0..points.size - 2) {
                        drawLine(buffer, points[p], points[p + 1])
                    }
                    tes.draw()
                }
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