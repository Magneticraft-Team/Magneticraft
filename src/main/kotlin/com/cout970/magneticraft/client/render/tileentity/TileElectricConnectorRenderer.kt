package com.cout970.magneticraft.client.render.tileentity

import com.cout970.loader.api.ModelCacheFactory
import com.cout970.loader.api.model.ICachedModel
import com.cout970.loader.api.model.IModelCube
import com.cout970.loader.api.model.IModelFilter
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.tileentity.electric.TileElectricConnector
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.Vec3d
import com.google.common.base.Predicates
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 29/06/2016.
 */
object TileElectricConnectorRenderer : TileEntityRenderer<TileElectricConnector>() {

    val texture = resource("textures/models/electric_connector.png")
    var block: ICachedModel? = null
    var base: ICachedModel? = null

    override fun renderTileEntityAt(te: TileElectricConnector, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {

        //create cache for wire connections
        te.wireRender.update {
            for (i in te.wiredConnections) {
                if (i.firstNode != te.node) continue
                renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector, 0.01)
            }
        }

        pushMatrix()
        translate(x, y, z)

        //render wires
        bindTexture(WIRE_TEXTURE)
        te.wireRender.render()

        //render block
        bindTexture(texture)

        when (te.getFacing()) {
            EnumFacing.UP -> {
                customRotate(Vec3d(180, 0, 0), Vec3d(0.5, 0.5, 0.5))
            }
            EnumFacing.NORTH -> {
                customRotate(Vec3d(90, 0, 0), Vec3d(0.5, 0.5, 0.5))
            }
            EnumFacing.SOUTH -> {
                customRotate(Vec3d(-90, 0, 0), Vec3d(0.5, 0.5, 0.5))
            }
            EnumFacing.WEST -> {
                customRotate(Vec3d(0, 0, -90), Vec3d(0.5, 0.5, 0.5))
            }
            EnumFacing.EAST -> {
                customRotate(Vec3d(0, 0, 90), Vec3d(0.5, 0.5, 0.5))
            }
            else -> Unit
        }

        block?.render()
        if (te.world.isSideSolid(te.pos.offset(te.getFacing()), EnumFacing.UP)) {
            base?.render()
        }

        popMatrix()
    }

    override fun isGlobalRenderer(te: TileElectricConnector?): Boolean = true

    override fun onModelRegistryReload() {
        super.onModelRegistryReload()
        val model = getModel(resource("models/block/mcm/electric_connector.mcm"))
        val isBase = IModelFilter {
            if (it is IModelCube) it.name.contains("Base") else false
        }
        block = ModelCacheFactory.createCachedModel(model.filter(Predicates.not(isBase)), 32)
        base = ModelCacheFactory.createCachedModel(model.filter(isBase), 32)
    }
}