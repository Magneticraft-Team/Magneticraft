package com.cout970.magneticraft.features.electric_conductors

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.misc.RegisterRenderer
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.misc.vector.lowercaseName
import com.cout970.magneticraft.misc.vector.minus
import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.systems.tilerenderers.BaseTileRenderer
import com.cout970.magneticraft.systems.tilerenderers.FilterRegex
import com.cout970.magneticraft.systems.tilerenderers.ModelSelector
import com.cout970.magneticraft.systems.tilerenderers.Utilities
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterRenderer(TileConnector::class)
object TileRendererConnector : BaseTileRenderer<TileConnector>() {

    override fun init() {
        createModel(Blocks.connector, ModelSelector("base", FilterRegex("Base")))
    }

    override fun render(te: TileConnector) {
        //updated the cache for rendering wires
        te.wireRender.update {
            te.electricModule.outputWiredConnections.forEach { i ->
                Utilities.renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector, 0.035)
            }
        }

        TileRendererConnector.bindTexture(Utilities.WIRE_TEXTURE)
        te.wireRender.render()

        if (Debug.DEBUG) {

//            val player = Minecraft.getMinecraft().player
//            val box = player.entityBoundingBox.center - te.pos.toVec3d()
//
//            Utilities.drawWireBetween(te.wrapper.connectors().first(), box, 0.035)

            Utilities.renderFloatingLabel("V: %.2f".format(te.node.voltage), vec3Of(0, 1, 0))
            Utilities.renderFloatingLabel("I: %.2f".format(te.node.amperage), vec3Of(0, 1.25, 0))
            Utilities.renderFloatingLabel("W: %.2f".format(te.node.voltage * te.node.amperage), vec3Of(0, 1.5, 0))
//            Utilities.renderFloatingLabel("R: %.2f".format(te.node.resistance), vec3Of(0, 1.75, 0))
        }

        Utilities.rotateAroundCenter(te.facing)
        renderModel("default")
        if (te.hasBase) {
            renderModel("base")
        }
    }
}

@RegisterRenderer(TileElectricPole::class)
object TileRendererElectricPole : BaseTileRenderer<TileElectricPole>() {

    override fun init() {
        createModel(Blocks.electricPole)
    }

    override fun render(te: TileElectricPole) {

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

        val orientation = te.getBlockState()[Blocks.PROPERTY_POLE_ORIENTATION]
        Utilities.rotateFromCenter(EnumFacing.UP, (orientation?.angle ?: 0f) + 90f)
        renderModel("default")
    }
}

@RegisterRenderer(TileElectricPoleTransformer::class)
object TileRendererElectricPoleTransformer : BaseTileRenderer<TileElectricPoleTransformer>() {

    override fun init() {
        createModel(Blocks.electricPoleTransformer)
    }

    override fun render(te: TileElectricPoleTransformer) {

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

        val orientation = te.getBlockState()[Blocks.PROPERTY_POLE_ORIENTATION]
        Utilities.rotateFromCenter(EnumFacing.UP, (orientation?.angle ?: 0f) + 90f)
        renderModel("default")
    }
}

@RegisterRenderer(TileElectricCable::class)
object TileRendererElectricCable : BaseTileRenderer<TileElectricCable>() {

    override fun init() {
        createModel(Blocks.electricCable,
            ModelSelector("up", FilterRegex("up")),
            ModelSelector("down", FilterRegex("down")),
            ModelSelector("north", FilterRegex("north")),
            ModelSelector("south", FilterRegex("south")),
            ModelSelector("west", FilterRegex("west")),
            ModelSelector("east", FilterRegex("east"))
        )

        createModelWithoutTexture(Blocks.electricCable,
            ModelSelector("center", FilterRegex("center"))
        )
    }

    override fun render(te: TileElectricCable) {
        var conn: EnumFacing? = null
        var count = 0

        EnumFacing.values().forEach { side ->
            if (te.canConnect(side)) {
                renderModel(side.lowercaseName)
                conn = conn ?: side.opposite
                count++
            }
        }

        if (count == 1) {
            bindTexture(resource("textures/blocks/electric_connectors/electric_cable_center_${conn!!.name.toLowerCase()}.png"))
        } else {
            bindTexture(resource("textures/blocks/electric_connectors/electric_cable_center.png"))
        }
        renderModel("center")

        if (Debug.DEBUG) {
            Utilities.renderFloatingLabel("V: %.2f".format(te.node.voltage), vec3Of(0, 1, 0))
//            Utilities.renderFloatingLabel("I: %.2f".format(te.node.amperage), vec3Of(0, 1.25, 0))
//            Utilities.renderFloatingLabel("W: %.2f".format(te.node.voltage * te.node.amperage), vec3Of(0, 1.5, 0))
        }
    }
}