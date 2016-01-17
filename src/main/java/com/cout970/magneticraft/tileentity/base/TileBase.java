package com.cout970.magneticraft.tileentity.base;

import com.cout970.magneticraft.api.base.IDataStorage;
import net.darkaqua.blacksmith.api.network.packet.ITileEntityUpdatePacket;
import net.darkaqua.blacksmith.api.network.packet.PacketFactory;
import net.darkaqua.blacksmith.api.storage.DataElementFactory;
import net.darkaqua.blacksmith.api.storage.IDataCompound;
import net.darkaqua.blacksmith.api.tileentity.defaults.DefaultTileEntityDefinition;

/**
 * Created by cout970 on 16/12/2015.
 */
public class TileBase extends DefaultTileEntityDefinition implements IDataStorage {

    @Override
    public ITileEntityUpdatePacket getUpdatePacket() {
        IDataCompound data = DataElementFactory.createDataCompound();
        writeUpdatePacket(data);
        return PacketFactory.createTileEntityUpdatePacket(parent, data);
    }

    @Override
    public void onUpdatePacketArrives(ITileEntityUpdatePacket packet) {
        readUpdatePacket(packet.getDataCompound());
    }

    protected void writeUpdatePacket(IDataCompound data) {
        saveData(data);
    }

    protected void readUpdatePacket(IDataCompound dataCompound) {
        loadData(dataCompound);
    }

}
