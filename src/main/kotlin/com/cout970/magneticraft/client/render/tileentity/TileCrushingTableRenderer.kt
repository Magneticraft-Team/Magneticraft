package com.cout970.magneticraft.client.render.tileentity

import com.cout970.magneticraft.tileentity.TileCrushingTable
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.ItemSkull

object TileCrushingTableRenderer : TileEntitySpecialRenderer<TileCrushingTable>() {

    override fun renderTileEntityAt(te: TileCrushingTable, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {
        val stack = te.getStack()

        if (stack != null) {
            GlStateManager.pushMatrix()
            GlStateManager.translate(x + 0.5, y + 0.9375, z + 0.3125)
            if (!Minecraft.getMinecraft().renderItem.shouldRenderItemIn3D(stack) || stack.item is ItemSkull) {
                GlStateManager.translate(0.0, 0.0, 0.125)
            }

            GlStateManager.rotate(90f, 1f, 0f, 0f)

            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)

            Minecraft.getMinecraft().renderItem.renderItem(stack, ItemCameraTransforms.TransformType.GROUND)

            GlStateManager.popMatrix()
        }
    }
}