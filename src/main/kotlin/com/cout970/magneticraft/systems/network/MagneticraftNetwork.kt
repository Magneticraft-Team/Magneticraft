package com.cout970.magneticraft.systems.network

import com.cout970.magneticraft.misc.resource
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraftforge.fml.network.NetworkDirection
import net.minecraftforge.fml.network.NetworkRegistry
import net.minecraftforge.fml.network.simple.SimpleChannel

object MagneticraftNetwork {
    lateinit var channel: SimpleChannel

    @Suppress("INACCESSIBLE_TYPE")
    fun init() {
        channel = NetworkRegistry.newSimpleChannel(resource("network_channel"), { "1.0.0" }, { true }, { true })
        var id = 0

        channel.registerMessage(++id, MessageContainerUpdate::class.java,
            { msg, buf -> msg.toBytes(buf) },
            { MessageContainerUpdate(it) },
            { msg, ctx -> msg.handle(ctx) })

        channel.registerMessage(++id, MessageTileUpdate::class.java,
            { msg, buf -> msg.toBytes(buf) },
            { MessageTileUpdate(it) },
            { msg, ctx -> msg.handle(ctx) })

        channel.registerMessage(++id, MessageGuiUpdate::class.java,
            { msg, buf -> msg.toBytes(buf) },
            { MessageGuiUpdate(it) },
            { msg, ctx -> msg.handle(ctx) })
    }

    fun <MSG> sendTo(msg: MSG, player: ServerPlayerEntity) {
        channel.sendTo(msg, player.connection.networkManager, NetworkDirection.PLAY_TO_CLIENT)
    }

    fun <MSG> sendToServer(msg: MSG) {
        channel.sendToServer(msg)
    }
}