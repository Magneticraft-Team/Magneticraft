package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.ManualMachines
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.TileCrushingTable
import com.cout970.magneticraft.tileentity.TileSluiceBox
import com.cout970.magneticraft.tileentity.modules.ModuleSluiceBox
import com.cout970.magneticraft.tilerenderer.core.*
import com.cout970.magneticraft.util.resource
import com.cout970.modelloader.api.ModelLoaderApi
import com.cout970.modelloader.api.ModelUtilties
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.item.ItemSkull
import net.minecraftforge.client.MinecraftForgeClient

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterRenderer(TileCrushingTable::class)
object TileRendererCrushingTable : TileRenderer<TileCrushingTable>() {

    override fun renderTileEntityAt(te: TileCrushingTable, x: Double, y: Double, z: Double, partialTicks: Float,
                                    destroyStage: Int) {
        val stack = te.crushingModule.storedItem

        if (stack.isNotEmpty) {
            pushMatrix()
            translate(x + 0.5, y + 0.9375, z + 0.3125)
            if (!Minecraft.getMinecraft().renderItem.shouldRenderItemIn3D(stack) || stack.item is ItemSkull) {
                translate(0.0, -0.045, 1 * PIXEL)
                rotate(90f, 1f, 0f, 0f)
            } else {
                translate(0.0, -0.125, 0.0625 * 3)
            }
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            Minecraft.getMinecraft().renderItem.renderItem(stack, ItemCameraTransforms.TransformType.GROUND)
            popMatrix()
        }
    }
}


@RegisterRenderer(TileSluiceBox::class)
object TileRendererSluiceBox : TileRendererSimple<TileSluiceBox>(
        modelLocation = modelOf(ManualMachines.sluiceBox),
        filters = listOf<(String) -> Boolean>({ it != "Shape17" }, { it == "Shape17" })
) {

    val gravelTexture = resource("textures/blocks/machines/sluice_box_gravel.png")
    var waterModel: ModelCache? = null

    override fun renderModels(models: List<ModelCache>, te: TileSluiceBox) {
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
                bindTexture(gravelTexture)
                models.last().render()
            }
        }
        models.forEach { it.renderTextured() }
    }

    override fun onModelRegistryReload() {
        super.onModelRegistryReload()
        waterModel?.close()
        val loc = modelOf(ManualMachines.sluiceBox, "water")()
        val model = ModelLoaderApi.getModel(loc) ?: return

        val textureMap = Minecraft.getMinecraft().textureMapBlocks
        val waterFlow = textureMap.getAtlasSprite("minecraft:blocks/water_flow")
        val finalModel = ModelTransform.updateModelUvs(model, waterFlow)

        waterModel = ModelCache {
            ModelUtilties.renderModelParts(finalModel.modelData, finalModel.modelData.parts)
        }
    }
}

//object DebugTileEntityRenderer : TileEntitySpecialRenderer<TileEntityBed>() {
//
//    private var model: IAnimatedModel? = null
//
//    override fun render(te: TileEntityBed, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
//        te.world = world
//        val time = te.world.totalWorldTime.toDouble()
//
//        if (!te.isHeadPiece) return
//
//        GlStateManager.pushMatrix()
//        GlStateManager.translate(x, y, z)
//        bindTexture(ResourceLocation("modelloader:textures/blocks/multiblocks/steam_engine.png"))
//        model?.render(time + partialTicks)
//        GlStateManager.popMatrix()
//    }
//
//    fun initModels() {
//        val loc = ModelResourceLocation("minecraft:gold_block", "normal")
//        val entry = ModelLoaderApi.getModelEntry(loc) ?: return
//        val model = entry.raw
//
//        if (model is Model.Gltf) {
//            this.model = GltfAnimationBuilder().build(model.data).firstOrNull()?.second
//        }
//    }
//}