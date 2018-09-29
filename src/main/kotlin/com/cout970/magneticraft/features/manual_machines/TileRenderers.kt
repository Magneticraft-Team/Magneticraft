package com.cout970.magneticraft.features.manual_machines

import com.cout970.magneticraft.misc.RegisterRenderer
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.systems.tilemodules.ModuleSluiceBox
import com.cout970.magneticraft.systems.tilerenderers.*
import com.cout970.modelloader.api.*
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.item.ItemSkull
import net.minecraftforge.client.MinecraftForgeClient

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterRenderer(TileCrushingTable::class)
object TileRendererCrushingTable : BaseTileRenderer<TileCrushingTable>() {

    override fun init() = Unit

    override fun render(te: TileCrushingTable) {
        val stack = te.crushingModule.storedItem

        if (stack.isNotEmpty) {
            translate(0.5, 0.9375, 0.3125)
            if (!Minecraft.getMinecraft().renderItem.shouldRenderItemIn3D(stack) || stack.item is ItemSkull) {
                translate(0.0, -0.045, 1 * PIXEL)
                rotate(90f, 1f, 0f, 0f)
            } else {
                translate(0.0, -0.125, 0.0625 * 3)
            }
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            Minecraft.getMinecraft().renderItem.renderItem(stack, ItemCameraTransforms.TransformType.GROUND)
        }
    }
}


@RegisterRenderer(TileSluiceBox::class)
object TileRendererSluiceBox : BaseTileRenderer<TileSluiceBox>() {

    var waterModel: IRenderCache? = null

    override fun init() {
        createModel(Blocks.sluiceBox,
            ModelSelector("gravel", FilterString("gravel"))
        )
    }

    override fun render(te: TileSluiceBox) {
        Utilities.rotateFromCenter(te.facing, 180f)

        if (MinecraftForgeClient.getRenderPass() == 1) {
            if (te.sluiceBoxModule.progressLeft > 0) {
                bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
                waterModel?.render()
            }
            return
        }

        if (te.invModule.inventory[0].isNotEmpty) {
            val p = te.sluiceBoxModule.progressLeft
            val progress = if (p == 0) 1.0 else p / ModuleSluiceBox.MAX_PROGRESS.toDouble()
            val y = (PIXEL * 4.0) * progress * te.sluiceBoxModule.level / ModuleSluiceBox.MAX_ITEMS.toDouble()

            stackMatrix {
                translate(0, y, 0)
                renderModel("gravel")
            }
        }
        renderModel("default")
    }

    override fun onModelRegistryReload() {
        super.onModelRegistryReload()
        waterModel?.close()
        val loc = modelOf(Blocks.sluiceBox, "water")
        val model = ModelLoaderApi.getModelEntry(loc) ?: return

        val textureMap = Minecraft.getMinecraft().textureMapBlocks
        val waterFlow = textureMap.getAtlasSprite("minecraft:blocks/water_flow")

        val raw = model.raw as? Model.Mcx ?: return
        val finalModel = ModelTransform.updateModelUvs(raw.data, waterFlow)

        waterModel = ModelCache {
            ModelUtilities.renderModel(finalModel)
        }
    }
}