package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.block.ElectricMachines
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.TileConnector
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.ModelCacheFactory
import com.cout970.magneticraft.tilerenderer.core.TileRenderer
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.util.resource
import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * Created by cout970 on 2017/06/29.
 */
object TileRendererConnector : TileRenderer<TileConnector>() {

    val texture = resource("textures/models/electric_connector.png")
    var model: ModelCache? = null
    var base: ModelCache? = null

    override fun renderTileEntityAt(te: TileConnector, x: Double, y: Double, z: Double, partialTicks: Float,
                                    destroyStage: Int) {

        //updated the cache for rendering wires
        te.wireRender.update {
            te.electricModule.outputWiredConnections.forEach { i ->
                Utilities.renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector, 0.035)
            }
        }

        stackMatrix {
            translate(x, y, z)
            bindTexture(Utilities.WIRE_TEXTURE)
            te.wireRender.render()

            bindTexture(texture)
            Utilities.rotateAroundCenter(te.facing)
            model?.render()

            if (te.hasBase) {
                base?.render()
            }
        }
    }

    fun shouldHaveBase(te: TileConnector): Boolean {
        val tile = te.world.getTileEntity(te.pos.offset(te.facing))
        if (tile != null) {
            val handler = ELECTRIC_NODE_HANDLER!!.fromTile(tile, te.facing.opposite)
            if (handler is IElectricNodeHandler) {
                val node = handler.nodes.firstOrNull { it is IElectricNode }
                if (node != null && handler.canConnect(node as IElectricNode, te.electricModule, te.wrapper,
                        te.facing.opposite)) {
                    return false
                }
            }
        }
        return true
    }

    override fun isGlobalRenderer(te: TileConnector?): Boolean = true

    override fun onModelRegistryReload() {
        val loc = ModelResourceLocation(ElectricMachines.connector.registryName, "model")

        model?.clear()
        base?.clear()
        model = ModelCacheFactory.createCache(loc) { !it.startsWith("Base") }
        base = ModelCacheFactory.createCache(loc) { it.startsWith("Base") }
    }
}