package com.cout970.magneticraft.systems.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.computer.IRW
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.systems.config.Config
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.net.Socket
import java.nio.charset.Charset
import javax.net.ssl.SSLSocketFactory

/**
 * Created by cout970 on 2016/10/31.
 */
class DeviceNetworkCard(val parent: ITileRef) : IDevice, ITileRef by parent {

    val status = 0
    var isActive: Boolean = true
    val internetAllowed: Boolean get() = Config.allowTcpConnections
    val maxSockets get() = Config.maxTcpConnections

    val targetIp = ByteArray(80)
    var targetPort = 0

    var targetMac = 0

    val inputBuffer = ByteArray(1024)
    var inputBufferPtr = 0
    val outputBuffer = ByteArray(1024)
    var outputBufferPtr = 0
    var hardwareLock = 0

    var connectionError = 0

    var debugLevel = 0

    var socket: Socket? = null

    companion object {
        var activeSockets = 0

        const val NO_ERROR = 0
        const val INVALID_PORT = 1
        const val INVALID_IP_SIZE = 2
        const val EXCEPTION_PARSING_IP = 3
        const val EXCEPTION_OPEN_SOCKET = 4
        const val INTERNET_NOT_ALLOWED = 5
        const val MAX_SOCKET_REACH = 6
        const val SOCKET_CLOSED = 7
        const val UNABLE_TO_READ_PACKET = 8
        const val UNABLE_TO_SEND_PACKET = 9
        const val INVALID_OUTPUT_BUFFER_POINTER = 10
        const val INVALID_INPUT_BUFFER_POINTER = 11
    }

    val memStruct = ReadWriteStruct("network_header",
        ReadWriteStruct("device_header",
            ReadOnlyByte("online") { if (isActive) 1 else 0 },
            ReadOnlyByte("type") { 2 },
            ReadOnlyShort("status") { status.toShort() }
        ),
        ReadOnlyByte("internetAllowed") { if (internetAllowed) 1 else 0 },
        ReadOnlyByte("maxSockets") { maxSockets.toByte() },
        ReadOnlyByte("activeSockets") { activeSockets.toByte() },
        ReadWriteByte("signal", { signal(it.toInt()) }, { 0 }),
        ReadOnlyInt("macAddress") { getMacAddress() },
        ReadWriteInt("targetMac", { targetMac = it }, { targetMac }),
        ReadWriteInt("targetPort", { targetPort = it }, { targetPort }),
        ReadWriteByteArray("targetIp", targetIp),
        ReadOnlyInt("connectionOpen") { if (socket?.isClosed != false) 0 else 1 },
        ReadOnlyInt("connectionError") { connectionError },
        ReadWriteInt("inputBufferPtr", { inputBufferPtr = it }, { inputBufferPtr }),
        ReadWriteInt("outputBufferPtr", { outputBufferPtr = it }, { outputBufferPtr }),
        ReadWriteInt("hardwareLock", { hardwareLock = it }, { hardwareLock }),
        ReadWriteByteArray("inputBuffer", inputBuffer),
        ReadWriteByteArray("outputBuffer ", outputBuffer)
    )

    fun getMacAddress(): Int {
        if (parent == FakeRef) {
            return 0xABCDEF01.toInt()
        }
        return parent.pos.hashCode()
    }

    private fun log(level: Int, vararg any: Any?) {
        if (debugLevel > level) {
//            debug(*any)
            println(any.joinToString("\n"))
        }
    }

    override fun update() {
        socket?.let {
            if (it.isClosed) {
                connectionError = SOCKET_CLOSED
                closeTcpConnection()
            } else {
                if (hardwareLock == 0) {

                    // Write buffer data to socket
                    try {
                        // check outputBufferPtr is valid
                        if (outputBufferPtr > outputBuffer.size || outputBufferPtr < 0) {
                            connectionError = INVALID_OUTPUT_BUFFER_POINTER
                            log(2,
                                "Error INVALID_OUTPUT_BUFFER_POINTER $outputBufferPtr not in [0, ${outputBuffer.size})")
                            closeTcpConnection()
                            return
                        }

                        // Write
                        if (outputBufferPtr > 0) {
                            it.getOutputStream().write(outputBuffer, 0, outputBufferPtr)

                            // debug print request
                            if (outputBufferPtr > 0) {
                                log(2, "Sending data: " + String(outputBuffer, 0, outputBufferPtr))
                            }

                            outputBufferPtr = 0
                        }
                    } catch (e: Exception) {
                        connectionError = UNABLE_TO_SEND_PACKET
                        log(2, "Error UNABLE_TO_SEND_PACKET")

                        closeTcpConnection()
                        e.printStackTrace()
                    }

                    // Read data from socket to buffer
                    try {
                        // check inputBufferPtr is valid
                        if (inputBufferPtr > inputBuffer.size || inputBufferPtr < 0) {
                            connectionError = INVALID_INPUT_BUFFER_POINTER
                            log(2, "Error INVALID_INPUT_BUFFER_POINTER $inputBufferPtr no in [0, ${inputBuffer.size})")
                            closeTcpConnection()
                            return
                        }

                        // Read
                        if (inputBufferPtr == 0) {
                            val read = it.getInputStream().read(inputBuffer, 0, inputBuffer.size)

                            if (read > 0) {
                                inputBufferPtr += read
                                log(2, "Receiving data: " + String(inputBuffer, 0, read))
                            } else if (read == -1) {
                                connectionError = SOCKET_CLOSED
                                closeTcpConnection()
                            }
                        }

                    } catch (e: Exception) {
                        connectionError = UNABLE_TO_READ_PACKET
                        log(2, "Error UNABLE_TO_READ_PACKET")
                        closeTcpConnection()
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun signal(signal: Int) {
        when (signal) {
            1 -> openTcpConnection()
            2 -> closeTcpConnection()
            3 -> openTcpSslConnection()
        }
    }

    fun closeTcpConnection() {
        socket?.let {
            log(1, "Closing connection")
            activeSockets--
            it.close()
        }
        socket = null
    }

    fun openTcpConnection() {
        openConnection { ip, port -> Socket(ip, port) }
    }

    fun openTcpSslConnection() {
        openConnection { ip, port ->
            val socketFactory = SSLSocketFactory.getDefault()
            socketFactory.createSocket(ip, port)
        }
    }

    fun openConnection(factory: (String, Int) -> Socket) {
        log(1, "Opening connection")
        if (!internetAllowed) {
            connectionError = INTERNET_NOT_ALLOWED
            log(1, "Error: INTERNET_NOT_ALLOWED")
            return
        }
        if (activeSockets >= maxSockets) {
            connectionError = MAX_SOCKET_REACH
            log(1, "Error: MAX_SOCKET_REACH")
            return
        }
        if (targetPort <= 0 || targetPort > 0xFFFF) {
            connectionError = INVALID_PORT
            log(1, "Error: INVALID_PORT")
            return
        }
        val ipStr: String
        try {
            val size = targetIp.indexOf(0)
            if (size == -1 || size >= 80) {
                connectionError = INVALID_IP_SIZE
                return
            }
            val tmp = ByteArray(size)
            System.arraycopy(targetIp, 0, tmp, 0, size)
            ipStr = tmp.toString(Charset.forName("US-ASCII"))
            log(1, "Ip parsed: '$ipStr'")
        } catch (e: Exception) {
            e.printStackTrace()
            connectionError = EXCEPTION_PARSING_IP
            log(1, "Error: EXCEPTION_PARSING_IP")
            return
        }
        try {
            socket = factory(ipStr, targetPort)
            connectionError = NO_ERROR
            activeSockets++
            outputBufferPtr = 0
            inputBufferPtr = 0
            log(1, "Socket created with ip '$ipStr' and port '$targetPort'")
        } catch (e: Exception) {
            e.printStackTrace()
            connectionError = EXCEPTION_OPEN_SOCKET
            log(1, "Error: EXCEPTION_OPEN_SOCKET")

            closeTcpConnection()
        }
    }

    override fun readByte(bus: IRW, addr: Int): Byte {
        return memStruct.read(addr)
    }

    override fun writeByte(bus: IRW, addr: Int, data: Byte) {
        memStruct.write(addr, data)
    }

    override fun getWorld(): World = parent.world

    override fun getPos(): BlockPos = parent.pos

    // TODO fix this
    override fun serialize() = emptyMap<String, Any>()

    override fun deserialize(map: Map<String, Any>) = Unit
}

//fun main(args: Array<String>) {
//    val socketFactory = SSLSocketFactory.getDefault()
//    val socket = socketFactory.createSocket("pastebin.com", 443)//"raw.githubusercontent.com", 443)//
//
//    socket.outputStream.apply {
//
//        //        write(("GET /Magneticraft-Team/Magneticraft/1.12/src/main/resources/assets/magneticraft/cpu/bios.bin HTTP/1.1\r\n" +
////               "Host: raw.githubusercontent.com\r\n" +
////               "Connection: close\r\n" +
////               "\r\n").toByteArray())
//
//        write(("GET /raw/pJwsc2XP HTTP/1.0\r\n" +
//                "Host: pastebin.com\r\n" +
//                "Connection: close\r\n" +
//                "\r\n").toByteArray())
//    }
//
//    val str = socket.inputStream.readBytes().toString(Charsets.UTF_8)
//
//    println(str)
//    socket.close()
//}