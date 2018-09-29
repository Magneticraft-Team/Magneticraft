package com.cout970.magneticraft.systems.computer.exception

import com.cout970.magneticraft.api.computer.ICPU

/**
 * Created by cout970 on 03/06/2016.
 */
class InvalidInstruction : ICPU.IInterruption {

    override fun getCode(): Int {
        return 1
    }

    override fun getDescription(): String {
        return "The CPU has found an unknown instruction"
    }

    override fun getName(): String {
        return "INVALID INSTRUCTION"
    }
}
