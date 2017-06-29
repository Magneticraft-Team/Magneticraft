package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.Machines
import com.cout970.magneticraft.tileentity.TileInserter
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.ModelCacheFactory
import com.cout970.magneticraft.tilerenderer.core.TileRenderer
import com.cout970.magneticraft.util.resource
import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * Created by cout970 on 2017/06/20.
 */
object TileRendererInserter : TileRenderer<TileInserter>() {

    val texture = resource("textures/blocks/machines/inserter.png")

    var baseFix: ModelCache? = null
    var baseRot: ModelCache? = null
    var armLower: ModelCache? = null
    var armMid: ModelCache? = null
    var armUpper: ModelCache? = null
    var piston: ModelCache? = null
    var pistonExtension: ModelCache? = null
    var clawLeft: ModelCache? = null
    var clawRight: ModelCache? = null

    override fun renderTileEntityAt(te: TileInserter, x: Double, y: Double, z: Double, partialTicks: Float,
                                    destroyStage: Int) {
        // error loading the model
        baseFix ?: return

        stackMatrix {
            translate(x, y, z)
            bindTexture(texture)
            baseFix?.render()
        }
    }

    override fun onModelRegistryReload() {
        val loc = ModelResourceLocation(Machines.inserter.registryName, "model")
        //cleaning
        baseFix?.clear()
        baseRot?.clear()
        armLower?.clear()
        armMid?.clear()
        armUpper?.clear()
        piston?.clear()
        pistonExtension?.clear()
        clawLeft?.clear()
        clawRight?.clear()

        baseFix = ModelCacheFactory.createCache(loc) { it == "Base1" }
        baseRot = ModelCacheFactory.createCache(loc) { it == "Base2" }
        armLower = ModelCacheFactory.createCache(loc) { it.startsWith("ArmLower") }
        armMid = ModelCacheFactory.createCache(loc) { it == "ArmMid" }
        armUpper = ModelCacheFactory.createCache(loc) { it.startsWith("ArmUpper") }
        piston = ModelCacheFactory.createCache(loc) { it == "Piston" }
        pistonExtension = ModelCacheFactory.createCache(loc) { it.startsWith("Extender") }
        clawLeft = ModelCacheFactory.createCache(loc) { it == "ClawP4" }
        clawRight = ModelCacheFactory.createCache(loc) { it == "ClawP3" }
    }

    enum class AnimationState(
            val isRotating: Boolean = false,
            val rotationLeft: Boolean = true,
            val isRetracting: Boolean = false,
            val isShort: Boolean = false
    ) {
        ROTATING(isRotating = true, rotationLeft = true),
        ROTATING_INV(isRotating = true, rotationLeft = false),

        RETRACTING_SHORT(isRetracting = true, isShort = true),
        EXTENDING_SHORT(isRetracting = false, isShort = true),
        RETRACTING_INV_SHORT(isRetracting = true, isShort = true),
        EXTENDING_INV_SHORT(isRetracting = false, isShort = true),
        RETRACTING_LARGE(isRetracting = true),
        EXTENDING_LARGE(isRetracting = false),
        RETRACTING_INV_LARGE(isRetracting = true),
        EXTENDING_INV_LARGE(isRetracting = false),

        DROP_ITEM_SHORT(isShort = true),
        GRAB_ITEM_SHORT(isShort = true),
        DROP_ITEM_LARGE,
        GRAB_ITEM_LARGE
    }
}
