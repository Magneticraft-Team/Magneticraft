package com.cout970.magneticraft.computer

import com.cout970.magneticraft.api.computer.ICPU
import com.cout970.magneticraft.api.computer.IMotherboard
import com.cout970.magneticraft.computer.exception.*
import java.util.*


/**
 * Created by cout970 on 2016/09/30.
 */
class CPU_MIPS : ICPU {

    private var motherboard: IMotherboard? = null

    // debug mode to see where the emulator fails
    var debugLevel = 0

    //cpu registers
    var registers = IntArray(32)
    var regHI = 0
    var regLO = 0
    var regPC = 0
    // https://wiki.osdev.org/MIPS_Overview
    //exceptions registers
    var regStatus = 0
    var regCause = 0
    var regEPC = 0
    //prefetch PC, used to read the current instruction
    var pfPC = 0
    //used to implement delayed branch, if the value is -1 no jump should be performed,
    //otherwise the value should be the effective jump address
    var jump = -1

    @Suppress("NOTHING_TO_INLINE")
    inline fun getRegister(t: Int): Int {
        return registers[t]
    }

    fun setRegister(s: Int, value: Int) {
        //reg Zero is hardwire to ground, so it always has the value 0
        if (s == 0) {
            return
        }
        if (debugLevel > 3) {
            log("Reg: %s, change from 0x%08x (%d) to 0x%08x (%d)", registerNames[s], registers[s], registers[s], value,
                    value)
        }
        registers[s] = value
    }

    override fun reset() {
        //reset registers
        Arrays.fill(registers, 0)
        regHI = 0
        regLO = 0
        regPC = Motherboard.CPU_START_POINT
        regStatus = 0x0000FFFF
        regCause = 0
        regEPC = 0

        debugLevel = 2
    }

    enum class InstructionType {
        R, I, J, EXCEPTION, COPROCESSOR, NOOP
    }

    override fun iterate() {
        val bus = motherboard?.bus!!
        //FETCH INSTRUCTION
        pfPC = regPC
        val instruct = bus.readWord(regPC)
        //increment PC or perform a jump
        if (jump != -1) {
            regPC = jump
            jump = -1
        } else {
            regPC += 4
        }
        //DEBUG prints the instruction info in the log
        if (debugLevel > 2) {
            debugInst(instruct)
        }

        //DECODE INSTRUCTION
        val opcode = instruct ushr 26
        val type: InstructionType

        type = when {
            instruct == 0 -> InstructionType.NOOP                                   // no action
            instruct == 0x0000000c || opcode == 0x10 -> InstructionType.EXCEPTION   // exception/syscall/trap
            (opcode ushr 2) == 0x04 -> InstructionType.COPROCESSOR
            opcode == 0 -> InstructionType.R                                        // type R
            opcode == 0x2 || opcode == 0x3 -> InstructionType.J                     // type J
            else -> InstructionType.I                                               // type I
        }

        //EXECUTE INSTRUCTION and WRITEBACK
        when (type) {
            InstructionType.R -> {
                //aux vars used to calculate unsigned operations without having overflow
                val m1: Long
                val m2: Long
                val mt: Long

                val func = instruct and 0b111111        // getBitsFromInt(instruct, 0, 5, false)
                val shamt = instruct ushr 6 and 0b11111 //getBitsFromInt(instruct, 6, 10, false)
                val rd = instruct ushr 11 and 0b11111   //getBitsFromInt(instruct, 11, 15, false)
                val rt = instruct ushr 16 and 0b11111   //getBitsFromInt(instruct, 16, 20, false)
                val rs = instruct ushr 21 and 0b11111   //getBitsFromInt(instruct, 21, 25, false)

                when (func) {

                    0x0//sll
                    -> setRegister(rd, getRegister(rt) shl shamt)
                    0x2//srl
                    -> setRegister(rd, getRegister(rt).ushr(shamt))
                    0x3//sra
                    -> setRegister(rd, getRegister(rt) shr shamt)
                    0x4//sllv
                    -> setRegister(rd, getRegister(rt) shl getRegister(rs))
                    0x6//srlv
                    -> setRegister(rd, getRegister(rt) ushr getRegister(rs))
                    0x7//srav
                    -> setRegister(rd, getRegister(rt) shr getRegister(rs))
                    0x8//jr
                    -> {
                        if (getRegister(rs) == 0 || getRegister(rs) == -1) {
                            interrupt(NullPointerException())
                            return
                        }
                        jump = getRegister(rs)
                    }
                    0x9//jalr
                    -> {
                        if (getRegister(rs) == -1 || getRegister(rs) == 0) {
                            interrupt(NullPointerException())
                        }
                        if (rt == 0) {
                            setRegister(31, regPC)
                        } else {
                            setRegister(rt, regPC)
                        }
                        jump = getRegister(rs)
                    }

                    0x0d // break
                    -> interrupt(BreakpointException())
                    0x10//mfhi
                    -> setRegister(rd, regHI)
                    0x11//mthi
                    -> regHI = getRegister(rd)
                    0x12//mflo
                    -> setRegister(rd, regLO)
                    0x13//mtlo
                    -> regLO = getRegister(rd)

                    0x18//mult
                    -> {
                        m1 = getRegister(rs).toLong()
                        m2 = getRegister(rt).toLong()
                        mt = m1 * m2
                        regLO = mt.toInt()
                        regHI = (mt shr 32).toInt()
                    }
                    0x19//multu
                    -> {
                        m1 = getRegister(rs).toLong() and 0xFFFF_FFFF
                        m2 = getRegister(rt).toLong() and 0xFFFF_FFFF
                        mt = m1 * m2
                        regLO = mt.toInt()
                        regHI = (mt shr 32).toInt()
                    }
                    0x1a//div
                    -> if (getRegister(rt) != 0) {
                        regLO = getRegister(rs) / getRegister(rt)
                        regHI = getRegister(rs) % getRegister(rt)
                    } else {
                        interrupt(ArithmeticException())
                    }
                    0x1b//divu
                    -> {
                        m1 = getRegister(rs).toLong() and 0xFFFF_FFFF
                        m2 = getRegister(rt).toLong() and 0xFFFF_FFFF
                        if (m2 == 0L) {
                            interrupt(ArithmeticException())
                        } else {
                            regLO = (m1 / m2).toInt()
                            regHI = (m1 % m2).toInt()
                        }
                    }
                    0x20//add
                    -> {
                        // TODO throw exception on overflow
                        setRegister(rd, getRegister(rt) + getRegister(rs))
                    }
                    0x21//addu
                    -> setRegister(rd, getRegister(rt) + getRegister(rs))
                    0x22//sub
                    -> setRegister(rd, getRegister(rs) - getRegister(rt))
                    0x23//subu
                    -> setRegister(rd, getRegister(rs) - getRegister(rt))
                    0x24//and
                    -> setRegister(rd, getRegister(rt) and getRegister(rs))
                    0x25//or
                    -> setRegister(rd, getRegister(rt) or getRegister(rs))
                    0x26//xor
                    -> setRegister(rd, getRegister(rt) xor getRegister(rs))
                    0x27//nor
                    -> setRegister(rd, (getRegister(rt) or getRegister(rs)).inv())
                    0x2a//slt
                    -> setRegister(rd, if (getRegister(rs) < getRegister(rt)) 1 else 0)
                    0x2b//sltu
                    -> {
                        m1 = getRegister(rs).toLong() and 0xFFFF_FFFF
                        m2 = getRegister(rt).toLong() and 0xFFFF_FFFF
                        setRegister(rd, if (m1 < m2) 1 else 0)
                    }
                    else -> interrupt(InvalidInstruction())
                }
            }
            InstructionType.J -> {
                val dir = getBitsFromInt(instruct, 0, 25, false)

                when (opcode) {
                    0x2//j
                    -> {
                        jump = regPC
                        jump = jump and 0xF0000000.toInt()
                        jump = jump or (dir shl 2)
                    }
                    0x3//jal
                    -> {
                        setRegister(31, regPC + 4)
                        jump = regPC
                        jump = jump and 0xF0000000.toInt()
                        jump = jump or (dir shl 2)
                    }
                }
            }
            InstructionType.I -> {
                //aux vars for unsigned operations
                val m1: Long
                val m2: Long

                val rs = instruct shr 21 and 31
                val rt = instruct shr 16 and 31

                val inmed = instruct shl 16 shr 16
                val inmedU = instruct shl 16 ushr 16

                when (opcode) {
                    0x1 -> when (rt) {
                    //bltz
                        0b00000 -> if (getRegister(rs) < 0) {
                            jump = regPC + (inmed shl 2)
                        }
                    //bgez
                        0b00001 -> if (getRegister(rs) >= 0) {
                            jump = regPC + (inmed shl 2)
                        }
                    //bltzal
                        0b10000 -> if (getRegister(rs) < 0) {
                            setRegister(31, regPC + 4)
                            jump = regPC + (inmed shl 2)
                        }
                    //bgezal
                        0b10001 -> if (getRegister(rs) >= 0) {
                            setRegister(31, regPC + 4)
                            jump = regPC + (inmed shl 2)
                        }
                    }
                    0x4//beq
                    -> if (getRegister(rt) == getRegister(rs)) {
                        jump = regPC + (inmed shl 2)
                    }
                    0x5//bne
                    -> if (getRegister(rt) != getRegister(rs)) {
                        jump = regPC + (inmed shl 2)
                    }
                    0x6//blez
                    -> if (getRegister(rs) <= 0) {
                        jump = regPC + (inmed shl 2)
                    }
                    0x7//bgtz
                    -> if (getRegister(rs) > 0) {
                        jump = regPC + (inmed shl 2)
                    }
                    0x8//addi
                    -> setRegister(rt, getRegister(rs) + inmed)
                    0x9//addiu
                    -> {
                        // TODO add trap on overflow
                        setRegister(rt, getRegister(rs) + inmed)
                    }
                    0xa//slti
                    -> setRegister(rt, if (getRegister(rs) < inmed) 1 else 0)
                    0xb//sltiu
                    -> {
                        m1 = getRegister(rs).toLong() and 0xFFFF_FFFF
                        m2 = inmedU.toLong() and 0xFFFF_FFFF
                        setRegister(rt, if (m1 < m2) 1 else 0)
                    }
                    0xc//andi
                    -> setRegister(rt, getRegister(rs) and inmedU)
                    0xd//ori
                    -> setRegister(rt, getRegister(rs) or inmedU)
                    0xe//xori
                    -> setRegister(rt, getRegister(rs) xor inmedU)
                    0xf//lui
                    -> setRegister(rt, inmedU shl 16)
                    0x18//llo
                    -> setRegister(rt, getRegister(rt) and 0xFFFF0000.toInt() or inmedU)
                    0x19//lhi
                    -> setRegister(rt, getRegister(rt) and 0x0000FFFF or (inmedU shl 16))
                    0x1a//trap
                    -> interrupt(Syscall())
                    0x20//lb
                    -> setRegister(rt, bus.readByte(getRegister(rs) + inmed).toInt())
                    0x21//lh
                    -> setRegister(rt, (bus.readWord(getRegister(rs) + inmed).toShort()).toInt())
                    0x22//lwl
                    -> {
                        val addr = getRegister(rs) + inmed
                        val word = bus.readWord(addr and 0xFFFF_FFFC.toInt())
                        when (addr and 0x3) {
                            0 -> setRegister(rt, ((word and 0x0000_00FF) shl 24) or (getRegister(rt) and 0x00FF_FFFF))
                            1 -> setRegister(rt, ((word and 0x0000_FFFF) shl 16) or (getRegister(rt) and 0x0000_FFFF))
                            2 -> setRegister(rt, ((word and 0x00FF_FFFF) shl 8) or (getRegister(rt) and 0x0000_00FF))
                            3 -> setRegister(rt, word)
                        }
                    }
                    0x23//lw
                    -> {
                        val addr = getRegister(rs) + inmed
                        if (addr and 0x3 != 0) {
                            interrupt(WordBoundaryException(addr))
                        } else {
                            setRegister(rt, bus.readWord(addr))
                        }
                    }
                    0x24//lbu
                    -> setRegister(rt, bus.readByte(getRegister(rs) + inmed).toInt() and 0xFF)
                    0x25//lhu
                    -> setRegister(rt, bus.readWord(getRegister(rs) + inmed) and 0xFFFF)
                    0x26//lwr
                    -> {
                        val addr = getRegister(rs) + inmed
                        val word = bus.readWord(addr and 0xFFFF_FFFC.toInt())
                        when (addr and 0x3) {
                            0 -> setRegister(rt, word)
                            1 -> setRegister(rt, (word ushr 8) or (getRegister(rt) and 0xFF000000.toInt()))
                            2 -> setRegister(rt, (word ushr 16) or (getRegister(rt) and 0xFFFF0000.toInt()))
                            3 -> setRegister(rt, (word ushr 24) or (getRegister(rt) and 0xFFFFFF00.toInt()))
                        }
                    }
                    0x28//sb
                    -> bus.writeByte(getRegister(rs) + inmed, (getRegister(rt) and 0xFF).toByte())
                    0x29//sh
                    -> {
                        val addr = getRegister(rs) + inmed
                        val value = getRegister(rt)
                        bus.writeByte(addr, (value and 0xFF).toByte())
                        bus.writeByte(addr + 1, (value and 0xFF00 ushr 8).toByte())
                    }
                    0x2a//swl
                    -> {
                        val addr = getRegister(rs) + inmed
                        val alignAddr = addr and 0xFFFF_FFFC.toInt()
                        val word = bus.readWord(alignAddr)
                        when (addr and 0x3) {
                            0 -> bus.writeWord(alignAddr, (getRegister(rt) ushr 24) or (word and 0xFFFF_FF00.toInt()))
                            1 -> bus.writeWord(alignAddr, (getRegister(rt) ushr 16) or (word and 0xFFFF_0000.toInt()))
                            2 -> bus.writeWord(alignAddr, (getRegister(rt) ushr 8) or (word and 0xFF00_0000.toInt()))
                            3 -> bus.writeWord(alignAddr, getRegister(rt))
                        }
                    }
                    0x2b//sw
                    -> {
                        if (getRegister(rs) + inmed and 0x3 != 0) {
                            interrupt(WordBoundaryException(getRegister(rs) + inmed))
                        } else {
                            bus.writeWord(getRegister(rs) + inmed, getRegister(rt))
                        }
                    }
                    0x2e//swr
                    -> {
                        // TODO test and maybe optimize
                        val addr = getRegister(rs) + inmed
                        val alignAddr = addr and 0xFFFF_FFFC.toInt()
                        val word = bus.readWord(alignAddr)
                        when (addr and 0x3) {
                            0 -> bus.writeWord(alignAddr, getRegister(rt))
                            1 -> bus.writeWord(alignAddr, (getRegister(rt) ushr 8) or (word and 0x0000_00FF))
                            2 -> bus.writeWord(alignAddr, (getRegister(rt) ushr 16) or (word and 0x0000_FFFF))
                            3 -> bus.writeWord(alignAddr, (getRegister(rt) ushr 24) or (word and 0x00FF_FFFF))
                        }
                    }
                    else -> interrupt(InvalidInstruction())
                }
            }
            InstructionType.EXCEPTION -> interrupt(Syscall())//syscall/trap
            InstructionType.COPROCESSOR -> {//coprocessor instructions

                val code = getBitsFromInt(instruct, 21, 25, false)
                val rt = getBitsFromInt(instruct, 16, 20, false)
                val rd = getBitsFromInt(instruct, 11, 15, false)

                when {
                    code == 0x0 -> {//mfc0 rd, rt | move from coprocessor 0
                        var value = 0
                        when (rt) {
                            12 -> value = regStatus
                            13 -> value = regCause
                            14 -> value = regEPC
                        }
                        setRegister(rd, value)
                    }
                    code == 0x4 -> {//mtc0 rd, rt | move to coprocessor 0
                        val value = getRegister(rt)
                        when (rt) {
                            12 -> regStatus = value
                            13 -> regCause = value
                            14 -> regEPC = value
                        }
                    }
                    code == 0x10 && instruct and 63 == 16 -> {//rfe | return from exception
                        regPC = regEPC
                        regStatus = regStatus shr 4
                    }
                    else -> interrupt(InvalidInstruction())
                }
            }
            CPU_MIPS.InstructionType.NOOP -> {
                // Nothing
            }
        }
    }

    companion object {

        val registerNames = arrayOf("z0", "at", "v0", "v1", "a0", "a1", "a2", "a3", "t0", "t1", "t2", "t3",
                "t4", "t5", "t6", "t7", "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "t8", "t9", "k0", "k1", "gp",
                "sp", "fp", "ra")
        val TYPE_R = arrayOf("SLL  ", "UNKNOW", "SRL  ", "SRA  ", "SLLV ", "UNKNOW", "SRLV ", "SRAV ", "JR   ", "JALR ",
                "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "MFHI ", "MTHI ", "MFLO ", "MTLO ",
                "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "MULT ", "MULTU", "DIV  ", "DIVU ", "UNKNOW", "UNKNOW",
                "UNKNOW", "UNKNOW", "ADD  ", "ADDU ", "SUB  ", "SUBU ", "AND  ", "OR   ", "XOR  ", "NOR  ", "UNKNOW",
                "UNKNOW", "SLT  ", "SLTU ", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW",
                "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW",
                "UNKNOW", "UNKNOW", "UNKNOW")
        val TYPE_I = arrayOf("UNKNOW", "BGEZ ", "UNKNOW", "UNKNOW", "BEQ  ", "BNE  ", "BLEZ ", "BGTZ ", "ADDI ",
                "ADDIU", "SLTI ", "SLTIU", "ANDI ", "ORI  ", "XORI ", "LUI  ", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW",
                "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "LLO  ", "LHI  ", "TRAP ", "UNKNOW", "UNKNOW", "UNKNOW",
                "UNKNOW", "UNKNOW", "LB   ", "LH   ", "LWL  ", "LW   ", "LBU  ", "LHU  ", "LWR   ", "UNKNOW", "SB   ",
                "SH   ", "SWL  ", "SW   ", "UNKNOW", "UNKNOW", "SWR  ", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW",
                "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW", "UNKNOW",
                "UNKNOW", "UNKNOW", "UNKNOW")

        private fun log(s: String, vararg objs: Any) {
            debug(String.format(s, *objs))
        }

        private fun debug(t: Any) {
            println(t.toString())
        }

        fun getBitsFromInt(inst_: Int, start_: Int, end_: Int, signed: Boolean): Int {
            var inst = inst_
            var start = start_
            var end = end_
            if (start > end) {
                val temp = end
                end = start
                start = temp
            }
            val max = 0xFFFFFFFF.toInt()
            val mask = max.ushr(31 - end) and (max shl start)
            inst = inst and mask
            if (signed) {
                inst = inst shl 31 - end
                return inst shr start + (31 - end)
            }
            return inst.ushr(start)
        }

        fun decompileInst(instruct: Int): String {
            if (instruct == 0) {
                return "NOP"
            }

            val opcode = getBitsFromInt(instruct, 26, 31, false)

            if (instruct == 0x0000000c || opcode == 0x10) {
                return "Exception inst: 0x%08x, inst: %d".format(instruct, instruct)
            } else if (opcode == 0) {//type R

                val rs: Int = getBitsFromInt(instruct, 21, 25, false)
                val rt: Int = getBitsFromInt(instruct, 16, 20, false)
                val rd: Int = getBitsFromInt(instruct, 11, 15, false)
                val shamt: Int = getBitsFromInt(instruct, 6, 10, false)
                val func: Int = getBitsFromInt(instruct, 0, 5, false)
                return "%s $%s, $%s, $%s (%05d) \t Type R: inst: 0x%08x"
                        .format(TYPE_R[func], registerNames[rd], registerNames[rs], registerNames[rt], shamt,
                                instruct)

            } else if (opcode == 0x2 || opcode == 0x3) {//type J

                val names = arrayOf("UNKNOW", "UNKNOW", "J    ", "JAL  ")
                val dir = getBitsFromInt(instruct, 0, 25, false)
                return "%s 0x%08x (0x%08x) \t Type J: inst: 0x%08x"
                        .format(names[opcode], dir, dir and 0xF0000000.toInt() or (dir shl 2), instruct)

            } else {//type I

                val rs: Int = getBitsFromInt(instruct, 21, 25, false)
                val rt: Int = getBitsFromInt(instruct, 16, 20, false)
                val inmed: Int = getBitsFromInt(instruct, 0, 15, true)
                val inmedU: Int = getBitsFromInt(instruct, 0, 15, false)
                return "%s $%s, $%s, %05d (%05d) \t Type I: inst: 0x%08x (opcode: 0x%x %d)"
                        .format(TYPE_I[opcode], registerNames[rs], registerNames[rt], inmed, inmedU, instruct, opcode,
                                opcode)
            }
        }
    }

    private fun debugInst(instruct: Int) {
        log("PC: 0x%08x \t ${decompileInst(instruct)}", pfPC)
    }

    override fun interrupt(exception: ICPU.IInterruption) {
        motherboard?.halt()
        if (debugLevel > 0) {
            val bus = motherboard?.bus!!
            log("Exception: %d (%s), regPC: 0x%08x, pfPC: 0x%08x, Description: %s", exception.code, exception.name,
                    regPC, pfPC, exception.description)
            val inst = bus.readWord(pfPC)
            debugInst(inst)

            debug("Registers: ")
            for (i in 0..31) {
                log("\t %d: \t %s : 0x%08x (%08d)", i, registerNames[i], getRegister(i), getRegister(i))
            }

            println("Code before error: ")
            for (j in 0..15) {
                val i = 15 - j
                val word = bus.readWord(pfPC - i * 4)
                println("(PC 0x%08x) 0x%08x            %s".format(pfPC - i * 4, word, decompileInst(word)))
            }
            println("Code after error: ")
            for (i in 0..15) {
                val word = bus.readWord(pfPC + i * 4)
                println("(PC 0x%08x) 0x%08x            %s".format(pfPC + i * 4, word, decompileInst(word)))
            }

            println("Stacktrace:")
            println("Stack Pointer: 0x%08x".format(getRegister(29)))
            for (i in 0..50) {
                println("(0x%08x) 0x%08x".format(getRegister(29) + i * 4,
                        bus.readWord(getRegister(29) + i * 4)))
            }
        } else {
//            int flag = exception.getCode();
//            if ((regStatus & (flag + 1)) == 0) {
//                return;
//            }
//            regCause = flag;
//            regEPC = regPC;
//            regStatus <<= 4;
//            regPC = 0x000;
        }
    }

    override fun setMotherboard(mb: IMotherboard) {
        motherboard = mb
    }

    override fun serialize(): Map<String, Any> {
        return mapOf(
                "Regs" to registers.copyOf(),
                "PC" to regPC,
                "regHI" to regHI,
                "regLO" to regLO,
                "Status" to regStatus,
                "Cause" to regCause,
                "EPC" to regEPC,
                "Jump" to jump
        )
    }

    override fun deserialize(map: Map<String, Any>) {
        registers = map["Regs"] as IntArray
        regPC = map["PC"] as Int
        regHI = map["regHI"] as Int
        regLO = map["regLO"] as Int
        regStatus = map["Status"] as Int
        regCause = map["Cause"] as Int
        regEPC = map["EPC"] as Int
        jump = map["Jump"] as Int
    }
}