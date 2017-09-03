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

@RegisterRenderer(TileMiningRobot::class)
object TileRendererMiningRobot : TileRendererSimple<TileMiningRobot>(
        modelLocation = { ModelResourceLocation(Computers.miningRobot.registryName, "model") },
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
