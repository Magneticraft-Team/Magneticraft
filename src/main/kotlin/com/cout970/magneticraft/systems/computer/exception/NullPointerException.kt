package com.cout970.magneticraft.systems.computer.exception

import com.cout970.magneticraft.api.computer.ICPU

/**
 * Created by cout970 on 03/06/2016.
 */
class NullPointerException : ICPU.IInterruption {

    override fun getCode(): Int {
        return 6
    }

    override fun getDescription(): String {
        return "Attempt to jump to an invalid location (addr == 0)"
    }

    override fun getName(): String {
        return "NULL POINTER EXCEPTION"
    }
}
