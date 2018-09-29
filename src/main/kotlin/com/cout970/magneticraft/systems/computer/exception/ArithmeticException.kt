package com.cout970.magneticraft.systems.computer.exception

import com.cout970.magneticraft.api.computer.ICPU

/**
 * Created by cout970 on 03/06/2016.
 */
class ArithmeticException : ICPU.IInterruption {

    override fun getCode(): Int {
        return 2
    }

    override fun getDescription(): String {
        return "Attempt to divide by 0"
    }

    override fun getName(): String {
        return "ARITHMETIC EXCEPTION"
    }
}
