package com.cout970.magneticraft.tilerenderer.custom

import com.cout970.magneticraft.block.AutomaticMachines
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.TileInserter
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.ModelCacheFactory
import com.cout970.magneticraft.tilerenderer.core.TileRenderer
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.util.resource
import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * Created by cout970 on 2017/06/20.
 */
@RegisterRenderer(TileInserter::class)
object TileRendererInserter : TileRenderer<TileInserter>() {

    val texture = resource("textures/blocks/machines/inserter.png")

    var model: ModelCache? = null

    override fun renderTileEntityAt(te: TileInserter, x: Double, y: Double, z: Double, partialTicks: Float,
                                    destroyStage: Int) {
        // error loading the model
        model ?: return

        stackMatrix {
            translate(x, y, z)
            Utilities.rotateFromCenter(te.facing, 180f)
            bindTexture(texture)
            model?.render()
        }
    }

    override fun onModelRegistryReload() {
        val loc = ModelResourceLocation(AutomaticMachines.inserter.registryName, "model")
        //cleaning
        model?.clear()

        model = ModelCacheFactory.createCache(loc)
    }

//    enum class AnimationState(
//            val isRotating: Boolean = false,
//            val rotationLeft: Boolean = true,
//            val isRetracting: Boolean = false,
//            val isShort: Boolean = false
//    ) {
//        ROTATING(isRotating = true, rotationLeft = true),
//        ROTATING_INV(isRotating = true, rotationLeft = false),
//
//        RETRACTING_SHORT(isRetracting = true, isShort = true),
//        EXTENDING_SHORT(isRetracting = false, isShort = true),
//        RETRACTING_INV_SHORT(isRetracting = true, isShort = true),
//        EXTENDING_INV_SHORT(isRetracting = false, isShort = true),
//        RETRACTING_LARGE(isRetracting = true),
//        EXTENDING_LARGE(isRetracting = false),
//        RETRACTING_INV_LARGE(isRetracting = true),
//        EXTENDING_INV_LARGE(isRetracting = false),
//
//        DROP_ITEM_SHORT(isShort = true),
//        GRAB_ITEM_SHORT(isShort = true),
//        DROP_ITEM_LARGE,
//        GRAB_ITEM_LARGE
//    }
}
