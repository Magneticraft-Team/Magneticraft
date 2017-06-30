package tilerenderer

import com.cout970.magneticraft.tileentity.TileKilnShelf
import com.cout970.magneticraft.util.resource
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.item.ItemSkull

object TileRendererKilnShelf : TileEntityRenderer<TileKilnShelf>() {

    val texture = resource("textures/models/kiln_shelf.png")

    override fun renderTileEntityAt(te: TileKilnShelf, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
        val stack = te.getStack()

        if (stack != null) {
            pushMatrix()
            translate(x + 0.5, y + 0.4375, z + 0.3125)
            if (!Minecraft.getMinecraft().renderItem.shouldRenderItemIn3D(stack) || stack.item is ItemSkull) {
                translate(0.0, -0.045, 0.125)
                rotate(90f, 1f, 0f, 0f)
            } else {
                translate(0.0, -0.125, 0.0625 * 3)
            }

            bindTexture(texture)

            Minecraft.getMinecraft().renderItem.renderItem(stack, ItemCameraTransforms.TransformType.GROUND)

            popMatrix()
        }
    }
}