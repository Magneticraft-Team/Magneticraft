package com.cout970.magneticraft.features.electric_conductors

import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.features.items.ToolItems
import com.cout970.magneticraft.misc.RegisterRenderer
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.getBlockPos
import com.cout970.magneticraft.misc.hasKey
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.misc.vector.lowercaseName
import com.cout970.magneticraft.misc.vector.minus
import com.cout970.magneticraft.misc.vector.toVec3d
import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.systems.tilerenderers.BaseTileRenderer
import com.cout970.magneticraft.systems.tilerenderers.FilterRegex
import com.cout970.magneticraft.systems.tilerenderers.ModelSelector
import com.cout970.magneticraft.systems.tilerenderers.Utilities
import net.minecraft.client.Minecraft
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

        bindTexture(Utilities.WIRE_TEXTURE)
        te.wireRender.render()

        val player = Minecraft.getMinecraft().player

        if (player.heldItemMainhand.item == ToolItems.voltmeter ||
                player.heldItemOffhand.item == ToolItems.voltmeter) {

            if (te.pos.distanceSq(player.position) < 8 * 8) {
                val text = "%.2fV %.2fA %.2fW".format(te.node.voltage, te.node.amperage, te.node.voltage * te.node.amperage)
                Utilities.renderFloatingLabel(text, vec3Of(0.5, 0.2, 0.5))
            }

        } else if (player.heldItemMainhand.item == ToolItems.copperCoil ||
                player.heldItemOffhand.item == ToolItems.copperCoil) {

            val stack = if (player.heldItemMainhand.isNotEmpty) {
                player.heldItemMainhand
            } else {
                player.heldItemOffhand
            }

            if (stack.hasKey(ToolItems.CopperCoil.POSITION_KEY)) {
                val basePos = stack.getBlockPos(ToolItems.CopperCoil.POSITION_KEY)

                if (basePos == te.pos) {
                    val oldPos = player.positionVector

                    player.setPosition(
                            player.posX + (player.posX - player.prevPosX) * ticks,
                            player.posY + (player.posY - player.prevPosY) * ticks,
                            player.posZ + (player.posZ - player.prevPosZ) * ticks
                    )

                    val box = player.entityBoundingBox.center - te.pos.toVec3d()

                    player.setPosition(
                            oldPos.x,
                            oldPos.y,
                            oldPos.z
                    )

                    if (basePos.distanceSq(player.position) < 10 * 10) {
                        Utilities.drawWireBetween(te.wrapper.connectors().first(), box, 0.035)
                    }
                }
            }
        }

        Utilities.rotateAroundCenter(te.facing)
        renderModel("default")
        if (te.hasBase) {
            renderModel("base")
        }
    }

    override fun isGlobalRenderer(te: TileConnector): Boolean {
        return te.electricModule.outputWiredConnections.isNotEmpty()
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

//        if (Debug.DEBUG) {
//            Utilities.renderFloatingLabel("V: %.2f".format(te.node.voltage), vec3Of(0, 1, 0))
//            Utilities.renderFloatingLabel("I: %.2f".format(te.node.amperage), vec3Of(0, 1.25, 0))
//            Utilities.renderFloatingLabel("W: %.2f".format(te.node.voltage * te.node.amperage), vec3Of(0, 1.5, 0))
//        }
    }
}

@RegisterRenderer(TileEnergyReceiver::class)
object TileRendererEnergyReceiver : BaseTileRenderer<TileEnergyReceiver>() {

    override fun init() {
        createModel(Blocks.energyReceiver)
    }

    override fun render(te: TileEnergyReceiver) {
        Utilities.rotateAroundCenter(te.facing)
        renderModel("default")
    }
}