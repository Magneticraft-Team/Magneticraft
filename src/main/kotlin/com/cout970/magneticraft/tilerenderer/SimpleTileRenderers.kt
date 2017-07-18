package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.Sprite
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.block.*
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.multiblock.MultiblockShelvingUnit
import com.cout970.magneticraft.multiblock.MultiblockSolarPanel
import com.cout970.magneticraft.multiblock.MultiblockSteamEngine
import com.cout970.magneticraft.tileentity.*
import com.cout970.magneticraft.tileentity.modules.ModuleShelvingUnit
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.tilerenderer.core.TileRenderer
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.minus
import com.cout970.magneticraft.util.vector.vec3Of
import com.cout970.modelloader.ModelData
import com.cout970.modelloader.QuadProvider
import com.cout970.modelloader.QuadStorage
import com.cout970.modelloader.api.ModelLoaderApi
import com.cout970.modelloader.api.ModelUtilties
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.vec2Of
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.item.ItemSkull
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.MinecraftForgeClient

/**
 * Created by cout970 on 2017/07/01.
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

@RegisterRenderer(TileConnector::class)
object TileRendererConnector : TileRendererSimple<TileConnector>(
        modelLocation = { ModelResourceLocation(ElectricMachines.connector.registryName, "model") },
        filters = listOf<(String) -> Boolean>({ !it.startsWith("Base") }, { it.startsWith("Base") })
) {

    override fun renderModels(models: List<ModelCache>, te: TileConnector) {
        //updated the cache for rendering wires
        te.wireRender.update {
            te.electricModule.outputWiredConnections.forEach { i ->
                Utilities.renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector, 0.035)
            }
        }

        bindTexture(Utilities.WIRE_TEXTURE)
        te.wireRender.render()

        if (Debug.DEBUG) {
            Utilities.renderFloatingLabel("V: %.2f".format(te.node.voltage), vec3Of(0, 1, 0))
            Utilities.renderFloatingLabel("I: %.2f".format(te.node.amperage), vec3Of(0, 1.25, 0))
            Utilities.renderFloatingLabel("W: %.2f".format(te.node.voltage * te.node.amperage), vec3Of(0, 1.5, 0))
            Utilities.renderFloatingLabel("R: %.2f".format(te.node.resistance), vec3Of(0, 1.75, 0))
        }

        Utilities.rotateAroundCenter(te.facing)
        models[0].renderTextured()
        if (te.hasBase) {
            models[1].renderTextured()
        }
    }
}

@RegisterRenderer(TileBattery::class)
object TileRendererBattery : TileRendererSimple<TileBattery>(
        modelLocation = { ModelResourceLocation(ElectricMachines.battery.registryName, "model") }
) {

    override fun renderModels(models: List<ModelCache>, te: TileBattery) {
        Utilities.rotateFromCenter(te.facing, 180f)
        models.forEach { it.renderTextured() }
    }
}

@RegisterRenderer(TileElectricFurnace::class)
object TileRendererElectricFurnace : TileRendererSimple<TileElectricFurnace>(
        modelLocation = { ModelResourceLocation(ElectricMachines.electric_furnace.registryName, "model") },
        filters = listOf({ name -> name == "Shape_0" }, { name -> name == "Shape_1" })
) {
    val texture_normal = resource("textures/blocks/electric_machines/electric_furnace.png")
    val texture_off = resource("textures/blocks/electric_machines/electric_furnace_front.png")
    val texture_on = resource("textures/blocks/electric_machines/electric_furnace_front_on.png")

    override fun renderModels(models: List<ModelCache>, te: TileElectricFurnace) {
        Utilities.rotateFromCenter(te.facing, 180f)
        bindTexture(texture_normal)
        models[0].render()
        bindTexture(if (te.processModule.working) texture_on else texture_off)
        models[1].render()
    }
}

@RegisterRenderer(TileElectricPole::class)
object TileRendererElectricPole : TileRendererSimple<TileElectricPole>(
        modelLocation = { ModelResourceLocation(ElectricMachines.electric_pole.registryName, "model") }
) {

    override fun renderModels(models: List<ModelCache>, te: TileElectricPole) {

        te.wireRender.update {
            for (i in te.electricModule.outputWiredConnections) {
                Utilities.renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector)
            }
            for (i in te.electricModule.inputWiredConnections) {

                //wires are renderer twice to fix a render bug in vanilla
                val trans = i.firstNode.pos - i.secondNode.pos
                pushMatrix()
                translate(trans.x.toDouble(), trans.y.toDouble(), trans.z.toDouble())
                Utilities.renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector)
                popMatrix()
            }
        }

        bindTexture(Utilities.WIRE_TEXTURE)
        te.wireRender.render()

        val orientation = te.getBlockState()[ElectricMachines.PROPERTY_POLE_ORIENTATION]
        Utilities.rotateFromCenter(EnumFacing.UP, (orientation?.angle ?: 0f) + 90f)
        models.forEach { it.renderTextured() }
    }
}

@RegisterRenderer(TileElectricPoleTransformer::class)
object TileRendererElectricPoleTransformer : TileRendererSimple<TileElectricPoleTransformer>(
        modelLocation = { ModelResourceLocation(ElectricMachines.electric_pole_transformer.registryName, "model") }
) {

    override fun renderModels(models: List<ModelCache>, te: TileElectricPoleTransformer) {

        te.wireRender.update {

            for (i in te.electricModule.outputWiredConnections) {
                Utilities.renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector)
            }
            for (i in te.electricModule.inputWiredConnections) {
                if ((i.secondNode as IWireConnector).connectorsSize == 3) {
                    //wires are renderer twice to fix a render bug in vanilla
                    val trans = i.firstNode.pos - i.secondNode.pos
                    pushMatrix()
                    translate(trans.x.toDouble(), trans.y.toDouble(), trans.z.toDouble())
                    Utilities.renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector)
                    popMatrix()
                }
            }
        }

        bindTexture(Utilities.WIRE_TEXTURE)
        te.wireRender.render()

        val orientation = te.getBlockState()[ElectricMachines.PROPERTY_POLE_ORIENTATION]
        Utilities.rotateFromCenter(EnumFacing.UP, (orientation?.angle ?: 0f) + 90f)
        models.forEach { it.renderTextured() }
    }
}

@RegisterRenderer(TileTubeLight::class)
object TileRendererTubeLight : TileRendererSimple<TileTubeLight>(
        modelLocation = { ModelResourceLocation(Decoration.tubeLight.registryName, "model") },
        filters = listOf(
                { it: String -> !it.matches("Right\\d".toRegex()) && !it.matches("Left\\d".toRegex()) },
                { it: String -> it.matches("Left\\d".toRegex()) },
                { it: String -> it.matches("Right\\d".toRegex()) }
        )
) {

    override fun renderModels(models: List<ModelCache>, te: TileTubeLight) {
        Utilities.rotateFromCenter(te.facing, 90f)
        val front = te.world.getTile<TileTubeLight>(te.pos.offset(te.facing, 1))
        val back = te.world.getTile<TileTubeLight>(te.pos.offset(te.facing, -1))

        models[0].renderTextured()
        if (front == null || front.facing.axis != te.facing.axis) {
            models[1].renderTextured()
        }
        if (back == null || back.facing.axis != te.facing.axis) {
            models[2].renderTextured()
        }
    }
}

@RegisterRenderer(TileSolarPanel::class)
object TileRendererSolarPanel : TileRendererSimple<TileSolarPanel>(
        modelLocation = { ModelResourceLocation(Multiblocks.solarPanel.registryName, "model") }
) {

    override fun renderModels(models: List<ModelCache>, te: TileSolarPanel) {
        if (!te.active) {
            Utilities.multiblockPreview(te.facing, MultiblockSolarPanel)
            return
        }

        Utilities.rotateFromCenter(te.facing, -90f)
        translate(-1.0, 0.0, 0.0)
        models.forEach { it.renderTextured() }
    }
}

@RegisterRenderer(TileShelvingUnit::class)
object TileRendererShelvingUnit : TileRendererSimple<TileShelvingUnit>(
        modelLocation = { ModelResourceLocation(Multiblocks.shelvingUnit.registryName, "model") },
        filters = filterOf((1..24).map { "Crate$it" })
) {

    override fun renderModels(models: List<ModelCache>, te: TileShelvingUnit) {
        if (!te.active) {
            Utilities.multiblockPreview(te.facing, MultiblockShelvingUnit)
            return
        }

        Utilities.rotateFromCenter(te.facing, 0f)
        models[0].renderTextured()
        te.shelvingUnitModule.chestCount.forEachIndexed { level, count ->
            (0 until count).forEach {
                models[1 + level * ModuleShelvingUnit.CHESTS_PER_LEVEL + it].renderTextured()
            }
        }
    }
}

@RegisterRenderer(TileComputer::class)
object TileRendererComputer : TileRendererSimple<TileComputer>(
        modelLocation = { ModelResourceLocation(Computers.computer.registryName, "model") }
) {
    override fun renderModels(models: List<ModelCache>, te: TileComputer) {
        Utilities.rotateFromCenter(te.facing, 0f)
        models.forEach { it.renderTextured() }
    }
}

@RegisterRenderer(TileSluiceBox::class)
object TileRendererSluiceBox : TileRendererSimple<TileSluiceBox>(
        modelLocation = { ModelResourceLocation(Machines.sluiceBox.registryName, "model") }
) {

    var waterModel: ModelCache? = null

    override fun renderModels(models: List<ModelCache>, te: TileSluiceBox) {
        Utilities.rotateFromCenter(te.facing, 180f)
        if (te.sluiceBoxModule.progressLeft > 0 && MinecraftForgeClient.getRenderPass() == 1) {
            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            waterModel?.render()
        } else {
            models.forEach { it.renderTextured() }
        }
    }

    override fun onModelRegistryReload() {
        super.onModelRegistryReload()
        waterModel?.clear()
        val loc = ModelResourceLocation(Machines.sluiceBox.registryName, "water")
        val model = ModelLoaderApi.getModel(loc) ?: return
        val textureMap = Minecraft.getMinecraft().textureMapBlocks
        val waterFlow = textureMap.getAtlasSprite("minecraft:blocks/water_flow")
        val finalModel = updateModelUvs(model, waterFlow)
        waterModel = ModelCache {
            ModelUtilties.renderModelParts(finalModel.modelData, finalModel.modelData.parts)
        }
    }

    fun updateModelUvs(provider: QuadProvider, sprite: Sprite): QuadProvider {
        val quads = QuadStorage(
                provider.modelData.quads.pos,
                provider.modelData.quads.tex.map { sprite.mapUv(it) },
                provider.modelData.quads.indices
        )
        val modelData = ModelData(
                provider.modelData.useAmbientOcclusion,
                provider.modelData.use3dInGui,
                provider.modelData.particleTexture,
                provider.modelData.parts,
                quads
        )
        return QuadProvider(
                modelData,
                provider.particles,
                provider.bakedQuads.flatMap { it.value }
        )
    }

    fun Sprite.mapUv(uv: IVector2): IVector2 {
        return vec2Of(
                getInterpolatedU(uv.xd * 16),
                getInterpolatedV(uv.yd * 16)
        )
    }
}

@RegisterRenderer(TileCombustionChamber::class)
object TileRendererCombustionChamber : TileRendererSimple<TileCombustionChamber>(
        modelLocation = { ModelResourceLocation(Machines.combustionChamber.registryName, "model") }
) {
    override fun renderModels(models: List<ModelCache>, te: TileCombustionChamber) {
        Utilities.rotateFromCenter(te.facing, 180f)
        models.forEach { it.renderTextured() }
    }
}

@RegisterRenderer(TileSteamBoiler::class)
object TileRendererSteamBoiler : TileRendererSimple<TileSteamBoiler>(
        modelLocation = { ModelResourceLocation(Machines.steamBoiler.registryName, "model") }
) {
    override fun renderModels(models: List<ModelCache>, te: TileSteamBoiler) {
        models.forEach { it.renderTextured() }
    }
}

@RegisterRenderer(TileSteamEngine::class)
object TileRendererSteamEngine : TileRendererSimple<TileSteamEngine>(
        modelLocation = { ModelResourceLocation(Multiblocks.steamEngine.registryName, "model") }
) {

    override fun renderModels(models: List<ModelCache>, te: TileSteamEngine) {
        if (!te.active) {
            Utilities.multiblockPreview(te.facing, MultiblockSteamEngine)
            return
        }
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(-1, 0, -1)
        models[0].renderTextured()
    }
}