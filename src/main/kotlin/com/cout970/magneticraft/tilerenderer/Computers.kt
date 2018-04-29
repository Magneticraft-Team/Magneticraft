package com.cout970.magneticraft.tilerenderer

import com.cout970.magneticraft.block.Computers
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.tileentity.RegisterRenderer
import com.cout970.magneticraft.tileentity.TileComputer
import com.cout970.magneticraft.tileentity.TileMiningRobot
import com.cout970.magneticraft.tileentity.modules.mining_robot.MineBlockTask
import com.cout970.magneticraft.tileentity.modules.mining_robot.MoveRobotTask
import com.cout970.magneticraft.tileentity.modules.mining_robot.RobotAction
import com.cout970.magneticraft.tilerenderer.core.*


/**
 * Created by cout970 on 2017/08/10.
 */

@RegisterRenderer(TileComputer::class)
object TileRendererComputer : TileRendererSimple<TileComputer>(
        modelLocation = modelOf(Computers.computer)
) {
    override fun renderModels(models: List<ModelCache>, te: TileComputer) {
        Utilities.rotateFromCenter(te.facing, 0f)
        models.forEach { it.renderTextured() }
        val item = te.invModule.inventory[0]

        if (item.isNotEmpty) {
            translate(12.5 * PIXEL, 7 * PIXEL, 5.20 * PIXEL)
            scale(1.5f, 1f, 1.5f)
            Utilities.renderItem(item)
        }
    }
}

@RegisterRenderer(TileMiningRobot::class)
object TileRendererMiningRobot : TileRendererSimple<TileMiningRobot>(
        modelLocation = modelOf(Computers.miningRobot),
        filters = listOf<(String) -> Boolean>(
                { !it.contains("drill") && !it.contains("prop") },
                { it.contains("drill") },
                { it.contains("prop") }
        )
) {

    override fun renderModels(models: List<ModelCache>, te: TileMiningRobot) {

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

            val level = orientation.level
            when (level) {
                Computers.OrientationLevel.UP -> translate(0.0, amount.toDouble(), 0.0)
                Computers.OrientationLevel.DOWN -> translate(0.0, -amount.toDouble(), 0.0)
                Computers.OrientationLevel.CENTER -> translate(0.0, 0.0, amount.toDouble())
            }
        }

        // Render engines
        models[2].renderTextured()

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
            Computers.OrientationLevel.UP -> {
                translate(0.5f, 0.5f, 0.5f)
                rotate(-90f, 1f, 0f, 0f)
                translate(-0.5f, -0.5f, -0.5f)
            }
            Computers.OrientationLevel.DOWN -> {
                translate(0.5f, 0.5f, 0.5f)
                rotate(90f, 1f, 0f, 0f)
                translate(-0.5f, -0.5f, -0.5f)
            }
            else -> Unit
        }

        models[0].renderTextured()

        // Move drill
        (task as? MineBlockTask)?.let {

            translate(0.5f, 0.5f, 0.5f)
            rotate(norm * 360 * 4, 0f, 0f, 1f)
            translate(-0.5f, -0.5f, -0.5f)
        }

        models[1].renderTextured()
    }
}
