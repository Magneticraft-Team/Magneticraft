package com.cout970.magneticraft.network


import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.TileBase
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.server.FMLServerHandler

/**
 * Created by cout970 on 16/07/2016.
 *
 * This class is used to send data from the client to the server, or vice-versa
 * See TileBase.receiveSyncData(...)
 * See TileBase.sendSyncData(...)
 *
 * Server -> Client
 * Client -> Server
 */
class MessageTileUpdate() : IMessage {

    //Data buffer
    var ibd: IBD? = null
    //Position of the block
    var pos: BlockPos? = null
    //Dimension of the block, only for server side
    var dimension = 0

    constructor(ibd: IBD, pos: BlockPos, dimension: Int) : this() {
        this.ibd = ibd
        this.pos = pos
        this.dimension = dimension
    }

    override fun fromBytes(buf: ByteBuf) {
        val ibd = IBD()
        dimension = buf.readInt()
        pos = BlockPos(buf.readInt(), buf.readInt(), buf.readInt())
        ibd.fromBuffer(buf)
        this.ibd = ibd
    }

    override fun toBytes(buf: ByteBuf) {
        buf.writeInt(dimension)
        buf.writeInt(pos!!.x)
        buf.writeInt(pos!!.y)
        buf.writeInt(pos!!.z)
        ibd!!.toBuffer(buf)
    }

    companion object : IMessageHandler<MessageTileUpdate, IMessage> {

        override fun onMessage(message: MessageTileUpdate, ctx: MessageContext): IMessage? {
            if (ctx.side == Side.CLIENT) {
                handleClient(message)
            } else {
                handleServer(message)
            }
            return null
        }

        fun handleClient(message: MessageTileUpdate) {
            Minecraft.getMinecraft().addScheduledTask {
                val world = Minecraft.getMinecraft().theWorld
                if (world.provider.dimension == message.dimension) {
                    world.getTile<TileBase>(message.pos!!)?.receiveSyncData(message.ibd!!, Side.SERVER)
                }
            }
        }

        fun handleServer(message: MessageTileUpdate) {
            FMLServerHandler.instance().server.addScheduledTask {
                val world = DimensionManager.getWorld(message.dimension)
                if (world != null) {
                    world.getTile<TileBase>(message.pos!!)?.receiveSyncData(message.ibd!!, Side.CLIENT)
                }
            }
        }
    }
}