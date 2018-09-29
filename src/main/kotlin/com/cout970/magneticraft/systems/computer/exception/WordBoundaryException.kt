package com.cout970.magneticraft.systems.computer.exception

import com.cout970.magneticraft.api.computer.ICPU
import com.cout970.magneticraft.misc.toHex

/**
 * Created by cout970 on 03/06/2016.
 */
class WordBoundaryException(val addr: Int) : ICPU.IInterruption {

    override fun getCode(): Int {
        return 4
    }

    override fun getDescription(): String {
        return "Attempt to write or read a word with and address not aligned with the word boundary ((addr & 3) != 0), Addr: ${addr.toHex()}"
    }

    override fun getName(): String {
        return "READ/WRITE NOT ALIGNED WITH WORD BOUNDARIES"
    }
}
