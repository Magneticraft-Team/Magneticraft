package com.cout970.magneticraft.systems.network

import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.network.readUUID
import com.cout970.magneticraft.misc.network.writeUUID
import com.cout970.magneticraft.systems.gui.ContainerBase
import io.netty.buffer.ByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.LogicalSidedProvider
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

/**
 * Created by cout970 on 2016/10/01.
 */

/**
 * Client -> Server
 * This message is used to send data from a GUI in the client to the Container in the server
 */
class MessageGuiUpdate(
    val ibd: IBD,
    val uuid: UUID
) {

    constructor(buf: ByteBuf) : this(
        IBD().fromBuffer(buf),
        buf.readUUID()
    )

    fun toBytes(buf: ByteBuf) {
        buf.writeUUID(uuid)
        ibd.toBuffer(buf)
    }

    fun handle(supplier: Supplier<NetworkEvent.Context>) {
        val ctx = supplier.get()
        if (ctx.direction.receptionSide != LogicalSide.SERVER) return

        ctx.enqueueWork {
            val server = LogicalSidedProvider.INSTANCE
                .get<Supplier<MinecraftServer>>(LogicalSide.SERVER)
                .get()

            val player = server.playerList.getPlayerByUUID(uuid) ?: return@enqueueWork
            val container = player.openContainer ?: return@enqueueWork
            if (container is ContainerBase) {
                container.receiveDataFromClient(ibd)
            }
        }
    }
}