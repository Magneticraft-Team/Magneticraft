package com.cout970.magneticraft.client.render.tileentity

import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.tileentity.electric.TileElectricPole
import net.minecraft.client.renderer.GlStateManager.*

/**
 * Created by cout970 on 29/06/2016.
 */
object TileElectricPoleRenderer : TileEntityRenderer<TileElectricPole>() {

    override fun renderTileEntityAt(te: TileElectricPole, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {

        te.wireRender.update {
            for (i in te.wiredConnections) {
                if (i.firstNode != te.node) continue
                renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector)
            }
        }

        pushMatrix()
        translate(x, y, z)
        bindTexture(WIRE_TEXTURE)
        te.wireRender.render()
        popMatrix()
    }

    override fun isGlobalRenderer(te: TileElectricPole?): Boolean = true
}