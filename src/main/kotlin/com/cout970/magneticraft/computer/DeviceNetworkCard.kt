package com.cout970.magneticraft.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.util.split
import com.cout970.magneticraft.util.splitRange
import com.cout970.magneticraft.util.splitSet
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset

/**
 * Created by cout970 on 2016/10/31.
 */
class DeviceNetworkCard(val parent: TileEntity) : IDevice {

    val status = 0
    val isActive: Boolean get() = !parent.isInvalid
    val internetAllowed: Boolean get() = Config.allowTcpConnections
    val maxConnections: Int get() = Math.max(Math.min(Config.maxTcpConnections - totalConnections.count { it.status == 1 }, 2), 0)

    var connection0 = Connection()
    var connection1 = Connection()

    init {
        totalConnections.add(connection0)
        totalConnections.add(connection1)
    }

    companion object {
        var totalConnections = mutableSetOf<Connection>()
    }

    /*
struct driver_device_internet_card {
/* 00 */        struct driver_device_header header;
/*    */
/* 04 */        byte internetAllowed; // 1 if the server config allow to use tcp sockets
/* 05 */        byte maxSockets;      //max amount of sockets that the server allow and the card supports
/* 06 */        byte none0; // unused
/* 07 */        byte none1; // unused
/* 08 */        struct driver_internet_socket sockets[0];
/*  */
};

sizeof = 96
struct driver_internet_socket {
/* 00 */    byte signal;          // byte used to send commands to the card
/* 01 */    byte status;          // current status, 1 if connected, 0 otherwise
/* 02 */    byte error;           // error code of the last connection
/* 03 */    byte flags;           // flags
/* 04 */    char ip[80];          // pointer to a string buffer used to start the connection, this can be an ip (255.255.255.255) or a url in format (http://www.example.com)
/* 84 */    int port;             // port used to access, for example 80 for http
/* 88 */    int inputStream;      // every time this value is read, java will call read() to the inputStream of the socket
/* 92 */   int outputStream;     // every time this value is write, java will call write() to the outputStream of the socket
};
     */

    override fun readByte(addr: Int): Byte {

        val a: Byte = when (addr) {
            0 -> (if (isActive) 1 else 0).toByte()                              //00, byte online
            1 -> 2.toByte()                                                     //01, byte type
            2, 3 -> status.split(addr - 2)                                      //02,03, short status
            4 -> if (internetAllowed) 1 else 0                                  //04 internet allowed
            5 -> maxConnections.toByte()                                        //05 maxConnections

            in 8..103 -> when (addr - 8) {
                1 -> connection0.status.toByte()
                2 -> connection0.error.toByte()
                3 -> connection0.flags.toByte()
                in 4..83 -> connection0.ip[addr - 8 - 4]
                in 84.splitRange() -> connection0.port.split(addr - 8 - 84)
                in 88.splitRange() -> connection0.read(addr - 8 - 88)
                else -> 0
            }
            in 104..199 -> when (addr - 104) {
                1 -> connection1.status.toByte()
                2 -> connection1.error.toByte()
                3 -> connection1.flags.toByte()
                in 4..83 -> connection1.ip[addr - 104 - 4]
                in 84.splitRange() -> connection1.port.split(addr - 104 - 84)
                in 88.splitRange() -> connection1.read(addr - 8 - 88)
                else -> 0
            }
            else -> 0
        }
        return a
    }

    override fun writeByte(addr: Int, data: Byte) {
        when (addr) {
            in 8..103 -> when (addr - 8) {
                0 -> connection0.signal(data)
                3 -> connection0.flags = data.toInt() and 0xFF
                in 4..83 -> connection0.ip[addr - 8 - 4] = data
                in 84.splitRange() -> connection0.port = connection0.port.splitSet(addr - 8 - 84, data)
                92 -> connection0.write(data)
            }
            in 104..199 -> when (addr - 104) {
                0 -> connection1.signal(data)
                3 -> connection1.flags = data.toInt() and 0xFF
                in 4..83 -> connection1.ip[addr - 104 - 4] = data
                in 84.splitRange() -> connection1.port = connection1.port.splitSet(addr - 104 - 84, data)
                92 -> connection1.write(data)
            }
        }
    }

    override fun getWorld(): World = parent.world

    override fun getPos(): BlockPos = parent.pos

    override fun deserializeNBT(nbt: NBTTagCompound?) = Unit

    override fun serializeNBT(): NBTTagCompound = NBTTagCompound()

    class Connection {
        var socket: Socket? = null
        val status: Int get() = if (socket != null && socket!!.isConnected) 1 else 0
        var error: Int = 0
        var flags: Int = 0
        var ip: ByteArray = ByteArray(80)
        var port: Int = 0

        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        var readCache: Int = -1

        fun read(index: Int): Byte {
            if (index == 3) {
                if (inputStream != null) {
                    readCache = inputStream!!.read()
                } else {
                    readCache = -1
                }

            }
            return readCache.split(index)
        }

        fun write(data: Byte) {
            if (outputStream != null) {
                outputStream!!.write(data.toInt() and 255)
            }
        }

        fun signal(data: Byte) {
            if (data == 0.toByte()) {
                if (socket != null) {
                    socket!!.close()
                    socket = null
                    inputStream = null
                    outputStream = null
                }
            } else if (data == 1.toByte()) {
                if (socket != null && !socket!!.isConnected) {
                    socket!!.close()
                    socket = null
                    inputStream = null
                    outputStream = null
                }
                if (socket == null) {
                    if (port <= 0 || port > 0xFFFF) {
                        error = 2
                        return
                    }
                    val ipstr: String
                    try {
                        val tmp = ByteArray(80)
                        System.arraycopy(ip, 0, tmp, 0, ip.indexOf(0))
                        ipstr = tmp.toString(Charset.forName("US-ASCII"))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        error = 3
                        return
                    }
                    try {
                        socket = Socket(ipstr, port)
                        inputStream = socket!!.inputStream
                        outputStream = socket!!.outputStream
                        error = 0
                    } catch (e: Exception) {
                        e.printStackTrace()
                        error = 4
                    }
                } else {
                    error = 1
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    val socket = Socket("pastebin.com", 80)
    socket.outputStream.write("GET /raw/wRpPhckZ HTTP/1.0\nHost: pastebin.com\n\n".toByteArray())
    val str = socket.inputStream.readBytes().toString(Charsets.UTF_8)
    println(str)
    println(str.replace('\r', 'n'))
    socket.close()
}