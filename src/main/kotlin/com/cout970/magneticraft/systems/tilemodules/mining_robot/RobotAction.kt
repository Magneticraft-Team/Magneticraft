package com.cout970.magneticraft.systems.tilemodules.mining_robot

/**
 * Created by cout970 on 2017/08/23.
 */

enum class RobotAction(val cooldown: Int, val taskFactory: () -> RobotTask) {
    MOVE_FRONT(10, { MoveRobotTask(true) }),
    MOVE_BACK(20, { MoveRobotTask(false) }),
    ROTATE_LEFT(10, { RotateLeftTask() }),
    ROTATE_RIGHT(10, { RotateRightTask() }),
    ROTATE_UP(10, { RotateUpTask() }),
    ROTATE_DOWN(10, { RotateDownTask() }),
    MINE(20, { MineBlockTask() }),
}