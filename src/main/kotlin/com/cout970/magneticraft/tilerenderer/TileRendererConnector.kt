package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.block.ElectricMachines
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.TileConnector
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.ModelCacheFactory
import com.cout970.magneticraft.tilerenderer.core.TileRenderer
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 2017/06/29.
 */
object TileRendererConnector : TileRenderer<TileConnector>() {

    val texture = resource("textures/models/electric_connector.png")
    var model: ModelCache? = null
    var base: ModelCache? = null

    override fun renderTileEntityAt(te: TileConnector, x: Double, y: Double, z: Double, partialTicks: Float,
                                    destroyStage: Int) {

        te.wireRender.update {
            te.electricModule.outputWiredConnections.forEach { i ->
                Utilities.renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector, 0.035)
            }
        }

        stackMatrix {
            translate(x, y, z)
            bindTexture(Utilities.WIRE_TEXTURE)
            te.wireRender.render()

            if (te.container.shouldTick(40)) {
                te.hasBase = shouldHaveBase(te)
            }

            bindTexture(texture)
            when (te.facing.opposite) {
                EnumFacing.UP -> {
                    Utilities.customRotate(vec3Of(180, 0, 0), Vec3d(0.5, 0.5, 0.5))
                }
                EnumFacing.NORTH -> {
                    Utilities.customRotate(vec3Of(90, 0, 0), Vec3d(0.5, 0.5, 0.5))
                }
                EnumFacing.SOUTH -> {
                    Utilities.customRotate(vec3Of(-90, 0, 0), Vec3d(0.5, 0.5, 0.5))
                }
                EnumFacing.WEST -> {
                    Utilities.customRotate(vec3Of(0, 0, -90), Vec3d(0.5, 0.5, 0.5))
                }
                EnumFacing.EAST -> {
                    Utilities.customRotate(vec3Of(0, 0, 90), Vec3d(0.5, 0.5, 0.5))
                }
                else -> Unit
            }

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