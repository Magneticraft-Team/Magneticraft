package com.cout970.magneticraft.network;

import com.cout970.magneticraft.tileentity.TileBase;
import io.netty.buffer.ByteBuf;
import net.darkaqua.blacksmith.network.ExtendedByteBuf;
import net.darkaqua.blacksmith.network.INetworkContext;
import net.darkaqua.blacksmith.network.INetworkMessage;
import net.darkaqua.blacksmith.network.INetworkMessageHandler;
import net.darkaqua.blacksmith.storage.IDataCompound;
import net.darkaqua.blacksmith.util.WorldRef;
import net.darkaqua.blacksmith.vectors.Vect3i;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by cout970 on 17/01/2016.
 */
public class MessageServerUpdate implements INetworkMessage, INetworkMessageHandler<MessageServerUpdate, MessageServerUpdate> {

    private Vect3i pos;
    private int dimension;
    private IDataCompound data;

    public MessageServerUpdate() {
    }

    public MessageServerUpdate(WorldRef worldRef, IDataCompound nbt) {
        pos = worldRef.getPosition().copy();
        dimension = worldRef.getWorld().provider.getDimensionId();
        data = nbt;
    }

    @Override
    public void fromBytes(ByteBuf buf, ExtendedByteBuf helper) {
        pos = helper.readVect3i();
        dimension = buf.readInt();
        try {
            data = helper.readDataCompound();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf, ExtendedByteBuf helper) {
        helper.writeVect3i(pos);
        buf.writeInt(dimension);
        helper.writeDataCompound(data);
    }

    @Override
    public MessageServerUpdate onMessage(MessageServerUpdate message, INetworkContext context) {

        context.getServerContext().addScheduledTask(() -> {
            World w = context.getClientContext().getWorld();
            if (w.provider.getDimensionId() == message.dimension) {
                TileEntity tile = w.getTileEntity(message.pos.toBlockPos());
                if (tile instanceof TileBase) {
                    ((TileBase) tile).readUpdatePacket(message.data);
                }
            }
        });
        return null;
    }
}
