package com.cout970.magneticraft.misc.network

import io.netty.buffer.ByteBuf

@Suppress("unused")
/**
 * Created by cout970 on 09/07/2016.
 *
 * Indexed Binary Data
 * Encodes and decodes data from a ByteBuf using a index to identify the data
 * See gui.common.Constants.kt for ids
 */
class IBD {

    private val map = mutableMapOf<Int, Any>()

    fun setInteger(id: Int, value: Int) {
        map.put(id, value)
    }

    fun setLong(id: Int, value: Long) {
        map.put(id, value)
    }

    fun setFloat(id: Int, value: Float) {
        map.put(id, value)
    }

    fun setDouble(id: Int, value: Double) {
        map.put(id, value)
    }

    fun setBoolean(id: Int, value: Boolean) {
        map.put(id, value)
    }

    fun setString(id: Int, value: String) {
        map.put(id, value)
    }

    fun setByteArray(id: Int, value: ByteArray) {
        map.put(id, value)
    }

    fun getInteger(id: Int) = map[id] as Int

    fun getLong(id: Int) = map[id] as Long

    fun getFloat(id: Int) = map[id] as Float

    fun getDouble(id: Int) = map[id] as Double

    fun getBoolean(id: Int) = map[id] as Boolean

    fun getString(id: Int) = map[id] as String

    fun getByteArray(id: Int) = map[id] as ByteArray

    fun getInteger(id: Int, action: (Int) -> Unit) {
        if (hasKey(id)) {
            val value = map[id]
            if (value is Int) {
                action.invoke(value)
            }
        }
    }

    fun getLong(id: Int, action: (Long) -> Unit) {
        if (hasKey(id)) {
            val value = map[id]
            if (value is Long) {
                action.invoke(value)
            }
        }
    }

    fun getFloat(id: Int, action: (Float) -> Unit) {
        if (hasKey(id)) {
            val value = map[id]
            if (value is Float) {
                action.invoke(value)
            }
        }
    }

    fun getDouble(id: Int, action: (Double) -> Unit) {
        if (hasKey(id)) {
            val value = map[id]
            if (value is Double) {
                action.invoke(value)
            }
        }
    }

    fun getBoolean(id: Int, action: (Boolean) -> Unit) {
        if (hasKey(id)) {
            val value = map[id]
            if (value is Boolean) {
                action.invoke(value)
            }
        }
    }

    fun getString(id: Int, action: (String) -> Unit) {
        if (hasKey(id)) {
            val value = map[id]
            if (value is String) {
                action.invoke(value)
            }
        }
    }

    fun getByteArray(id: Int, action: (ByteArray) -> Unit) {
        if (hasKey(id)) {
            val value = map[id]
            if (value is ByteArray) {
                action.invoke(value)
            }
        }
    }

    fun hasKey(id: Int) = map.containsKey(id)

    fun remove(id: Int) {
        map.remove(id)
    }

    fun clear() {
        map.clear()
    }

    fun merge(other: IBD) {
        for ((key, value) in other.map) {
            map.put(key, value)
        }
    }

    fun fromBuffer(buf: ByteBuf) {
        clear()
        val size = buf.readInt()
        for (i in 0 until size) {
            val type = buf.readByte()
            val id = buf.readInt()
            when (type.toInt()) {
                1 -> {
                    setInteger(id, buf.readInt())
                }
                2 -> {
                    setLong(id, buf.readLong())
                }
                3 -> {
                    setFloat(id, buf.readFloat())
                }
                4 -> {
                    setDouble(id, buf.readDouble())
                }
                5 -> {
                    setBoolean(id, buf.readBoolean())
                }
                6 -> {
                    setString(id, buf.readString())
                }
                7 -> {
                    setByteArray(id, buf.readByteArray())
                }
            }
        }
    }

    fun toBuffer(buf: ByteBuf) {
        buf.writeInt(map.size)
        for ((id, value) in map) {
            val type = when (value) {
                is Int -> 1
                is Long -> 2
                is Float -> 3
                is Double -> 4
                is Boolean -> 5
                is String -> 6
                is ByteArray -> 7
                else -> throw IllegalStateException("Invalid value type: ${value.javaClass}, value:$value")
            }
            buf.writeByte(type)
            buf.writeInt(id)
            when (type) {
                1 -> {
                    buf.writeInt(value as Int)
                }
                2 -> {
                    buf.writeLong(value as Long)
                }
                3 -> {
                    buf.writeFloat(value as Float)
                }
                4 -> {
                    buf.writeDouble(value as Double)
                }
                5 -> {
                    buf.writeBoolean(value as Boolean)
                }
                6 -> {
                    buf.writeString(value as String)
                }
                7 -> {
                    buf.writeByteArray(value as ByteArray)
                }
                else -> throw IllegalStateException("Invalid value type: ${value.javaClass}, value:$value")
            }
        }
    }
}


