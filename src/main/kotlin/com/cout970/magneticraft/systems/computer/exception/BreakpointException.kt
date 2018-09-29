package com.cout970.magneticraft.systems.computer.exception

import com.cout970.magneticraft.api.computer.ICPU

class BreakpointException : ICPU.IInterruption {

    override fun getCode(): Int {
        return 7
    }

    override fun getDescription(): String {
        return "Reached a breakpoint"
    }

    override fun getName(): String {
        return "Breakpoint"
    }
}
