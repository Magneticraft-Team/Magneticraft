package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.block.Decoration
import com.cout970.magneticraft.block.ElectricMachines
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.*
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.minus
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/07/01.
 */

@RegisterRenderer(TileBattery::class)
object TileRendererBattery : TileRendererSimple<TileBattery>(
        modelLocation = { ModelResourceLocation(ElectricMachines.battery.registryName, "model") }
) {
    val texture = resource("textures/blocks/electric_machines/battery.png")

    override fun renderModels(models: List<ModelCache>, te: TileBattery) {
        bindTexture(texture)
        Utilities.rotateFromCenter(te.facing, 180f)
        models.forEach { it.render() }
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

@RegisterRenderer(TileCoalGenerator::class)
object TileRendererCoalGenerator : TileRendererSimple<TileCoalGenerator>(
        modelLocation = { ModelResourceLocation(ElectricMachines.coal_generator.registryName, "model") },
        filters = listOf({ name -> name == "Shape_0" }, { name -> name != "Shape_0" }) //TODO add rotation animation
) {
    val texture = resource("textures/blocks/electric_machines/coal_generator.png")

    override fun renderModels(models: List<ModelCache>, te: TileCoalGenerator) {
        bindTexture(texture)
        Utilities.rotateFromCenter(te.facing, 180f)
        models.forEach { it.render() }
    }
}

@RegisterRenderer(TileElectricPole::class)
object TileRendererElectricPole : TileRendererSimple<TileElectricPole>(
        modelLocation = { ModelResourceLocation(ElectricMachines.electric_pole.registryName, "model") }
) {
    val texture = resource("textures/blocks/electric_machines/electric_pole.png")

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

        bindTexture(texture)
        val orientation = te.getBlockState()[ElectricMachines.PROPERTY_POLE_ORIENTATION]
        Utilities.rotateFromCenter(EnumFacing.UP, (orientation?.angle ?: 0f) + 90f)
        models.forEach { it.render() }
    }
}

@RegisterRenderer(TileElectricPoleTransformer::class)
object TileRendererElectricPoleTransformer : TileRendererSimple<TileElectricPoleTransformer>(
        modelLocation = { ModelResourceLocation(ElectricMachines.electric_pole_transformer.registryName, "model") }
) {
    val texture = resource("textures/blocks/electric_machines/electric_pole_transformer.png")

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

        bindTexture(texture)
        val orientation = te.getBlockState()[ElectricMachines.PROPERTY_POLE_ORIENTATION]
        Utilities.rotateFromCenter(EnumFacing.UP, (orientation?.angle ?: 0f) + 90f)
        models.forEach { it.render() }
    }
}

@RegisterRenderer(TileTubeLight::class)
object TileRendererTubeLight : TileRendererSimple<TileTubeLight>(
        modelLocation = { ModelResourceLocation(Decoration.tubeLight.registryName, "model") }
) {
    val texture = resource("textures/blocks/decoration/tube_light.png")

    override fun renderModels(models: List<ModelCache>, te: TileTubeLight) {
        bindTexture(texture)
        Utilities.rotateFromCenter(te.facing, 90f)
        models.forEach { it.render() }
    }
}