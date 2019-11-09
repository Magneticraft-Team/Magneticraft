package com.cout970.magneticraft.systems.network


import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.network.readBlockPos
import com.cout970.magneticraft.misc.network.writeBlockPos
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.systems.tileentities.TileBase
import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.util.math.BlockPos
import net.minecraft.world.dimension.DimensionType
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

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
class MessageTileUpdate(
    //Data buffer
    val ibd: IBD,
    //Position of the block
    val pos: BlockPos,
    //Dimension of the block, only for server side
    val dimension: Int
) {

    constructor(buf: ByteBuf) : this(
        IBD().fromBuffer(buf),
        buf.readBlockPos(),
        buf.readInt()
    )

    fun toBytes(buf: ByteBuf) {
        ibd.toBuffer(buf)
        buf.writeBlockPos(pos)
        buf.writeInt(dimension)
    }

    fun handle(supplier: Supplier<NetworkEvent.Context>) {
        val ctx = supplier.get()
        when (ctx.direction.receptionSide!!) {
            LogicalSide.CLIENT -> handleClient(ctx)
            LogicalSide.SERVER -> handleServer(ctx)
        }
    }

    fun handleClient(ctx: NetworkEvent.Context) {
        ctx.enqueueWork {
            val world = Minecraft.getInstance().world
            if (world.dimension.type.id == dimension) {
                world.getTile<TileBase>(pos)?.receiveSyncData(ibd, LogicalSide.SERVER)
            }
        }
    }

    fun handleServer(ctx: NetworkEvent.Context) {
        ctx.enqueueWork {
            val type = DimensionType.getById(dimension) ?: return@enqueueWork
            val world = ctx.sender?.server?.getWorld(type) ?: return@enqueueWork
            world.getTile<TileBase>(pos)?.receiveSyncData(ibd, LogicalSide.CLIENT)
        }
    }
}