package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.ElectricMachines
import com.cout970.magneticraft.tileentity.TileBattery
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.ModelCacheFactory
import com.cout970.magneticraft.tilerenderer.core.TileRenderer
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.util.resource
import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * Created by cout970 on 2017/06/30.
 */
object TileRendererBattery : TileRenderer<TileBattery>() {

    val texture = resource("textures/blocks/electric_machines/battery.png")

    var model: ModelCache? = null

    override fun renderTileEntityAt(te: TileBattery, x: Double, y: Double, z: Double, partialTicks: Float,
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
        val loc = ModelResourceLocation(ElectricMachines.battery.registryName, "model")
        //cleaning
        model?.clear()

        model = ModelCacheFactory.createCache(loc)
    }
}