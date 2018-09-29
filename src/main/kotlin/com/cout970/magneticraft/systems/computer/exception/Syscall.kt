package com.cout970.magneticraft.systems.computer.exception

import com.cout970.magneticraft.api.computer.ICPU

/**
 * Created by cout970 on 03/06/2016.
 */
class Syscall : ICPU.IInterruption {

    override fun getCode(): Int {
        return 3
    }

    override fun getDescription(): String {
        return "The current process has made a system call"
    }

    override fun getName(): String {
        return "SYSCALL"
    }
}
