package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.tileentity.TileTableSieve
import com.cout970.magneticraft.util.resource
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 19/06/2016.
 */
object TileRendererTableSieve : TileEntityRenderer<TileTableSieve>() {

    val texture = resource("textures/models/table_sieve_content.png")

    override fun renderTileEntityAt(te: TileTableSieve, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
        val item = te.inventory[0]
        if (item != null) {
            pushMatrix()
            translate(x, y, z)
            val size = item.stackSize
            var process = 0.0
            if (size != 0) {
                process = (size + (1 - (te.progress / TileTableSieve.UPDATE_TIME.toDouble()))) / 64
            }

            val h = 0.0625 * 7.50 + 0.0625 * 1.5 * process
            bindTexture(texture)
            renderQuad(h)

            popMatrix()
        }
    }

    private fun renderQuad(h: Double) {
        val tes = Tessellator.getInstance()
        val buffer = tes.buffer
        val a = 0.0625 * 2
        val b = 1 - a
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL)
        buffer.pos(a, h, a).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
        buffer.pos(a, h, b).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()
        buffer.pos(b, h, b).tex(1.0, 1.0).normal(0f, 1f, 0f).endVertex()
        buffer.pos(b, h, a).tex(1.0, 0.0).normal(0f, 1f, 0f).endVertex()
        tes.draw()
    }
}