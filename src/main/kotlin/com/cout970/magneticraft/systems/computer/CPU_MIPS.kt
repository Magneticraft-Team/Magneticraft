package com.cout970.magneticraft.systems.computer

import com.cout970.magneticraft.api.computer.ICPU
import com.cout970.magneticraft.api.computer.IMotherboard
import com.cout970.magneticraft.systems.computer.exception.*
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
    //exceptions registers http://people.cs.pitt.edu/~don/coe1502/current/Unit4a/Unit4a.html
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

                val func = instruct and 0b111111
                val shamt = instruct ushr 6 and 0b11111
                val rd = instruct ushr 11 and 0b11111
                val rt = instruct ushr 16 and 0b11111
                val rs = instruct ushr 21 and 0b11111

                when (func) {
                    // SLL rd, rt, shamt (Shift Left Logical)
                    0x00 -> setRegister(rd, getRegister(rt) shl shamt)
                    0x02 -> {
                        if (rs and 1 == 0) {
                            // SRL rd, rt, shamt (Shift Right Unsigned)
                            setRegister(rd, getRegister(rt).ushr(shamt))
                        } else {
                            // ROTR rd, rt, shamt (Rotate Word Right)
                            val left = getRegister(rt) and ((1 shl (shamt + 1)) - 1)
                            setRegister(rd, (left shl (32 - shamt)) or getRegister(rt).ushr(shamt))
                        }
                    }
                    // SRA rd, rt, shamt (Shift Word Right Arithmetic)
                    0x03 -> setRegister(rd, getRegister(rt) shr shamt)
                    // SLLV rd, rt, rs (Shift Left Logical Variable)
                    0x04 -> setRegister(rd, getRegister(rt) shl getRegister(rs))
                    0x06 -> {
                        if (rs and 1 == 0) {
                            // SRLV rd, rt, rs (Shift Right Unsigned)
                            setRegister(rd, getRegister(rt) ushr getRegister(rs))
                        } else {
                            // ROTRV rd, rt, rs (Rotate Word Right)
                            val left = getRegister(rt) and ((1 shl (getRegister(rs) + 1)) - 1)
                            setRegister(rd, (left shl (32 - getRegister(rs))) or getRegister(rt).ushr(getRegister(rs)))
                        }
                    }
                    // SRAV rd, rt, rs (Shift Word Right Arithmetic Variable)
                    0x07 -> setRegister(rd, getRegister(rt) shr getRegister(rs))
                    // JR rs (Jump Register)
                    0x08 -> {
                        if (getRegister(rs) == 0 || getRegister(rs) == -1) {
                            interrupt(NullPointerException())
                            return
                        }
                        jump = getRegister(rs)
                    }
                    // JALR target (Jump and Link Register)
                    0x9 -> {
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
                    // MOVZ rd, rs, rt (Move Conditional on Zero)
                    0x0a -> if (getRegister(rt) == 0) {
                        setRegister(rd, getRegister(rs))
                    }
                    // MOVN rd, rs, rt (Move Conditional on Not Zero)
                    0x0b -> if (getRegister(rt) != 0) {
                        setRegister(rd, getRegister(rs))
                    }
                    // SYSCALL
                    0x0c -> interrupt(Syscall())
                    // BREAK
                    0x0d -> interrupt(BreakpointException())
                    // SYNC
                    0x0f -> {
                        // Not supported
                    }
                    // MFHI rd (Move From HI Register)
                    0x10 -> setRegister(rd, regHI)
                    // MTHI rd (Move To HI Register )
                    0x11 -> regHI = getRegister(rd)
                    // MFLO rd (Move From LO Register)
                    0x12 -> setRegister(rd, regLO)
                    // MTLO rd (Move To LO Register)
                    0x13 -> regLO = getRegister(rd)
                    // MULT rs, rt (Multiply Word)
                    0x18 -> {
                        m1 = getRegister(rs).toLong()
                        m2 = getRegister(rt).toLong()
                        mt = m1 * m2
                        regLO = mt.toInt()
                        regHI = (mt shr 32).toInt()
                    }
                    // MULTU rs, rt (Multiply Unsigned Word)
                    0x19 -> {
                        m1 = getRegister(rs).toLong() and 0xFFFF_FFFF
                        m2 = getRegister(rt).toLong() and 0xFFFF_FFFF
                        mt = m1 * m2
                        regLO = mt.toInt()
                        regHI = (mt shr 32).toInt()
                    }
                    // DIV rs, rt
                    0x1a -> if (getRegister(rt) != 0) {
                        regLO = getRegister(rs) / getRegister(rt)
                        regHI = getRegister(rs) % getRegister(rt)
                    } else {
                        interrupt(ArithmeticException())
                    }
                    // DIVU rs, rt
                    0x1b -> {
                        m1 = getRegister(rs).toLong() and 0xFFFF_FFFF
                        m2 = getRegister(rt).toLong() and 0xFFFF_FFFF
                        if (m2 == 0L) {
                            interrupt(ArithmeticException())
                        } else {
                            regLO = (m1 / m2).toInt()
                            regHI = (m1 % m2).toInt()
                        }
                    }
                    // ADD rd, rs, rt (Add)
                    // TODO trap on overflow
                    0x20 -> setRegister(rd, getRegister(rt) + getRegister(rs))
                    // ADDU rd, rs, rt (Add Unsigned)
                    0x21 -> setRegister(rd, getRegister(rt) + getRegister(rs))
                    // SUB rd, rs, rt (Subtract)
                    0x22 -> setRegister(rd, getRegister(rs) - getRegister(rt))
                    // SUBU rd, rs, rt (Subtract Unsigned)
                    0x23 -> setRegister(rd, getRegister(rs) - getRegister(rt))
                    // AND rd, rs, rt
                    0x24 -> setRegister(rd, getRegister(rt) and getRegister(rs))
                    // OR rd, rs, rt (Or)
                    0x25 -> setRegister(rd, getRegister(rt) or getRegister(rs))
                    // XOR rd, rs, rt (Exclusive Or)
                    0x26 -> setRegister(rd, getRegister(rt) xor getRegister(rs))
                    // NOR rd, rs, rt (Not Or)
                    0x27 -> setRegister(rd, (getRegister(rt) or getRegister(rs)).inv())
                    // SLT rd, rs, rt (Set on Less Than)
                    0x2a -> setRegister(rd, if (getRegister(rs) < getRegister(rt)) 1 else 0)
                    // SLTU rd, rs, rt (Set on Less Than Unsigned)
                    0x2b -> {
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
                    // J target (Jump)
                    0x2 -> {
                        jump = regPC
                        jump = jump and 0xF0000000.toInt()
                        jump = jump or (dir shl 2)
                    }
                    // JAL target (Jump and Link)
                    0x3 -> {
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
                val rd = instruct shr 11 and 31

                val immed = instruct shl 16 shr 16
                val immedU = instruct shl 16 ushr 16

                when (opcode) {
                    0x01 -> when (rt) {
                        // BLTZ rs, offset
                        0b00000 -> if (getRegister(rs) < 0) {
                            jump = regPC + (immed shl 2)
                        }
                        // BGEZ rs, offset
                        0b00001 -> if (getRegister(rs) >= 0) {
                            jump = regPC + (immed shl 2)
                        }
                        // BLTZAL rs, offset
                        0b10000 -> if (getRegister(rs) < 0) {
                            setRegister(31, regPC + 4)
                            jump = regPC + (immed shl 2)
                        }
                        // BGEZAL rs, offset
                        0b10001 -> if (getRegister(rs) >= 0) {
                            setRegister(31, regPC + 4)
                            jump = regPC + (immed shl 2)
                        }
                        // SYNCI offset(base)
                        0b11111 -> {
                            // Not supported
                        }
                        // Conditional traps are not supported
                        // TLB instructions are not supported
                        else -> interrupt(InvalidInstruction())
                    }
                    // BEQ rt, rs, offset
                    0x04 -> if (getRegister(rt) == getRegister(rs)) {
                        jump = regPC + (immed shl 2)
                    }
                    // BNE rs, rt, offset
                    0x05 -> if (getRegister(rt) != getRegister(rs)) {
                        jump = regPC + (immed shl 2)
                    }
                    // BLEZ rs, offset
                    0x06 -> if (getRegister(rs) <= 0) {
                        jump = regPC + (immed shl 2)
                    }
                    0x07//bgtz
                    -> if (getRegister(rs) > 0) {
                        jump = regPC + (immed shl 2)
                    }
                    // ADDI rt, rs, immediate (Add Immediate)
                    // TODO add trap on overflow
                    0x08 -> setRegister(rt, getRegister(rs) + immed)
                    // ADDIU rt, rs, immediate (Add Immediate unsigned)
                    0x09 -> setRegister(rt, getRegister(rs) + immed)
                    // SLTI rt, rs, immediate (Set on Less Than Immediate)
                    0x0a -> setRegister(rt, if (getRegister(rs) < immed) 1 else 0)
                    // SLTIU rt, rs, immediate (Set on Less Than Immediate Unsigned)
                    0x0b -> {
                        m1 = getRegister(rs).toLong() and 0xFFFF_FFFF
                        m2 = immedU.toLong() and 0xFFFF_FFFF
                        setRegister(rt, if (m1 < m2) 1 else 0)
                    }
                    // ANDI rt, rs, immediate
                    0x0c -> setRegister(rt, getRegister(rs) and immedU)
                    // ORI rt, rs, immediate (Or Immediate)
                    0x0d -> setRegister(rt, getRegister(rs) or immedU)
                    // XORI rt, rs, immediate (Exclusive Or immediate)
                    0x0e -> setRegister(rt, getRegister(rs) xor immedU)
                    // LUI rt, immediate (Load Upped Immediate)
                    0x0f -> setRegister(rt, immedU shl 16)
                    // LLO rt, immediate (Load Lower Bits)
                    0x18 -> setRegister(rt, getRegister(rt) and 0xFFFF0000.toInt() or immedU)
                    // LHI rt, immediate (Load Higher Bits)
                    0x19 -> setRegister(rt, getRegister(rt) and 0x0000FFFF or (immedU shl 16))
                    // TRAP (Syscall)
                    0x1a -> interrupt(Syscall())
                    // Special 2
                    0x1c -> {
                        val special = instruct and 31
                        when (special) {
                            // MADD rs, rt (Multiply and Add Word)
                            0x00 -> {
                                val hilo = (regHI.toLong() shl 32) or regLO.toLong()
                                val base = getRegister(rs).toLong() * getRegister(rt).toLong()
                                val result = hilo + base

                                regHI = (result ushr 32).toInt()
                                regHI = result.toInt()
                            }
                            // MADDU rs, rt (Multiply and Add Unsigned Word)
                            0x01 -> {
                                val hilo = (regHI.toLong() shl 32) or regLO.toLong()
                                val base = getRegister(rs).toLong() * getRegister(rt).toLong()
                                val result = hilo + base

                                regHI = (result ushr 32).toInt()
                                regHI = result.toInt()
                            }
                            // MUL rd, rs, rt (Multiply Word)
                            0x02 -> setRegister(rd, getRegister(rs) * getRegister(rt))
                            // MSUB rs, rt (Multiply and Subtract Word)
                            0x04 -> {
                                val hilo = (regHI.toLong() shl 32) or regLO.toLong()
                                val base = getRegister(rs).toLong() * getRegister(rt).toLong()
                                val result = hilo - base

                                regHI = (result ushr 32).toInt()
                                regHI = result.toInt()
                            }
                            // MSUBU rs, rt (Multiply and Subtract Unsigned Word)
                            0x05 -> {
                                val hilo = (regHI.toLong() shl 32) or regLO.toLong()
                                val base = getRegister(rs).toLong() * getRegister(rt).toLong()
                                val result = hilo - base

                                regHI = (result ushr 32).toInt()
                                regHI = result.toInt()
                            }
                            // CLO rt, rs (rt and rd must be the same)
                            // Count Leading Ones in Word
                            0x21 -> setRegister(rt, java.lang.Integer.numberOfLeadingZeros(getRegister(rs).inv()))
                            // CLZ rt, rs (rt and rd must be the same)
                            // Count Leading Zeros in Word
                            0x20 -> setRegister(rt, java.lang.Integer.numberOfLeadingZeros(getRegister(rs)))
                            // SDBBP code (Software Debug Breakpoint)
                            0x3f -> interrupt(BreakpointException())

                            else -> interrupt(InvalidInstruction())
                        }
                    }
                    // Special 3
                    0x1f -> {
                        val code = instruct and 63

                        when (code) {
                            // EXT rt, rs, pos, size (Extract Bit Field)
                            0x00 -> {
                                val size = instruct shr 11 and 31 // (size - 1)
                                val pos = instruct shr 6 and 31

                                setRegister(rt, getBitsFromInt(getRegister(rs), pos, pos + size, false))
                            }
                            // INS (Insert Bit Field)
                            0x04 -> {
                                val mbs = instruct shr 11 and 31 // (pos + size -1)
                                val lbs = instruct shr 6 and 31 // pos

                                val a = getRegister(rs) and (0xFFFF_FFFF.toInt() shl (mbs + 1)) // rt[31..(msb+1)]
                                val b = getRegister(rs) and (0xFFFF_FFFF.toInt() ushr (32 - (mbs - lbs))) // rs[(msb-lbs)..0]
                                val c = getRegister(rs) and (0xFFFF_FFFF.toInt() shl (32 - (lbs - 1))) //

                                setRegister(rt, a or b or c)
                            }
                            // BSHFL
                            0x20 -> {
                                val op = (instruct ushr 6) and 31

                                when (op) {
                                    // WSBH rd, rt (Word Swap Bytes Within Halfwords)
                                    0x02 -> {
                                        val a = (getRegister(rt) and 0xFF) shl 8
                                        val b = ((getRegister(rt) ushr 8) and 0xFF)
                                        val c = ((getRegister(rt) ushr 16) and 0xFF) shl 24
                                        val d = ((getRegister(rt) ushr 24) and 0xFF) shl 16
                                        setRegister(rd, a or b or c or d)
                                    }
                                    // SEB rd, rt (Sign-Extend Byte)
                                    0x10 -> setRegister(rd, getRegister(rt).toByte().toInt())
                                    // SEH rd, rt (Sign-Extend Halfword)
                                    0x18 -> setRegister(rd, getRegister(rt).toShort().toInt())
                                    else -> interrupt(InvalidInstruction())
                                }
                            }
                            // RDHWR rt, rd (Read Hardware Register)
                            0x3b -> {
                                // Not supported
                            }

                            else -> interrupt(InvalidInstruction())
                        }
                    }
                    // LB rt, offset(base) (Load Byte)
                    0x20 -> setRegister(rt, bus.readByte(getRegister(rs) + immed).toInt())
                    // LH rt, offset(base) (Load Halfword)
                    0x21 -> setRegister(rt, (bus.readWord(getRegister(rs) + immed).toShort()).toInt())
                    // LWL rt, offset(base) (Load World Left)
                    0x22 -> {
                        val addr = getRegister(rs) + immed
                        val word = bus.readWord(addr and 0xFFFF_FFFC.toInt())
                        when (addr and 0x3) {
                            0 -> setRegister(rt, ((word and 0x0000_00FF) shl 24) or (getRegister(rt) and 0x00FF_FFFF))
                            1 -> setRegister(rt, ((word and 0x0000_FFFF) shl 16) or (getRegister(rt) and 0x0000_FFFF))
                            2 -> setRegister(rt, ((word and 0x00FF_FFFF) shl 8) or (getRegister(rt) and 0x0000_00FF))
                            3 -> setRegister(rt, word)
                        }
                    }
                    // LW rt, offset(base) (Load Word)
                    0x23 -> {
                        val addr = getRegister(rs) + immed
                        if (addr and 0x3 != 0) {
                            interrupt(WordBoundaryException(addr))
                        } else {
                            setRegister(rt, bus.readWord(addr))
                        }
                    }
                    // LBU rt, offset(base) (Load Byte Unsigned)
                    0x24 -> setRegister(rt, bus.readByte(getRegister(rs) + immed).toInt() and 0xFF)
                    // LHU rt, offset(base) (Load Halfword Unsigned)
                    0x25 -> setRegister(rt, bus.readWord(getRegister(rs) + immed) and 0xFFFF)
                    // LWR rt, offset(base) (Load Word Right)
                    0x26 -> {
                        val addr = getRegister(rs) + immed
                        val word = bus.readWord(addr and 0xFFFF_FFFC.toInt())
                        when (addr and 0x3) {
                            0 -> setRegister(rt, word)
                            1 -> setRegister(rt, (word ushr 8) or (getRegister(rt) and 0xFF000000.toInt()))
                            2 -> setRegister(rt, (word ushr 16) or (getRegister(rt) and 0xFFFF0000.toInt()))
                            3 -> setRegister(rt, (word ushr 24) or (getRegister(rt) and 0xFFFFFF00.toInt()))
                        }
                    }
                    // SB rt, offset(base) (Store Byte)
                    0x28 -> bus.writeByte(getRegister(rs) + immed, (getRegister(rt) and 0xFF).toByte())
                    // SH rt, offset(base) (Store Halfword)
                    0x29 -> {
                        val addr = getRegister(rs) + immed
                        val value = getRegister(rt)
                        bus.writeByte(addr, (value and 0xFF).toByte())
                        bus.writeByte(addr + 1, (value and 0xFF00 ushr 8).toByte())
                    }
                    // SWL rt, offset(base) (Store Word Left)
                    0x2a -> {
                        val addr = getRegister(rs) + immed
                        val alignAddr = addr and 0xFFFF_FFFC.toInt()
                        val word = bus.readWord(alignAddr)
                        when (addr and 0x3) {
                            0 -> bus.writeWord(alignAddr, (getRegister(rt) ushr 24) or (word and 0xFFFF_FF00.toInt()))
                            1 -> bus.writeWord(alignAddr, (getRegister(rt) ushr 16) or (word and 0xFFFF_0000.toInt()))
                            2 -> bus.writeWord(alignAddr, (getRegister(rt) ushr 8) or (word and 0xFF00_0000.toInt()))
                            3 -> bus.writeWord(alignAddr, getRegister(rt))
                        }
                    }
                    // SW rt, offset(base) (Store Word)
                    0x2b -> if (getRegister(rs) + immed and 0x3 != 0) {
                        interrupt(WordBoundaryException(getRegister(rs) + immed))
                    } else {
                        bus.writeWord(getRegister(rs) + immed, getRegister(rt))
                    }
                    // SWR  rt, offset(base) (Store Word Right)
                    0x2e -> {
                        val addr = getRegister(rs) + immed
                        val alignAddr = addr and 0xFFFF_FFFC.toInt()
                        val word = bus.readWord(alignAddr)
                        when (addr and 0x3) {
                            0 -> bus.writeWord(alignAddr, getRegister(rt))
                            1 -> bus.writeWord(alignAddr, (getRegister(rt) ushr 8) or (word and 0x0000_00FF))
                            2 -> bus.writeWord(alignAddr, (getRegister(rt) ushr 16) or (word and 0x0000_FFFF))
                            3 -> bus.writeWord(alignAddr, (getRegister(rt) ushr 24) or (word and 0x00FF_FFFF))
                        }
                    }
                    // CACHE
                    0x2f -> {
                        // Ignored, there is no cache
                    }
                    // LL rt, offset(base) (Load Linked Word)
                    0x30 -> {
                        val addr = getRegister(rs) + immed
                        if (addr and 0x3 != 0) {
                            interrupt(WordBoundaryException(addr))
                        } else {
                            setRegister(rt, bus.readWord(addr))
                        }
                    }
                    // PREFETCH
                    0x33 -> {
                        // Ignore
                    }
                    // SC rt, offset(base) (Store Conditional World)
                    0x38 -> if (getRegister(rs) + immed and 0x3 != 0) {
                        interrupt(WordBoundaryException(getRegister(rs) + immed))
                    } else {
                        bus.writeWord(getRegister(rs) + immed, getRegister(rt))
                        setRegister(rt, 1)
                    }
                    else -> interrupt(InvalidInstruction())
                }
            }
            // SYSCALL/TRAP
            InstructionType.EXCEPTION -> interrupt(Syscall())
            // COPROCESSOR INSTRUCTIONS
            InstructionType.COPROCESSOR -> {

                val code = getBitsFromInt(instruct, 21, 25, false)
                val rt = getBitsFromInt(instruct, 16, 20, false)
                val rd = getBitsFromInt(instruct, 11, 15, false)
                val co = instruct ushr 25 and 1
                val special = instruct and 63

                when {
                    // MFC0 rt, rd (Move From Coprocessor 0)
                    code == 0x00 -> {
                        var value = 0
                        when (rd) {
                            12 -> value = regStatus
                            13 -> value = regCause
                            14 -> value = regEPC
                        }
                        setRegister(rt, value)
                    }
                    // CFC2 rt, immed (Move Control Word From Coprocessor 2)
                    // 0x2 -> {}
                    // MTC0 rt, rd (Move To Coprocessor 0)
                    code == 0x04 -> {
                        val value = getRegister(rt)
                        when (rd) {
                            12 -> regStatus = value
                            13 -> regCause = value
                            14 -> regEPC = value
                        }
                    }
                    // RFE (Return From Exception)
                    code == 0x10 && special == 0x10 -> {
                        regPC = regEPC
                        regStatus = regStatus shr 4
                    }
                    // ERET (Exception return)
                    code == 0x10 && special == 0x18 -> {
                        regPC = regEPC
                        regStatus = regStatus shr 4
                    }
                    // DI rt (Disable Interrupts)
                    code == 0x0b && rd == 0x0c && special == 0x00 -> {
                        setRegister(rt, regStatus)
                        // 4 last bits are masks for the 4 types of interruptions
                        regStatus = regStatus and 0xFFFF_FFF0.toInt()
                    }
                    // EI rt (Enable Interrupts)
                    code == 0x0b && rd == 0x0c && special == 0x20 -> {
                        setRegister(rt, regStatus)
                        // 4 last bits are masks for the 4 types of interruptions
                        regStatus = regStatus or 0x0f
                    }
                    // DERET (Debug Exception Return)
                    co == 1 && special == 0x1f -> {
                        regPC = regEPC
                        regStatus = regStatus shr 4
                    }
                    // Wait (Enter Standby Mode)
                    co == 1 && special == 0x20 -> {
                        interrupt(BreakpointException())
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

        val TYPE_R = arrayOf(
                "SLL   ", "UNKN01", "SRL   ", "SRA   ", "SLLV  ", "UNKN05", "SRLV  ", "SRAV  ", "JR    ", "JALR  ",
                "UNKN10", "UNKN11", "UNKN12", "UNKN13", "UNKN14", "UNKN15", "MFHI  ", "MTHI  ", "MFLO  ", "MTLO  ",
                "UNKN20", "UNKN21", "UNKN22", "UNKN23", "MULT  ", "MULTU ", "DIV   ", "DIVU  ", "UNKN28", "UNKN29",
                "UNKN30", "UNKN31", "ADD   ", "ADDU  ", "SUB   ", "SUBU  ", "AND   ", "OR    ", "XOR   ", "NOR   ",
                "UNKN40", "UNKN41", "SLT   ", "SLTU  ", "UNKN44", "UNKN45", "UNKN46", "UNKN47", "UNKN48", "UNKN49",
                "UNKN50", "UNKN51", "UNKN52", "UNKN53", "UNKN54", "UNKN55", "UNKN56", "UNKN57", "UNKN58", "UNKN59",
                "UNKN60", "UNKN61", "UNKN62", "UNKN63"
        )

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

            debug("Code before error: ")
            for (j in 0..15) {
                val i = 15 - j
                val word = bus.readWord(pfPC - i * 4)
                debug("(PC 0x%08x) 0x%08x            %s".format(pfPC - i * 4, word, decompileInst(word)))
            }
            debug("Code after error: ")
            for (i in 0..15) {
                val word = bus.readWord(pfPC + i * 4)
                debug("(PC 0x%08x) 0x%08x            %s".format(pfPC + i * 4, word, decompileInst(word)))
            }

            debug("Stacktrace:")
            debug("Stack Pointer: 0x%08x".format(getRegister(29)))
            for (i in 0..50) {
                debug("(0x%08x) 0x%08x".format(getRegister(29) + i * 4,
                        bus.readWord(getRegister(29) + i * 4)))
            }
        } else {
//            val flag = exception.code
//            if ((regStatus and (flag + 1)) == 0) {
//                 // ignored, already handling other interruption
//                return
//            }
//            regCause = flag
//            regEPC = regPC
//            regStatus = regStatus shl 4
//            regPC = 0x0000
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