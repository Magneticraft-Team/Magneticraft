package com.cout970.magneticraft.features.multiblocks.tilerenderers

import com.cout970.magneticraft.features.multiblocks.tileentities.TileSolarPanel
import com.cout970.magneticraft.features.multiblocks.tileentities.TileSteamEngine
import com.cout970.magneticraft.misc.RegisterRenderer
import com.cout970.magneticraft.systems.tilerenderers.*
import net.minecraft.util.EnumFacing
import com.cout970.magneticraft.features.multiblocks.Blocks as Multiblocks

@RegisterRenderer(TileSteamEngine::class)
object TileRendererSteamEngine : TileRendererMultiblock<TileSteamEngine>() {

    override fun init() {
        val gears = FilterOr(
            FilterString("gearbox_lid_side"),
            FilterString("gear_box_lid_top"),
            FilterString("gearbox_lid_lock")
        )

        val notGears = FilterAnd(
            FilterNotString("gearbox_lid_side"),
            FilterNotString("gear_box_lid_top"),
            FilterNotString("gearbox_lid_lock")
        )

        createModel(Multiblocks.steamEngine,
            ModelSelector("base", notGears),
            ModelSelector("gears", gears),

            ModelSelector("animation", notGears,
                FilterRegex("animation", FilterTarget.ANIMATION)
            )
        )
    }

    override fun render(te: TileSteamEngine) {
        Utilities.rotateFromCenter(te.facing, 0f)
        translate(-1, 0, -1)

        if (te.steamGeneratorModule.working()) {
            renderModel("animation")
        } else {
            renderModel("base")
        }

        val step = Math.max(0.0, (te.steamEngineMbModule.auxTime - ticks) / 20.0)
        val clock = if (te.steamEngineMbModule.lidOpen) 1 - step else step

        translate(-0.5 * PIXEL * clock, -2.5 * PIXEL * clock, 0)
        translate(-4 * PIXEL, 1, 0)
        rotate(-120 * clock, 0, 0, 1)
        translate(4 * PIXEL, -1, 0)
        renderModel("gears")
    }
}

@RegisterRenderer(TileSolarPanel::class)
object TileRendererSolarPanel : TileRendererMultiblock<TileSolarPanel>() {

    override fun init() {
        val parts = listOf("Panel1", "Panel2", "Panel3", "Panel4", "Panel5", "Panel6")
            .flatMap { prefix -> (1..3).map { "$prefix-$it" } }

        createModel(Multiblocks.solarPanel,
            parts.map { ModelSelector(it.toLowerCase(), FilterString(it)) })
    }

    override fun render(te: TileSolarPanel) {
        Utilities.rotateFromCenter(te.facing, -90f)
        translate(-1.0, 0.0, 0.0)
        renderModel("default")

        val worldTime = te.world.totalWorldTime
        val time = (worldTime % 24000L).toInt()
        val normTime = time / 12000f
        val preAngle = if (normTime > 1) 0f else (normTime * 2 - 1) * 30f

        val targetAngle = when (te.facing) {
            EnumFacing.NORTH -> preAngle
            EnumFacing.SOUTH -> -preAngle
            EnumFacing.WEST -> preAngle
            EnumFacing.EAST -> -preAngle
            else -> 0f
        }

        val oldTime = te.deltaTime
        te.deltaTime = System.currentTimeMillis()
        val delta = (Math.min(te.deltaTime - oldTime, 1000)) / 1000f

        val angle = te.currentAngle
        te.currentAngle += (targetAngle - te.currentAngle) * delta * 0.5f

        when (te.facing.axis) {
            EnumFacing.Axis.X -> {
                stackMatrix {
                    translate(1.5f, 11f / 16f, 0.5f)
                    rotate(angle, 0f, 0f, 1f)
                    translate(-1.5f, -11f / 16f, -0.5f)
                    renderModel("panel1-1")
                    renderModel("panel1-2")
                    renderModel("panel1-3")

                    renderModel("panel4-1")
                    renderModel("panel4-2")
                    renderModel("panel4-3")
                }
                stackMatrix {
                    translate(0.5f, 11f / 16f, 0.5f)
                    rotate(angle, 0f, 0f, 1f)
                    translate(-0.5f, -11f / 16f, -0.5f)

                    renderModel("panel2-1")
                    renderModel("panel2-2")
                    renderModel("panel2-3")

                    renderModel("panel5-1")
                    renderModel("panel5-2")
                    renderModel("panel5-3")
                }
                stackMatrix {
                    translate(-0.5f, 11f / 16f, 0.5f)
                    rotate(angle, 0f, 0f, 1f)
                    translate(0.5f, -11f / 16f, -0.5f)
                    renderModel("panel3-1")
                    renderModel("panel3-2")
                    renderModel("panel3-3")

                    renderModel("panel6-1")
                    renderModel("panel6-2")
                    renderModel("panel6-3")
                }
            }
            EnumFacing.Axis.Z -> {
                stackMatrix {
                    translate(0f, 0.75f, 1.25f)
                    rotate(angle, 1f, 0f, 0f)
                    translate(0f, -0.75f + 0.1f, -1.25f)
                    renderModel("panel1-1")
                    renderModel("panel1-2")
                    renderModel("panel1-3")
                    renderModel("panel2-1")
                    renderModel("panel2-2")
                    renderModel("panel2-3")
                    renderModel("panel3-1")
                    renderModel("panel3-2")
                    renderModel("panel3-3")
                }
                stackMatrix {
                    translate(0f, 0.75f, -0.25f)
                    rotate(angle, 1f, 0f, 0f)
                    translate(0f, -0.75f + 0.1f, 0.25f)
                    renderModel("panel4-1")
                    renderModel("panel4-2")
                    renderModel("panel4-3")
                    renderModel("panel5-1")
                    renderModel("panel5-2")
                    renderModel("panel5-3")
                    renderModel("panel6-1")
                    renderModel("panel6-2")
                    renderModel("panel6-3")
                }
            }
            else -> Unit
        }
    }
}