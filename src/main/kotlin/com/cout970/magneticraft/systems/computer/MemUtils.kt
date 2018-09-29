package com.cout970.magneticraft.systems.computer

import com.cout970.magneticraft.misc.split
import com.cout970.magneticraft.misc.splitSet

/**
 * Created by cout970 on 2017/07/22.
 */

interface IVariable {
    val size: Int

    fun read(addr: Int): Byte
    fun write(addr: Int, value: Byte)
}

class ReadWriteStruct(val name: String, vararg val vars: IVariable) : IVariable {

    val variables: Map<Int, Pair<IVariable, Int>>
    val addresses: Map<IVariable, Int>

    init {
        val map = mutableMapOf<Int, Pair<IVariable, Int>>()
        val map2 = mutableMapOf<IVariable, Int>()
        var currentVar = 0
        var pos = 0

        while (currentVar < vars.size) {
            val variable = vars[currentVar]

            map2 += variable to pos
            (0 until variable.size).forEach {
                map += pos++ to (variable to it)
            }
            currentVar++
        }
        addresses = map2
        variables = map
    }

    override val size: Int = variables.size

    override fun read(addr: Int): Byte {
        val (variable, pos) = variables[addr] ?: return 0
        return variable.read(pos)
    }

    override fun write(addr: Int, value: Byte) {
        val (variable, pos) = variables[addr] ?: return
        return variable.write(pos, value)
    }

    override fun toString(): String = toString(0)

    fun toString(indent: Int): String = buildString {
        appendln("struct {")
        vars.forEach { variable ->
            val pos = addresses[variable]
            if (variable is ReadWriteStruct) {
                appendln("/* %3d 0x%02x */    %s".format(pos, pos, variable.toString(18).trim()))
            } else {
                appendln("/* %3d 0x%02x */    %s".format(pos, pos, variable.toString()))
            }
        }
        append("} $name;")
    }.split('\n').joinToString("\n") { (0 until indent).joinToString("") { " " } + it }
}

class ReadOnlyInt(val name: String, val setter: () -> Int) : IVariable {

    override val size = 4

    override fun read(addr: Int): Byte = setter().split(addr)
    override fun write(addr: Int, value: Byte) = Unit

    override fun toString(): String = "const Int $name;"
}

class ReadWriteInt(val name: String, val setter: (Int) -> Unit, val getter: () -> Int) : IVariable {

    override val size = 4

    override fun read(addr: Int) = getter().split(addr)

    override fun write(addr: Int, value: Byte) {
        setter(getter().splitSet(addr, value))
    }

    override fun toString(): String = "Int $name;"
}

class ReadOnlyShort(val name: String, val getter: () -> Short) : IVariable {

    override val size = 2

    override fun read(addr: Int): Byte = getter().toInt().split(addr)
    override fun write(addr: Int, value: Byte) = Unit

    override fun toString(): String = "const Short $name;"
}

class ReadWriteShort(val name: String, val setter: (Short) -> Unit, val getter: () -> Short) : IVariable {

    override val size = 2

    override fun read(addr: Int) = getter().toInt().split(addr)

    override fun write(addr: Int, value: Byte) {
        setter(getter().toInt().splitSet(addr, value).toShort())
    }

    override fun toString(): String = "Short $name;"
}

class ReadOnlyByte(val name: String, val getter: () -> Byte) : IVariable {

    override val size = 1

    override fun read(addr: Int): Byte = getter().toInt().split(addr)
    override fun write(addr: Int, value: Byte) = Unit

    override fun toString(): String = "const Byte $name;"
}

class ReadWriteByte(val name: String, val setter: (Byte) -> Unit, val getter: () -> Byte) : IVariable {

    override val size = 1

    override fun read(addr: Int) = getter()

    override fun write(addr: Int, value: Byte) {
        setter(value)
    }

    override fun toString(): String = "Byte $name;"
}

class ReadOnlyByteArray(
    val name: String,
    val arrayGetter: () -> ByteArray
) : IVariable {

    override val size: Int = arrayGetter().size

    override fun read(addr: Int): Byte = arrayGetter()[addr]

    override fun write(addr: Int, value: Byte) = Unit

    override fun toString(): String = "const Byte $name[$size];"
}

class ReadOnlyIntArray(
    val name: String,
    val arrayGetter: () -> IntArray
) : IVariable {

    override val size: Int = arrayGetter().size * 4

    override fun read(addr: Int): Byte = arrayGetter()[addr / 4].split(addr % 4)

    override fun write(addr: Int, value: Byte) = Unit

    override fun toString(): String = "const Int $name[${arrayGetter().size}];"
}

class ReadWriteByteArray(
    val name: String,
    val array: ByteArray
) : IVariable {

    override val size: Int = array.size

    override fun read(addr: Int): Byte = array[addr]

    override fun write(addr: Int, value: Byte) {
        array[addr] = value
    }

    override fun toString(): String = "Byte $name[$size];"
}