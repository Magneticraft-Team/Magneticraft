package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.block.ElectricConductors
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.tileentity.TileConnector
import com.cout970.magneticraft.tileentity.TileElectricCable
import com.cout970.magneticraft.tileentity.TileElectricPole
import com.cout970.magneticraft.tileentity.TileElectricPoleTransformer
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.TileRendererSimple
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.tilerenderer.core.modelOf
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.minus
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/08/10.
 */


@RegisterRenderer(TileConnector::class)
object TileRendererConnector : TileRendererSimple<TileConnector>(
        modelLocation = modelOf(ElectricConductors.connector),
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
//            Utilities.renderFloatingLabel("R: %.2f".format(te.node.resistance), vec3Of(0, 1.75, 0))
        }

        Utilities.rotateAroundCenter(te.facing)
        models[0].renderTextured()
        if (te.hasBase) {
            models[1].renderTextured()
        }
    }
}

@RegisterRenderer(TileElectricPole::class)
object TileRendererElectricPole : TileRendererSimple<TileElectricPole>(
        modelLocation = modelOf(ElectricConductors.electric_pole)
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

        val orientation = te.getBlockState()[ElectricConductors.PROPERTY_POLE_ORIENTATION]
        Utilities.rotateFromCenter(EnumFacing.UP, (orientation?.angle ?: 0f) + 90f)
        models.forEach { it.renderTextured() }
    }
}

@RegisterRenderer(TileElectricPoleTransformer::class)
object TileRendererElectricPoleTransformer : TileRendererSimple<TileElectricPoleTransformer>(
        modelLocation = modelOf(ElectricConductors.electric_pole_transformer)
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

        val orientation = te.getBlockState()[ElectricConductors.PROPERTY_POLE_ORIENTATION]
        Utilities.rotateFromCenter(EnumFacing.UP, (orientation?.angle ?: 0f) + 90f)
        models.forEach { it.renderTextured() }
    }
}

@RegisterRenderer(TileElectricCable::class)
object TileRendererElectricCable : TileRendererSimple<TileElectricCable>(
        modelLocation = modelOf(ElectricConductors.electric_cable),
        filters = filterOf(listOf("center", "up", "down", "north", "south", "west", "east"))
) {

    override fun renderModels(models: List<ModelCache>, te: TileElectricCable) {
        var conn: EnumFacing? = null
        var count = 0

        sidesToIndices.forEach { (side, index) ->
            val tile = te.world.getTileEntity(te.pos + side) ?: return@forEach

            if (tile.getOrNull(ELECTRIC_NODE_HANDLER, side.opposite) != null) {
                models[index].renderTextured()
                conn = conn ?: side.opposite
                count++
            }
        }

        if (count == 1) {
            bindTexture(resource("textures/blocks/electric_connectors/electric_cable_center_${conn!!.name.toLowerCase()}.png"))
        } else {
            bindTexture(resource("textures/blocks/electric_connectors/electric_cable_center.png"))
        }
        models[0].render()

        if (Debug.DEBUG) {
            Utilities.renderFloatingLabel("V: %.2f".format(te.node.voltage), vec3Of(0, 1, 0))
//            Utilities.renderFloatingLabel("I: %.2f".format(te.node.amperage), vec3Of(0, 1.25, 0))
//            Utilities.renderFloatingLabel("W: %.2f".format(te.node.voltage * te.node.amperage), vec3Of(0, 1.5, 0))
        }
    }
}