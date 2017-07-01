package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.ElectricMachines
import com.cout970.magneticraft.tileentity.TileBattery
import com.cout970.magneticraft.tileentity.TileElectricFurnace
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.util.resource
import net.minecraft.client.renderer.block.model.ModelResourceLocation

/**
 * Created by cout970 on 2017/07/01.
 */

object TileRendererBattery : TileRendererSimple<TileBattery>(
        modelLocation = { ModelResourceLocation(ElectricMachines.battery.registryName, "model") }
) {
    val texture = resource("textures/blocks/electric_machines/battery.png")

    override fun renderModels(models: List<ModelCache>, te: TileBattery) {
        bindTexture(texture)
        Utilities.rotateFromCenter(te.facing, 180f)
        models.forEach { it.render() }
    }
}

object TileRendererElectricFurnace : TileRendererSimple<TileElectricFurnace>(
        modelLocation = { ModelResourceLocation(ElectricMachines.electric_furnace.registryName, "model") },
        filters = listOf({ name -> name == "Shape_0" }, { name -> name == "Shape_1" })
) {
    val texture_normal = resource("textures/blocks/electric_machines/electric_furnace.png")
    val texture_off = resource("textures/blocks/electric_machines/electric_furnace_front.png")
    val texture_on = resource("textures/blocks/electric_machines/electric_furnace_front_on.png")

    override fun renderModels(models: List<ModelCache>, te: TileElectricFurnace) {
        Utilities.rotateFromCenter(te.facing, 180f)
        bindTexture(texture_normal)
        models[0].render()
        bindTexture(if (te.processModule.working) texture_on else texture_off)
        models[1].render()
    }
}