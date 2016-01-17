package com.cout970.magneticraft.network;

import com.cout970.magneticraft.tileentity.base.TileBase;
import io.netty.buffer.ByteBuf;
import net.darkaqua.blacksmith.api.network.ExtendedByteBuf;
import net.darkaqua.blacksmith.api.network.INetworkContext;
import net.darkaqua.blacksmith.api.network.INetworkMessage;
import net.darkaqua.blacksmith.api.network.INetworkMessageHandler;
import net.darkaqua.blacksmith.api.storage.IDataCompound;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.util.Vect3i;
import net.darkaqua.blacksmith.api.util.WorldRef;
import net.darkaqua.blacksmith.api.world.IWorld;

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
        dimension = worldRef.getWorld().getWorldDimension();
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

        context.addScheduledTask(() -> {
            IWorld w = context.getClientContext().getWorld();
            if (w.getWorldDimension() == message.dimension) {
                ITileEntity tile = w.getTileEntity(message.pos);
                ITileEntityDefinition def = tile.getTileEntityDefinition();
                if (def instanceof TileBase) {
                    ((TileBase) def).readUpdatePacket(message.data);
                }
            }
        });
        return null;
    }
}
