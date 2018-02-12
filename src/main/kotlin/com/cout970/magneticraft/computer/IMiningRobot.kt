package com.cout970.magneticraft.computer

import com.cout970.magneticraft.tileentity.modules.mining_robot.RequestStatus

/**
 * Created by cout970 on 2017/08/22.
 */
interface IMiningRobot {

    val batterySize: Int
    val batteryCharge: Int

    val status: RequestStatus
    val failReason: Int

    val orientationFlag: Int
    val cooldown: Int
    val scanResult: Int

    fun move(front: Boolean)

    fun rotateLeft()
    fun rotateRight()
    fun rotateUp()
    fun rotateDown()

    fun mine()
    fun scan()
}