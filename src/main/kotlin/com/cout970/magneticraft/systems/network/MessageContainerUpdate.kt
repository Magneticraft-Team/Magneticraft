package com.cout970.magneticraft.systems.network

import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.systems.gui.ContainerBase
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

/**
 * Created by cout970 on 10/07/2016.
 */

/**
 * Server -> Client
 * This message is used to send data from a container in the server to the GUI in the client
 */
class MessageContainerUpdate(val ibd: IBD = IBD()) {

    constructor(buf: ByteBuf) : this() {
        ibd.fromBuffer(buf)
    }

    fun toBytes(buf: ByteBuf) {
        ibd.toBuffer(buf)
    }

    fun handle(supplier: Supplier<NetworkEvent.Context>) {
        val ctx = supplier.get()
        if (ctx.direction.receptionSide != LogicalSide.CLIENT) return

        val container = Minecraft.getInstance().player.openContainer
        if (container is ContainerBase) {
            container.receiveDataFromServer(ibd)
        }
    }
}