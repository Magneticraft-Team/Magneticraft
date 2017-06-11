package com.cout970.magneticraft.tilerenderer

import com.cout970.loader.api.ModelCacheFactory
import com.cout970.loader.api.model.ICachedModel
import com.cout970.loader.api.model.IModelFilter
import com.cout970.loader.api.model.IModelPart
import com.cout970.loader.api.model.IObjGroup
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.api.energy.IWireConnector
import com.cout970.magneticraft.registry.ELECTRIC_NODE_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.electric.TileElectricConnector
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.vec3Of
import com.google.common.base.Predicates
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 29/06/2016.
 */
object TileRendererElectricConnector : TileEntityRenderer<TileElectricConnector>() {

    val texture = resource("textures/models/electric_connector.png")
    var block: ICachedModel? = null
    var base: ICachedModel? = null

    override fun renderTileEntityAt(te: TileElectricConnector, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int) {

        //create cache for wire connections
        te.wireRender.update {
            for (i in te.traitElectricity.outputWiredConnections) {
                renderConnection(i, i.firstNode as IWireConnector, i.secondNode as IWireConnector, 0.035)
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
                customRotate(vec3Of(180, 0, 0), Vec3d(0.5, 0.5, 0.5))
            }
            EnumFacing.NORTH -> {
                customRotate(vec3Of(90, 0, 0), Vec3d(0.5, 0.5, 0.5))
            }
            EnumFacing.SOUTH -> {
                customRotate(vec3Of(-90, 0, 0), Vec3d(0.5, 0.5, 0.5))
            }
            EnumFacing.WEST -> {
                customRotate(vec3Of(0, 0, -90), Vec3d(0.5, 0.5, 0.5))
            }
            EnumFacing.EAST -> {
                customRotate(vec3Of(0, 0, 90), Vec3d(0.5, 0.5, 0.5))
            }
            else -> Unit
        }
        //update base every 40 ticks
        if (te.tickToNextUpdate == 0) {
            te.tickToNextUpdate = 40
            te.hasBase = true
            val tile = te.world.getTileEntity(te.pos.offset(te.getFacing()))
            if (tile != null) {
                val handler = ELECTRIC_NODE_HANDLER!!.fromTile(tile, te.getFacing().opposite)
                if (handler is IElectricNodeHandler) {
                    val node = handler.nodes.firstOrNull { it is IElectricNode }
                    if (node != null && handler.canConnect(node as IElectricNode, te.traitElectricity, te.mainNode, te.getFacing().opposite)) {
                        te.hasBase = false
                    }
                }
            }
        }
        block?.render()
        if (te.hasBase) {
            base?.render()
        }
        popMatrix()
    }

    override fun isGlobalRenderer(te: TileElectricConnector?): Boolean = true

    override fun onModelRegistryReload() {
        super.onModelRegistryReload()
        try {
            val model = getModelObj(resource("models/block/obj/electric_connector.obj"))
            val isBase = object : IModelFilter {
                override fun apply(it: IModelPart?): Boolean = if (it is IObjGroup) it.getName().contains("base") else false
            }
            block = ModelCacheFactory.createCachedModel(model.filter(Predicates.not(isBase)), 1)
            base = ModelCacheFactory.createCachedModel(model.filter(isBase), 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}