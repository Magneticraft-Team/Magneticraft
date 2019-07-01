package com.cout970.magneticraft.features.computers

import com.cout970.magneticraft.misc.RegisterRenderer
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.systems.tilemodules.mining_robot.MineBlockTask
import com.cout970.magneticraft.systems.tilemodules.mining_robot.MoveRobotTask
import com.cout970.magneticraft.systems.tilemodules.mining_robot.RobotAction
import com.cout970.magneticraft.systems.tilerenderers.*


/**
 * Created by cout970 on 2017/08/10.
 */


@RegisterRenderer(TileComputer::class)
object DebugTileRenderer : BaseTileRenderer<TileComputer>() {

    override fun init() {
        createModel(Blocks.computer)
    }

    override fun render(te: TileComputer) {
        Utilities.rotateFromCenter(te.facing, 0f)
        renderModel("default")

        val item = te.invModule.inventory[0]

        if (item.isNotEmpty) {
            translate(12.5 * PIXEL, 7 * PIXEL, 5.20 * PIXEL)
            scale(1.5f, 1f, 1.5f)
            Utilities.renderItem(item)
        }
    }
}

@RegisterRenderer(TileMiningRobot::class)
object TileRendererMiningRobot : BaseTileRenderer<TileMiningRobot>() {

    override fun init() {
        createModel(Blocks.miningRobot,
            ModelSelector("drill", FilterRegex("drill.*")),
            ModelSelector("prop", FilterRegex("prop.*"))
        )
    }

    override fun render(te: TileMiningRobot) {

        val mod = te.robotControlModule
        val task = mod.task
        val norm = task?.let { Math.max(0f, mod.clientCooldown.toFloat() - ticks) / it.action.cooldown } ?: 0.0f
        val orientation = mod.clientOrientation ?: te.orientation

        // Rotations (only when rotating)
        task?.let {
            when (it.action) {
                RobotAction.ROTATE_RIGHT -> {
                    translate(0.5f, 0.5f, 0.5f)
                    rotate(norm * 90, 0f, 1f, 0f)
                    translate(-0.5f, -0.5f, -0.5f)
                }
                RobotAction.ROTATE_LEFT -> {
                    translate(0.5f, 0.5f, 0.5f)
                    rotate(-norm * 90, 0f, 1f, 0f)
                    translate(-0.5f, -0.5f, -0.5f)
                }
                else -> Unit
            }
        }

        // Rotations always
        Utilities.rotateFromCenter(orientation.direction, 180f)

        // Translation
        (task as? MoveRobotTask)?.let {

            val amount = when (it.front) {
                true -> 1 - norm
                else -> norm - 1
            }

            when (orientation.level) {
                Blocks.OrientationLevel.UP -> translate(0.0, amount.toDouble(), 0.0)
                Blocks.OrientationLevel.DOWN -> translate(0.0, -amount.toDouble(), 0.0)
                Blocks.OrientationLevel.CENTER -> translate(0.0, 0.0, amount.toDouble())
            }
        }

        // Render engines
        renderModel("prop")

        // Rotate up/down (only when rotating)
        task?.let {

            when (it.action) {
                RobotAction.ROTATE_UP -> {
                    translate(0.5f, 0.5f, 0.5f)
                    rotate(norm * 90, 1f, 0f, 0f)
                    translate(-0.5f, -0.5f, -0.5f)
                }
                RobotAction.ROTATE_DOWN -> {
                    translate(0.5f, 0.5f, 0.5f)
                    rotate(-norm * 90, 1f, 0f, 0f)
                    translate(-0.5f, -0.5f, -0.5f)
                }
                else -> Unit
            }
        }

        when (orientation.level) {
            Blocks.OrientationLevel.UP -> {
                translate(0.5f, 0.5f, 0.5f)
                rotate(-90f, 1f, 0f, 0f)
                translate(-0.5f, -0.5f, -0.5f)
            }
            Blocks.OrientationLevel.DOWN -> {
                translate(0.5f, 0.5f, 0.5f)
                rotate(90f, 1f, 0f, 0f)
                translate(-0.5f, -0.5f, -0.5f)
            }
            else -> Unit
        }

        renderModel("default")

        // Move drill
        (task as? MineBlockTask)?.let {

            translate(0.5f, 0.5f, 0.5f)
            rotate(norm * 360 * 4, 0f, 0f, 1f)
            translate(-0.5f, -0.5f, -0.5f)
        }

        renderModel("drill")
    }
}
