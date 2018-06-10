package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.ElectricMachines
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.TileBattery
import com.cout970.magneticraft.tileentity.TileWindTurbine
import com.cout970.magneticraft.tilerenderer.core.*
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterRenderer(TileBattery::class)
object TileRendererBattery : TileRendererSimple<TileBattery>(
        modelLocation = modelOf(ElectricMachines.battery)
) {

    override fun renderModels(models: List<ModelCache>, te: TileBattery) {
        Utilities.rotateFromCenter(te.facing, 180f)
        models.forEach { it.renderTextured() }
    }
}

@RegisterRenderer(TileWindTurbine::class)
object TileRendererWindTurbine : TileRendererSimple<TileWindTurbine>(
        modelLocation = modelOf(ElectricMachines.windTurbine)
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