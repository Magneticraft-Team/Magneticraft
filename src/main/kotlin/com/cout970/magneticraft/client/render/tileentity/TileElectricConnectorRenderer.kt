package com.cout970.magneticraft.client.render.tileentity

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.tileentity.electric.TileElectricConnector
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 29/06/2016.
 */
object TileElectricConnectorRenderer : TileEntitySpecialRenderer<TileElectricConnector>() {

    override fun renderTileEntityAt(te: TileElectricConnector, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {

        if(Debug.DEBUG) {
            pushMatrix()
            val sum = if ((te.pos.x + te.pos.y + te.pos.z) % 2 == 0) 1 else 0
            color(1f, 1f, 1f)
            renderFloatingLabel("%.1fV".format(te.node.voltage), Vec3d(x + 0.5, y + 1 + 0.5 + sum, z + 0.5))
            renderFloatingLabel("%.2fA".format(te.node.amperage), Vec3d(x + 0.5, y + 1 + 0.25 + sum, z + 0.5))
            renderFloatingLabel("%.2fW".format(te.node.voltage * te.node.amperage), Vec3d(x + 0.5, y + 1 + sum, z + 0.5))
            popMatrix()
        }
    }
}