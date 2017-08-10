package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.Computers
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.TileComputer
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.tilerenderer.core.TileRendererSimple
import com.cout970.magneticraft.tilerenderer.core.Utilities
import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterRenderer(TileComputer::class)
object TileRendererComputer : TileRendererSimple<TileComputer>(
        modelLocation = { ModelResourceLocation(Computers.computer.registryName, "model") }
) {
    override fun renderModels(models: List<ModelCache>, te: TileComputer) {
        Utilities.rotateFromCenter(te.facing, 0f)
        models.forEach { it.renderTextured() }
        val item = te.invModule.inventory[0]

        if (item.isNotEmpty) {
            rotate(90, 1, 0, 0)
            translate(3 * PIXEL, 7 * PIXEL, -4 * PIXEL)
            Utilities.renderItem(item)
        }
    }
}
