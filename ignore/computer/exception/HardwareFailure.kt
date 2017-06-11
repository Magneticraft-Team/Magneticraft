package com.cout970.magneticraft.computer.exception

import com.cout970.magneticraft.api.computer.ICPU

/**
 * Created by cout970 on 03/06/2016.
 */
@Suppress("unused")
class HardwareFailure : ICPU.IInterruption {

    override fun getCode(): Int {
        return 0
    }

    override fun getDescription(): String {
        return "An unknown error has occurred with some hardware component"
    }

    override fun getName(): String {
        return "HARDWARE ERROR"
    }
}
