package com.cout970.magneticraft.client.render.tileentity

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
                if (te.firstNode == i.firstNode && te.secondNode == i.firstNode) continue
                renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector)
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