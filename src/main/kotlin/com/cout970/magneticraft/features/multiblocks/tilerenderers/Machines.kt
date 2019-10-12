package com.cout970.magneticraft.features.multiblocks.tilerenderers

import com.cout970.magneticraft.features.automatic_machines.Blocks
import com.cout970.magneticraft.features.automatic_machines.TileRendererConveyorBelt
import com.cout970.magneticraft.features.multiblocks.tileentities.*
import com.cout970.magneticraft.misc.RegisterRenderer
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.systems.tilerenderers.*
import com.cout970.modelloader.api.*
import com.cout970.modelloader.api.formats.gltf.GltfAnimationBuilder
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.ResourceLocation
import com.cout970.magneticraft.features.multiblocks.Blocks as Multiblocks

@RegisterRenderer(TileGrinder::class)
object TileRendererGrinder : TileRendererMultiblock<TileGrinder>() {

    override fun init() {
        createModel(Multiblocks.grinder, ModelSelector("animation", FilterAlways, FilterRegex("animation", FilterTarget.ANIMATION)))
    }

    override fun render(te: TileGrinder) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(0, 0, -1)
        if (te.processModule.working) renderModel("animation") else renderModel("default")
    }
}

@RegisterRenderer(TileSieve::class)
object TileRendererSieve : TileRendererMultiblock<TileSieve>() {

    override fun init() {
        createModel(Multiblocks.sieve, ModelSelector("animation", FilterAlways, FilterRegex("animation", FilterTarget.ANIMATION)))
    }

    override fun render(te: TileSieve) {
        Utilities.rotateFromCenter(te.facing, 180f)
        translate(0, 0, 2)
        if (te.processModule.working) renderModel("animation") else renderModel("default")
    }
}

@RegisterRenderer(TileHydraulicPress::class)
object TileRendererHydraulicPress : TileRendererMultiblock<TileHydraulicPress>() {

    override fun init() {
        createModel(Multiblocks.hydraulicPress, ModelSelector("animation", FilterAlways, FilterRegex("animation", FilterTarget.ANIMATION)))
    }

    override fun render(te: TileHydraulicPress) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(0, 0, -1)
        if (te.processModule.working) renderModel("animation") else renderModel("default")

        val stack = te.inventory[0]
        if (stack.isNotEmpty) {
            stackMatrix {
                translate(0.5f, 2f, 0.5f)
                if (!Minecraft.getMinecraft().renderItem.shouldRenderItemIn3D(stack)) {
                    scale(2.0)
                } else {
                    translate(0f, -PIXEL, 0f)
                }
                Utilities.renderItem(stack)
            }
        }
    }
}

@RegisterRenderer(TileBigCombustionChamber::class)
object TileRendererBigCombustionChamber : TileRendererMultiblock<TileBigCombustionChamber>() {

    override fun init() {
        createModel(Multiblocks.bigCombustionChamber,
                ModelSelector("fire_off", FilterString("fire_off")),
                ModelSelector("fire_on", FilterString("fire_on"))
        )
    }

    override fun render(te: TileBigCombustionChamber) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(0, 0, -1)
        renderModel("default")
        if (te.bigCombustionChamberModule.working()) renderModel("fire_on") else renderModel("fire_off")
    }
}

@RegisterRenderer(TileBigSteamBoiler::class)
object TileRendererBigSteamBoiler : TileRendererMultiblock<TileBigSteamBoiler>() {

    override fun init() {
        createModel(Multiblocks.bigSteamBoiler)
    }

    override fun render(te: TileBigSteamBoiler) {
        Utilities.rotateFromCenter(te.facing, 0f)
        renderModel("default")
    }
}

@RegisterRenderer(TileSteamTurbine::class)
object TileRendererSteamTurbine : TileRendererMultiblock<TileSteamTurbine>() {

    override fun init() {
        createModel(Multiblocks.steamTurbine,
                ModelSelector("blade", FilterRegex("blade.*")),
                ModelSelector("not_blade", FilterNotRegex("blade.*"))
        )
    }

    override fun render(te: TileSteamTurbine) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(-1, 0, 0)
        renderModel("not_blade")

        val speed = 0.25f * te.steamGeneratorModule.production.storage / te.steamGeneratorModule.maxProduction

        // Smooth changes in speed
        if (te.turbineSpeed < speed) {
            te.turbineSpeed += 0.25f / 20
        } else if (te.turbineSpeed > speed) {
            te.turbineSpeed -= 0.25f / 20
        }

        // Smooth rotation
        val now = (System.currentTimeMillis() % 0xFFFF).toFloat()
        val delta = now - te.lastTime

        te.lastTime = now
        te.turbineAngle += te.turbineSpeed * delta

        translate(1.5f, 1.5f, 0f)
        repeat(12) {
            stackMatrix {
                rotate(it * 360 / 12f + te.turbineAngle, 0, 0, 1)
                translate(-1.5f, -1.5f, 0f)
                renderModel("blade")
            }
        }
    }
}

@RegisterRenderer(TileBigElectricFurnace::class)
object TileRendererBigElectricFurnace : TileRendererMultiblock<TileBigElectricFurnace>() {

    var belt: IRenderCache? = null

    override fun init() {
        createModelWithoutTexture(Multiblocks.bigElectricFurnace,
                ModelSelector("multiblock", FilterAlways),
                ModelSelector("dark", FilterString("Object 26"))
        )

        createModel(Blocks.conveyorBelt, listOf(
                ModelSelector("back_legs", FilterRegex("back_leg.*")),
                ModelSelector("front_legs", FilterRegex("front_leg.*")),
                ModelSelector("lateral_left", FilterRegex("lateral_left")),
                ModelSelector("lateral_right", FilterRegex("lateral_right")),
                ModelSelector("panel_left", FilterRegex("panel_left")),
                ModelSelector("panel_right", FilterRegex("panel_right"))
        ), "base")

        val anim = modelOf(Blocks.conveyorBelt, "anim")

        //cleaning
        belt?.close()

        val beltModel = ModelLoaderApi.getModelEntry(anim) ?: return
        val texture = resource("blocks/machines/conveyor_belt_anim")

        belt = updateTexture(beltModel, texture)
    }

    override fun render(te: TileBigElectricFurnace) {
        Utilities.rotateFromCenter(te.facing, 0f)
        // Input belt
        renderBelt()
        TileRendererConveyorBelt.renderDynamicParts(te.inConveyor, ticks)

        // Main structure
        translate(0f, 0f, -1f)
        if (te.processModule.working) {
            bindTexture(resource("textures/blocks/multiblocks/big_electric_furnace_on.png"))
        } else {
            bindTexture(resource("textures/blocks/multiblocks/big_electric_furnace_off.png"))
        }
        renderModel("multiblock")

        // Dark interior
        disableTexture2D()
        if (te.processModule.working) {
            color(0.2f, 0.1f, 0.05f, 1f)
        } else {
            color(0f, 0f, 0f, 1f)
        }
        renderModel("dark")
        enableTexture2D()
        color(1f, 1f, 1f, 1f)

        // Exit belt
        translate(0f, 0f, -1f)
        renderBelt()
        TileRendererConveyorBelt.renderDynamicParts(te.outConveyor, ticks)
    }

    fun renderBelt() {
        belt?.render()

        renderModel("default")
        renderModel("panel_left")
        renderModel("panel_right")
        renderModel("back_legs")
        renderModel("front_legs")
    }

    private fun updateTexture(model: ModelEntry, texture: ResourceLocation): IRenderCache {
        val raw = model.raw
        val textureMap = Minecraft.getMinecraft().textureMapBlocks
        val animTexture = textureMap.getAtlasSprite(texture.toString())

        return when (raw) {
            is Model.Mcx -> {
                val finalModel = ModelTransform.updateModelUvs(raw.data, animTexture)
                TextureModelCache(TextureMap.LOCATION_BLOCKS_TEXTURE, ModelCache { ModelUtilities.renderModel(finalModel) })
            }
            is Model.Gltf -> {
                val finalModel = ModelTransform.updateModelUvs(raw.data, animTexture)
                val anim = GltfAnimationBuilder().buildPlain(finalModel)
                AnimationRenderCache(anim) { 0.0 }
            }
            else -> error("Invalid type: $raw, ${raw::class.java}")
        }
    }
}