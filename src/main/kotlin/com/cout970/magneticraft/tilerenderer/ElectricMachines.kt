package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.ElectricMachines
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.TileBattery
import com.cout970.magneticraft.tileentity.TileElectricFurnace
import com.cout970.magneticraft.tileentity.TileWindTurbine
import com.cout970.magneticraft.tilerenderer.core.ModelCache
import com.cout970.magneticraft.tilerenderer.core.PIXEL
import com.cout970.magneticraft.tilerenderer.core.TileRendererSimple
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.util.resource
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterRenderer(TileBattery::class)
object TileRendererBattery : TileRendererSimple<TileBattery>(
        modelLocation = { ModelResourceLocation(ElectricMachines.battery.registryName, "model") }
) {

    override fun renderModels(models: List<ModelCache>, te: TileBattery) {
        Utilities.rotateFromCenter(te.facing, 180f)
        models.forEach { it.renderTextured() }
    }
}

@RegisterRenderer(TileElectricFurnace::class)
object TileRendererElectricFurnace : TileRendererSimple<TileElectricFurnace>(
        modelLocation = { ModelResourceLocation(ElectricMachines.electricFurnace.registryName, "model") },
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

@RegisterRenderer(TileWindTurbine::class)
object TileRendererWindTurbine : TileRendererSimple<TileWindTurbine>(
        modelLocation = { ModelResourceLocation(ElectricMachines.windTurbine.registryName, "model") }
) {

    override fun renderModels(models: List<ModelCache>, te: TileWindTurbine) {
        if (!te.windTurbineModule.hasTurbineHitbox) return
        Utilities.rotateFromCenter(te.facing, 180f)
        translate(0, -5, 1)

        var angle = te.windTurbineModule.rotation
        angle += te.windTurbineModule.rotationSpeed * ticks

        if (te.facing.axisDirection == EnumFacing.AxisDirection.NEGATIVE) {
            angle = -angle
        }

        translate(0.5, 5.5, 0)
        rotate(angle, 0, 0, 1)
        translate(-0.5, -5.5 + 2 * PIXEL, 0)
        models[0].renderTextured()
    }
}











