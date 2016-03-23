package com.cout970.magneticraft.tileentity;

import com.cout970.magneticraft.ManagerNetwork;
import com.cout970.magneticraft.network.MessageServerUpdate;
import net.darkaqua.blacksmith.Game;
import net.darkaqua.blacksmith.storage.DataElementFactory;
import net.darkaqua.blacksmith.storage.IDataCompound;
import net.darkaqua.blacksmith.storage.IDataStorage;
import net.darkaqua.blacksmith.util.WorldRef;
import net.darkaqua.blacksmith.vectors.Vect3d;
import net.darkaqua.blacksmith.vectors.Vect3i;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

/**
 * Created by cout970 on 16/12/2015.
 */
public class TileBase extends TileEntity implements IDataStorage, ITickable {

    public void sendUpdateToClient() {
        if (Game.isClient()) { return; }
        IDataCompound nbt = DataElementFactory.createDataCompound();
        writeUpdatePacket(nbt);
        MessageServerUpdate message = new MessageServerUpdate(getWorldRef(), nbt);
        ManagerNetwork.CHANNEL.sendToAllAround(message, getWorld().provider.getDimensionId(), 64, new Vect3d(getPos()));
    }

    @Override
    public Packet getDescriptionPacket() {
        IDataCompound data = DataElementFactory.createDataCompound();
        writeUpdatePacket(data);
        return new S35PacketUpdateTileEntity(getPos(), 0, data.asNBTTagCompound());
    }


    @Override
    public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.S35PacketUpdateTileEntity pkt) {
        readUpdatePacket(DataElementFactory.fromNBTCompound(pkt.getNbtCompound()));
    }

    public void writeUpdatePacket(IDataCompound data) {
        saveData(data);
    }

    public synchronized void readUpdatePacket(IDataCompound dataCompound) {
        loadData(dataCompound);
    }


    public WorldRef getWorldRef() {
        return new WorldRef(getWorld(), new Vect3i(getPos()));
    }

    @Override
    public void loadData(IDataCompound data) {}

    @Override
    public void saveData(IDataCompound data) {}

    @Override
    public void update() {

    }
}
