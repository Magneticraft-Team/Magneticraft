package com.cout970.magneticraft.tilerenderer


import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.tileentity.electric.TileElectricPoleAdapter
import com.cout970.magneticraft.util.vector.minus

/**
 * Created by cout970 on 06/07/2016.
 */
object TileRendererElectricPoleAdapter : TileEntityRenderer<TileElectricPoleAdapter>() {

    override fun renderTileEntityAt(te: TileElectricPoleAdapter, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {

        te.wireRender.update {
            for (i in te.traitElectricity.outputWiredConnections) {
                renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector)
            }
            for (i in te.traitElectricity.inputWiredConnections) {
                if (i.secondNode == te.firstNode) continue
                //wires are renderer twice to fix a render bug in vanilla
                val trans = i.firstNode.pos - i.secondNode.pos
                pushMatrix()
                translate(trans.x.toDouble(), trans.y.toDouble(), trans.z.toDouble())
                renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector)
                popMatrix()
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