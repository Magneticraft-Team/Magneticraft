package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.block.Machines
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.TileConveyorBelt
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.ModelCacheFactory
import com.cout970.magneticraft.tilerenderer.core.TileRenderer
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.times
import com.cout970.magneticraft.util.vector.vec3Of
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import org.lwjgl.input.Keyboard

/**
 * Created by cout970 on 2017/06/16.
 */
object TileRendererConveyorBelt : TileRenderer<TileConveyorBelt>() {

    var staticModel: ModelCache? = null
    var lateralLeft: ModelCache? = null
    var lateralRight: ModelCache? = null
    var panelLeft: ModelCache? = null
    var panelRight: ModelCache? = null
    var rollers: List<Pair<IVector3, ModelCache?>> = emptyList()


    override fun renderTileEntityAt(te: TileConveyorBelt, x: Double, y: Double, z: Double, partialTicks: Float,
                                    destroyStage: Int) {

        if (Keyboard.isKeyDown(Keyboard.KEY_1)) {
            onModelRegistryReload()
        }
        staticModel?.let { model ->

            GlStateManager.pushMatrix()
            GlStateManager.translate(x, y, z)
            Utilities.rotateFromCenter(te.facing)
            bindTexture(resource("textures/blocks/machines/conveyor_belt.png"))
            model.render()
            if (te.world.getTile<TileConveyorBelt>(te.pos.add(te.facing.rotateYCCW().directionVec)) != null) {
                lateralLeft?.render()
            } else {
                panelLeft?.render()
            }
            if (te.world.getTile<TileConveyorBelt>(te.pos.add(te.facing.rotateY().directionVec)) != null) {
                lateralRight?.render()
            } else {
                panelRight?.render()
            }
            rollers.forEach {
                if (it.second != null) {
                    val angle = te.rotation
                    te.rotation += partialTicks
                    val trans = it.first * Utilities.PIXEL

                    GlStateManager.pushMatrix()
                    GlStateManager.translate(trans.xCoord, trans.yCoord, trans.zCoord)
                    GlStateManager.rotate(-angle, 1f, 0f, 0f)
                    GlStateManager.translate(-trans.xCoord, -trans.yCoord, -trans.zCoord)
                    it.second?.render()
                    GlStateManager.popMatrix()
                }
            }
            GlStateManager.popMatrix()
        }
    }

    override fun onModelRegistryReload() {
        val loc = ModelResourceLocation(Machines.conveyorBelt.registryName, "model")
        //cleaning
        staticModel?.clear()
        rollers.forEach { it.second?.clear() }
        lateralLeft?.clear()
        lateralRight?.clear()

        staticModel = ModelCacheFactory.createCache(loc) {
            !it.startsWith("roller") && !it.startsWith("lateral") && !it.startsWith("panel")
        }
        rollers = listOf(
                vec3Of(0.0, 11, 1.25) to ModelCacheFactory.createCache(loc) { it == "roller5" }, // 1
                vec3Of(0.0, 11, 3.85) to ModelCacheFactory.createCache(loc) { it == "roller2" }, // 2
                vec3Of(0.0, 11, 6.6) to ModelCacheFactory.createCache(loc) { it == "roller3" }, // 3
                vec3Of(0.0, 11, 9.2) to ModelCacheFactory.createCache(loc) { it == "roller6" }, // 4
                vec3Of(0.0, 11, 11.9) to ModelCacheFactory.createCache(loc) { it == "roller4" }, // 5
                vec3Of(0.0, 11, 14.65) to ModelCacheFactory.createCache(loc) { it == "roller1" } // 6
        )
        lateralLeft = ModelCacheFactory.createCache(loc) { it.startsWith("lateral_left") }
        lateralRight = ModelCacheFactory.createCache(loc) { it.startsWith("lateral_right") }
        panelLeft = ModelCacheFactory.createCache(loc) { it.startsWith("panel_left") }
        panelRight = ModelCacheFactory.createCache(loc) { it.startsWith("panel_right") }
    }
}