package com.cout970.magneticraft.systems.tilemodules.mining_robot

/**
 * Created by cout970 on 2017/08/23.
 */

enum class RobotAction(val cooldown: Int, val taskFactory: () -> RobotTask) {
    MOVE_FRONT(5, { MoveRobotTask(true) }),
    MOVE_BACK(10, { MoveRobotTask(false) }),
    ROTATE_LEFT(5, { RotateLeftTask() }),
    ROTATE_RIGHT(5, { RotateRightTask() }),
    ROTATE_UP(5, { RotateUpTask() }),
    ROTATE_DOWN(5, { RotateDownTask() }),
    MINE(10, { MineBlockTask() }),
}