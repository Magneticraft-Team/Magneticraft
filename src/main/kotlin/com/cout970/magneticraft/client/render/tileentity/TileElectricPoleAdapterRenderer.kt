package com.cout970.magneticraft.client.render.tileentity

import coffee.cypher.mcextlib.extensions.vectors.minus
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.tileentity.electric.TileElectricPoleAdapter
import net.minecraft.client.renderer.GlStateManager.*

/**
 * Created by cout970 on 06/07/2016.
 */
object TileElectricPoleAdapterRenderer : TileEntityRenderer<TileElectricPoleAdapter>() {

    override fun renderTileEntityAt(te: TileElectricPoleAdapter, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {

        te.wireRender.update {
            for (i in te.wiredConnections) {
                if (te.firstNode == i.firstNode || te.secondNode == i.firstNode) {
                    renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector)
                } else if (i.firstNode == te.secondNode) {//wires are renderer twice to fix a render bug in vanilla
                    val trans = i.firstNode.pos - i.secondNode.pos
                    pushMatrix()
                    translate(trans.x.toDouble(), trans.y.toDouble(), trans.z.toDouble())
                    renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector)
                    popMatrix()
                }
            }
        }

        pushMatrix()
        translate(x, y, z)
        bindTexture(WIRE_TEXTURE)
        te.wireRender.render()
        popMatrix()
    }

    override fun isGlobalRenderer(te: TileElectricPoleAdapter?): Boolean = true
}