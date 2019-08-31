package com.cout970.magneticraft.features.electric_machines

import com.cout970.magneticraft.misc.RegisterRenderer
import com.cout970.magneticraft.misc.vector.vec3Of
import com.cout970.magneticraft.systems.tilerenderers.*
import net.minecraft.util.EnumFacing
import kotlin.math.min

/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterRenderer(TileBattery::class)
object TileRendererBattery : BaseTileRenderer<TileBattery>() {

    override fun init() {
        createModel(Blocks.battery)
    }

    override fun render(te: TileBattery) {
        Utilities.rotateFromCenter(te.facing, 180f)
        renderModel("default")
    }
}

@RegisterRenderer(TileWindTurbine::class)
object TileRendererWindTurbine : BaseTileRenderer<TileWindTurbine>() {

    override fun init() {
        createModel(Blocks.windTurbine)
    }

    override fun render(te: TileWindTurbine) {
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
        renderModel("default")
    }
}

@RegisterRenderer(TileElectricEngine::class)
object TileRendererElectricEngine : BaseTileRenderer<TileElectricEngine>() {

    override fun init() {
        createModel(Blocks.electricEngine,
                ModelSelector("animation", FilterAlways, FilterString("animation", FilterTarget.ANIMATION))
        )
    }

    override fun render(te: TileElectricEngine) {
        Utilities.rotateAroundCenter(te.facing)
        Utilities.customRotate(vec3Of(0, 0, -90), vec3Of(0.5))

        val speedUp = 0.0005
        val speedDown = speedUp * 5
        val speedMultiplier = 3.0

        // Detect if the engine is working
        val isWorking = (te.world.totalWorldTime - te.lastWorkingTick) < 20

        if (isWorking) {
            // Speed up
            if (te.animationSpeed < 1.0) {
                te.animationSpeed += speedUp
            }
        } else if (te.animationSpeed > speedDown * 2) {
            // Slowdown
            te.animationSpeed -= speedDown
        } else {
            // Slowly go back to the starting position
            te.animationSpeed = 0.0
            val length = (getModel("animation") as AnimationRenderCache).model.length * 20
            val remaining = length - (te.animationStep % length)

            if (remaining > 0 && remaining < length) {
                te.animationStep += min(speedDown * 5, remaining)
            }
        }

        // Delta time, amount of time since last animationStep update
        // Note: time is updated in BaseTileRenderer and already has partialTicks in it
        val delta = min(time - te.animationLastTime, 1000.0)
        te.animationLastTime = time

        // Advance animation
        te.animationStep += te.animationSpeed * delta * speedMultiplier
        time = te.animationStep

        renderModel("animation")
    }
}