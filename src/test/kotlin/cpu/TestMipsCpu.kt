package cpu

import com.cout970.magneticraft.api.computer.IROM
import com.cout970.magneticraft.systems.computer.Bus
import com.cout970.magneticraft.systems.computer.CPU_MIPS
import com.cout970.magneticraft.systems.computer.Motherboard
import com.cout970.magneticraft.systems.computer.RAM
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Number of bugs found when adding tests: 4
 * wait, does 'code not even run once' count?, yes, then: 8
 */
class TestMipsCpu {

    lateinit var cpu: CPU_MIPS
    lateinit var memory: RAM
    lateinit var bus: Bus
    lateinit var motherboard: Motherboard

    @Before
    fun setUp() {
        cpu = CPU_MIPS()
        memory = RAM(0x20000, true)
        val rom = IROM { ByteInputStream() }
        motherboard = Motherboard(cpu, memory, rom)
        motherboard.reset()
        motherboard.start()
    }

    fun registerOperation(input: Int, instr: Int): Int {
        cpu.setRegister(8, input)
        memory.writeWord(Motherboard.CPU_START_POINT, instr) // instruction to test
        cpu.iterate()
        return cpu.getRegister(9)
    }

    fun register2Operation(input: Int, extra: Int, instr: Int): Int {
        cpu.setRegister(8, input)
        cpu.setRegister(10, extra)
        memory.writeWord(Motherboard.CPU_START_POINT, instr) // instruction to test
        cpu.iterate()
        return cpu.getRegister(9)
    }

    private fun assertEqualsHex(msg: String, expected: Int, value: Int) {
        assertEquals(msg, "0x%8x".format(expected), "0x%8x".format(value))
    }

    private fun assertEqualsHex(msg: String, expected: Long, value: Int) {
        assertEquals(msg, "0x%8x".format(expected), "0x%8x".format(value))
    }

    @Test
    fun testSLL() {
        val result = registerOperation(0x12345678, 0x000848c0)
        assertEqualsHex("SLL failed", 0x91a2b3c0, result)
    }

    @Test
    fun testSRL() {
        val result = registerOperation(0x12345678, 0x000848c2)
        assertEqualsHex("SRL failed", 0x02468acf, result)
    }

    @Test
    fun testSRA() {
        val result = registerOperation(0xF2345678.toInt(), 0x000848c3)
        assertEqualsHex("SRA failed", 0xfe468acf, result)
    }

    @Test
    fun testSLLV() {
        val result = register2Operation(0xF2345678.toInt(), 3, 0x01484804)
        assertEqualsHex("SLLV failed", 0x91a2b3c0, result)
    }

    @Test
    fun testSRLV() {
        val result = register2Operation(0xF2345678.toInt(), 3, 0x01484806)
        assertEqualsHex("SRLV failed", 0x1e468acf, result)
    }

    @Test
    fun testSRAV() {
        val result = register2Operation(0xF2345678.toInt(), 3, 0x01484807)
        assertEqualsHex("SRAV failed", 0xfe468acf, result)
    }

    @Test
    fun testMULT() {
        cpu.setRegister(8, 2)
        cpu.setRegister(10, 0x8000_0000.toInt())
        memory.writeWord(Motherboard.CPU_START_POINT, 0x010a0018)
        cpu.iterate()
        // 0x8000_0000.toInt() * 2 == 0xFFFF_FFFF_0000_0000
        assertEqualsHex("MULT failed", 0x0000_0000, cpu.regLO)
        assertEqualsHex("MULT failed", 0xFFFF_FFFF, cpu.regHI) // overflow
    }

    @Test
    fun testMULTU() {
        cpu.setRegister(8, 2)
        cpu.setRegister(10, 0x8000_0000.toInt())
        memory.writeWord(Motherboard.CPU_START_POINT, 0x010a0019)
        cpu.iterate()
        // 0x8000_0000.toInt() * 2 == 0x0000_0001_0000_0000
        assertEqualsHex("MULTU failed", 0x0000_0000, cpu.regLO)
        assertEqualsHex("MULTU failed", 0x0000_0001, cpu.regHI)
    }

    @Test
    fun testDIV() {
        cpu.setRegister(8, 13)
        cpu.setRegister(10, 5)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x010a001a)
        cpu.iterate()
        // 13 / 5 == 2
        // 13 % 5 == 3
        assertEqualsHex("DIV failed", 2, cpu.regLO)
        assertEqualsHex("DIV failed", 3, cpu.regHI)
    }

    @Test
    fun testDIV2() {
        cpu.setRegister(8, -13)
        cpu.setRegister(10, 5)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x010a001a)
        cpu.iterate()
        // -13 / 5 == -2
        // -13 % 5 == -3
        assertEqualsHex("DIV failed", -2, cpu.regLO)
        assertEqualsHex("DIV failed", -3, cpu.regHI)
    }

    @Test
    fun testDIVU() {
        cpu.setRegister(8, 13)
        cpu.setRegister(10, 5)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x010a001b)
        cpu.iterate()
        // 13 / 5 == 2
        // 13 % 5 == 3
        assertEqualsHex("DIVU failed", 2, cpu.regLO)
        assertEqualsHex("DIVU failed", 3, cpu.regHI)
    }

    @Test
    fun testDIVU2() {
        cpu.setRegister(8, -13) // 0xfffffff3
        cpu.setRegister(10, 5)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x010a001b)
        cpu.iterate()
        // -13 / 5 == 858993456 (0x33333330)
        // -13 % 5 == 3
        assertEqualsHex("DIVU failed", 0x33333330, cpu.regLO)
        assertEqualsHex("DIVU failed", 3, cpu.regHI)
    }

    @Test
    fun testADD() {
        val result = register2Operation(10, 5, 0x010a4820)
        assertEqualsHex("ADD failed", 15, result)
    }

    @Test
    fun testADDU() {
        val result = register2Operation(10, -5, 0x010a4821)
        assertEqualsHex("ADDU failed", 5, result)
    }

    @Test
    fun testSUB() {
        val result = register2Operation(10, 5, 0x010a4822)
        assertEqualsHex("SUB failed", 5, result)
    }

    @Test
    fun testSUBU() {
        val result = register2Operation(10, -5, 0x010a4823)
        assertEqualsHex("SUBU failed", 15, result)
    }

    @Test
    fun testAND() {
        val result = register2Operation(0xFF00, 0xFFFF, 0x010a4824)
        assertEqualsHex("AND failed", 0xFF00, result)
    }

    @Test
    fun testOR() {
        val result = register2Operation(0xFF00, 0xFFFF, 0x010a4825)
        assertEqualsHex("OR failed", 0xFFFF, result)
    }

    @Test
    fun testXOR() {
        val result = register2Operation(0xFF00, 0xFFFF, 0x010a4826)
        assertEqualsHex("XOR failed", 0x00FF, result)
    }

    @Test
    fun testNOR() {
        val result = register2Operation(0xFF00, 0xFFFF, 0x010a4827)
        assertEqualsHex("NOR failed", 0xffff0000, result)
    }

    @Test
    fun testSLT() {
        val result = register2Operation(2, 3, 0x010a482a)
        assertEqualsHex("SLT failed", 1, result)
    }

    @Test
    fun testSLT2() {
        val result = register2Operation(3, 2, 0x010a482a)
        assertEqualsHex("SLT failed", 0, result)
    }

    @Test
    fun testBLTZ() {
        registerOperation(1, 0x0500fffe) // jump back
        cpu.iterate()
        assertEqualsHex("BLTZ failed", Motherboard.CPU_START_POINT + 8, cpu.regPC)
    }

    @Test
    fun testBLTZ2() {
        registerOperation(-1, 0x0500fffe) // jump back
        cpu.iterate()
        assertEqualsHex("BLTZ failed", Motherboard.CPU_START_POINT - 4, cpu.regPC)
    }

    @Test
    fun testBGEZ() {
        registerOperation(1, 0x1d00fffe) // jump back
        cpu.iterate()
        assertEqualsHex("BGEZ failed", Motherboard.CPU_START_POINT - 4, cpu.regPC)
    }

    @Test
    fun testBGEZ2() {
        registerOperation(-1, 0x1d00fffe) // jump back
        cpu.iterate()
        assertEqualsHex("BGEZ failed", Motherboard.CPU_START_POINT + 8, cpu.regPC)
    }

    @Test
    fun testBLTZAL() {
        registerOperation(1, 0x0510fffe) // jump back
        cpu.iterate()
        assertEqualsHex("BLTZAL failed", Motherboard.CPU_START_POINT + 8, cpu.regPC)
        assertEqualsHex("BLTZAL failed", 0, cpu.getRegister(31))
    }

    @Test
    fun testBLTZAL2() {
        registerOperation(-1, 0x0510fffe) // jump back
        cpu.iterate()
        assertEqualsHex("BLTZAL failed", Motherboard.CPU_START_POINT - 4, cpu.regPC)
        assertEqualsHex("BLTZAL failed", Motherboard.CPU_START_POINT + 8, cpu.getRegister(31))
    }

    @Test
    fun testBGEZAL() {
        registerOperation(1, 0x0511fffe) // jump back
        cpu.iterate()
        assertEqualsHex("BGEZAL failed", Motherboard.CPU_START_POINT - 4, cpu.regPC)
        assertEqualsHex("BGEZAL failed", Motherboard.CPU_START_POINT + 8, cpu.getRegister(31))
    }

    @Test
    fun testBGEZAL2() {
        registerOperation(-1, 0x0511fffe) // jump back
        cpu.iterate()
        assertEqualsHex("BGEZAL failed", Motherboard.CPU_START_POINT + 8, cpu.regPC)
        assertEqualsHex("BGEZAL failed", 0, cpu.getRegister(31))
    }

    @Test
    fun testBEQ() {
        register2Operation(1, 1, 0x110afffd) // jump back
        cpu.iterate()
        assertEqualsHex("BEQ failed", Motherboard.CPU_START_POINT - 8, cpu.regPC)
    }

    @Test
    fun testBEQ2() {
        register2Operation(1, 2, 0x110afffd) // jump back
        cpu.iterate()
        assertEqualsHex("BEQ failed", Motherboard.CPU_START_POINT + 8, cpu.regPC)
    }

    @Test
    fun testBNE() {
        register2Operation(1, 2, 0x150afffd) // jump back
        cpu.iterate()
        assertEqualsHex("BNE failed", Motherboard.CPU_START_POINT - 8, cpu.regPC)
    }

    @Test
    fun testBNE2() {
        register2Operation(1, 1, 0x150afffd) // jump back
        cpu.iterate()
        assertEqualsHex("BNE failed", Motherboard.CPU_START_POINT + 8, cpu.regPC)
    }

    @Test
    fun testBLEZ() {
        registerOperation(1, 0x1900fffe) // jump back
        cpu.iterate()
        assertEqualsHex("BLEZ failed", Motherboard.CPU_START_POINT + 8, cpu.regPC)
    }

    @Test
    fun testBLEZ2() {
        registerOperation(-1, 0x1900fffe) // jump back
        cpu.iterate()
        assertEqualsHex("BLEZ failed", Motherboard.CPU_START_POINT - 4, cpu.regPC)
    }

    @Test
    fun testBLEZ3() {
        registerOperation(0, 0x1900fffe) // jump back
        cpu.iterate()
        assertEqualsHex("BLEZ failed", Motherboard.CPU_START_POINT - 4, cpu.regPC)
    }

    @Test
    fun testBGTZ() {
        registerOperation(1, 0x1d00fffe) // jump back
        cpu.iterate()
        assertEqualsHex("BGTZ failed", Motherboard.CPU_START_POINT - 4, cpu.regPC)
    }

    @Test
    fun testBGTZ2() {
        registerOperation(-1, 0x1d00fffe) // jump back
        cpu.iterate()
        assertEqualsHex("BGTZ failed", Motherboard.CPU_START_POINT + 8, cpu.regPC)
    }

    @Test
    fun testBGTZ3() {
        registerOperation(0, 0x1d00fffe) // jump back
        cpu.iterate()
        assertEqualsHex("BGTZ failed", Motherboard.CPU_START_POINT + 8, cpu.regPC)
    }

    @Test
    fun testADDI() {
        val result = registerOperation(10, 0x21090005)
        // 10 + 5 = 15
        assertEqualsHex("ADDI failed", 15, result)
    }

    @Test
    fun testADDI2() {
        val result = registerOperation(-1, 0x21090005)
        // (-1) + 5 = 4
        assertEqualsHex("ADDI failed", 4, result)
    }

    @Test
    fun testADDIU() {
        val result = registerOperation(10, 0x25090005)
        // 10 + 5 = 15
        assertEqualsHex("ADDIU failed", 15, result)
    }

    @Test
    fun testADDIU2() {
        val result = registerOperation(-1, 0x25090005)
        // (-1) + 5 = 4
        assertEqualsHex("ADDIU failed", 4, result)
    }

    @Test
    fun testSLTI() {
        val result = registerOperation(4, 0x29090005)
        assertEqualsHex("SLTI failed", 1, result)
    }

    @Test
    fun testSLTI2() {
        val result = registerOperation(6, 0x29090005)
        assertEqualsHex("SLTI failed", 0, result)
    }

    @Test
    fun testSLTIU() {
        val result = registerOperation(-1, 0x2d090005)
        assertEqualsHex("SLTIU failed", 0, result)
    }

    @Test
    fun testSLTIU2() {
        val result = registerOperation(4, 0x2d090005)
        assertEqualsHex("SLTIU failed", 1, result)
    }

    @Test
    fun testANDI() {
        val result = registerOperation(0b011, 0x31090005)
        assertEqualsHex("ANDI failed", 0b001, result)
    }

    @Test
    fun testORI() {
        val result = registerOperation(0b011, 0x35090005)
        assertEqualsHex("ORI failed", 0b111, result)
    }

    @Test
    fun testXORI() {
        val result = registerOperation(0b011, 0x39090005)
        assertEqualsHex("XORI failed", 0b110, result)
    }

    @Test
    fun testLUI() {
        memory.writeWord(Motherboard.CPU_START_POINT, 0x3c09FFFF)
        cpu.iterate()
        assertEqualsHex("LUI failed", 0xFFFF_0000, cpu.getRegister(9))
    }

    @Test
    fun testLLO() {
        memory.writeWord(Motherboard.CPU_START_POINT, 0x6009FFFF)
        cpu.iterate()
        assertEqualsHex("LLO failed", 0x0000_FFFF, cpu.getRegister(9))
    }

    @Test
    fun testLHI() {
        memory.writeWord(Motherboard.CPU_START_POINT, 0x6409FFFF)
        cpu.iterate()
        assertEqualsHex("LHI failed", 0xFFFF_0000, cpu.getRegister(9))
    }

    @Test
    fun testLB() {
        memory.writeWord(0x00004, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x81090004.toInt())
        cpu.iterate()
        assertEqualsHex("LB failed", 0x78, cpu.getRegister(9))
    }

    @Test
    fun testLB2() {
        memory.writeWord(0x00004, 0x123456F8)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x81090004.toInt())
        cpu.iterate()
        assertEqualsHex("LB failed", 0xFFFF_FFF8, cpu.getRegister(9))
    }

    @Test
    fun testLH() {
        memory.writeWord(0x00004, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x85090004.toInt())
        cpu.iterate()
        assertEqualsHex("LH failed", 0x5678, cpu.getRegister(9))
    }

    @Test
    fun testLH2() {
        memory.writeWord(0x00004, 0x1234F678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x85090004.toInt())
        cpu.iterate()
        assertEqualsHex("LH failed", 0xFFFF_F678, cpu.getRegister(9))
    }

    @Test
    fun testLWL() {
        memory.writeWord(0x00004, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x89090004.toInt())
        cpu.iterate()
        assertEqualsHex("LWL failed", 0x78000000, cpu.getRegister(9))
    }

    @Test
    fun testLWL2() {
        memory.writeWord(0x00004, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x89090005.toInt())
        cpu.iterate()
        assertEqualsHex("LWL failed", 0x56780000, cpu.getRegister(9))
    }

    @Test
    fun testLWL3() {
        memory.writeWord(0x00004, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x89090006.toInt())
        cpu.iterate()
        assertEqualsHex("LWL failed", 0x34567800, cpu.getRegister(9))
    }

    @Test
    fun testLWL4() {
        memory.writeWord(0x00004, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x89090007.toInt())
        cpu.iterate()
        assertEqualsHex("LWL failed", 0x12345678, cpu.getRegister(9))
    }

    @Test
    fun testLW() {
        memory.writeWord(0x00004, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x8d090004.toInt())
        cpu.iterate()
        assertEqualsHex("LW failed", 0x12345678, cpu.getRegister(9))
    }

    @Test
    fun testLBU() {
        memory.writeWord(0x00004, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x91090004.toInt())
        cpu.iterate()
        assertEqualsHex("LBU failed", 0x78, cpu.getRegister(9))
    }

    @Test
    fun testLBU2() {
        memory.writeWord(0x00004, 0x123456F8)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x91090004.toInt())
        cpu.iterate()
        assertEqualsHex("LBU failed", 0xF8, cpu.getRegister(9))
    }

    @Test
    fun testLHU() {
        memory.writeWord(0x00004, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x95090004.toInt())
        cpu.iterate()
        assertEqualsHex("LHU failed", 0x5678, cpu.getRegister(9))
    }

    @Test
    fun testLHU2() {
        memory.writeWord(0x00004, 0x1234F678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x95090004.toInt())
        cpu.iterate()
        assertEqualsHex("LHU failed", 0xF678, cpu.getRegister(9))
    }

    @Test
    fun testLWR() {
        memory.writeWord(0x00004, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x99090004.toInt())
        cpu.iterate()
        assertEqualsHex("LWR failed", 0x12345678, cpu.getRegister(9))
    }

    @Test
    fun testLWR2() {
        memory.writeWord(0x00004, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x99090005.toInt())
        cpu.iterate()
        assertEqualsHex("LWR failed", 0x00123456, cpu.getRegister(9))
    }

    @Test
    fun testLWR3() {
        memory.writeWord(0x00004, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x99090006.toInt())
        cpu.iterate()
        assertEqualsHex("LWR failed", 0x00001234, cpu.getRegister(9))
    }

    @Test
    fun testLWR4() {
        memory.writeWord(0x00004, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0x99090007.toInt())
        cpu.iterate()
        assertEqualsHex("LWR failed", 0x00000012, cpu.getRegister(9))
    }

    @Test
    fun testSB() {
        cpu.setRegister(8, 0x1000)
        cpu.setRegister(10, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0xa10a0004.toInt())
        cpu.iterate()
        assertEqualsHex("SB failed", 0x0000_0078, memory.readWord(0x1004))
    }

    @Test
    fun testSH() {
        cpu.setRegister(8, 0x1000)
        cpu.setRegister(10, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0xa50a0004.toInt())
        cpu.iterate()
        assertEqualsHex("SH failed", 0x0000_5678, memory.readWord(0x1004))
    }

    @Test
    fun testSWL() {
        cpu.setRegister(8, 0x1000)
        cpu.setRegister(10, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0xa90a0004.toInt())
        cpu.iterate()
        assertEqualsHex("SWL failed", 0x0000_0012, memory.readWord(0x1004))
    }

    @Test
    fun testSWL2() {
        cpu.setRegister(8, 0x1000)
        cpu.setRegister(10, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0xa90a0005.toInt())
        cpu.iterate()
        assertEqualsHex("SWL failed", 0x0000_1234, memory.readWord(0x1004))
    }

    @Test
    fun testSWL3() {
        cpu.setRegister(8, 0x1000)
        cpu.setRegister(10, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0xa90a0006.toInt())
        cpu.iterate()
        assertEqualsHex("SWL failed", 0x0012_3456, memory.readWord(0x1004))
    }

    @Test
    fun testSWL4() {
        cpu.setRegister(8, 0x1000)
        cpu.setRegister(10, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0xa90a0007.toInt())
        cpu.iterate()
        assertEqualsHex("SWL failed", 0x1234_5678, memory.readWord(0x1004))
    }

    @Test
    fun testSW() {
        cpu.setRegister(8, 0x1000)
        cpu.setRegister(10, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0xad0a0004.toInt())
        cpu.iterate()
        assertEqualsHex("SW failed", 0x12345678, memory.readWord(0x1004))
    }

    @Test
    fun testSWR() {
        cpu.setRegister(8, 0x1000)
        cpu.setRegister(10, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0xb90a0004.toInt())
        cpu.iterate()
        assertEqualsHex("SWR failed", 0x1234_5678, memory.readWord(0x1004))
    }

    @Test
    fun testSWR2() {
        cpu.setRegister(8, 0x1000)
        cpu.setRegister(10, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0xb90a0005.toInt())
        cpu.iterate()
        assertEqualsHex("SWR failed", 0x0012_3456, memory.readWord(0x1004))
    }

    @Test
    fun testSWR3() {
        cpu.setRegister(8, 0x1000)
        cpu.setRegister(10, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0xb90a0006.toInt())
        cpu.iterate()
        assertEqualsHex("SWR failed", 0x0000_1234, memory.readWord(0x1004))
    }

    @Test
    fun testSWR4() {
        cpu.setRegister(8, 0x1000)
        cpu.setRegister(10, 0x12345678)
        memory.writeWord(Motherboard.CPU_START_POINT, 0xb90a0007.toInt())
        cpu.iterate()
        assertEqualsHex("SWR failed", 0x0000_0012, memory.readWord(0x1004))
    }
}