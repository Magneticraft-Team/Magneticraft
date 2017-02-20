package com.cout970.magneticraft.network

import com.cout970.magneticraft.gui.common.ContainerBase
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.network.readUUID
import com.cout970.magneticraft.misc.network.writeUUID
import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import java.util.*

/**
 * Created by cout970 on 2016/10/01.
 */

/**
 * Client -> Server
 * This message is used to send data from a GUI in the client to the Container in the server
 */
class MessageGuiUpdate() : IMessage {

    //Data buffer
    var ibd: IBD? = null
    //player uuid
    var uuid: UUID? = null

    constructor(ibd: IBD, uuid: UUID) : this() {
        this.ibd = ibd
        this.uuid = uuid
    }

    override fun fromBytes(buf: ByteBuf) {
        val ibd = IBD()
        uuid = buf.readUUID()
        ibd.fromBuffer(buf)
        this.ibd = ibd
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeUUID(uuid!!)
        ibd!!.toBuffer(buf)
    }

    companion object : IMessageHandler<MessageGuiUpdate, IMessage> {

        override fun onMessage(message: MessageGuiUpdate?, ctx: MessageContext?): IMessage? {
            if (ctx!!.side == Side.SERVER) {
                val server = FMLCommonHandler.instance().minecraftServerInstance ?: return null
                val player = server.playerList.getPlayerByUUID(message!!.uuid) ?: return null
                val container = player.openContainer
                if (container is ContainerBase) {
                    container.receiveDataFromClient(message.ibd!!)
                }
            } else {
                throw IllegalStateException()
            }
            return null
        }
    }
}