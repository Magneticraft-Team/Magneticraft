package com.cout970.magneticraft.systems.tilemodules.mining_robot

/**
 * Created by cout970 on 2017/08/23.
 */
enum class RequestStatus {
    RUNNING,
    FAILED,
    SUCCESSFUL
}

object FailReason {
    val NO_FAIL = 0
    val NO_ENERGY = 1
    val BLOCKED = 2
    val UNBREAKABLE = 3
    val LIMIT_REACHED = 4
    val INVENTORY_FULL = 5
    val AIR = 6
}

val RequestStatus?.isFinished: Boolean get() = this == RequestStatus.FAILED || this == RequestStatus.SUCCESSFUL
val RequestStatus?.isNotFinished: Boolean get() = !isFinished