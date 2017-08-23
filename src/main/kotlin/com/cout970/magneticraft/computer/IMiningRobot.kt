package com.cout970.magneticraft.computer

import com.cout970.magneticraft.tileentity.modules.mining_robot.RequestStatus
import com.cout970.magneticraft.tileentity.modules.mining_robot.RobotAction

/**
 * Created by cout970 on 2017/08/22.
 */
interface IMiningRobot {

    val batterySize: Int
    val batteryCharge: Int

    val requestedAction: RobotAction?
    val requestStatus: RequestStatus
    val failReason: Int

    val cooldown: Int

    fun move(front: Boolean)

    fun rotateLeft()
    fun rotateRight()
    fun rotateUp()
    fun rotateDown()

    fun mine()
}