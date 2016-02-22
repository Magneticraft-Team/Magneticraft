package com.cout970.magneticraft.tileentity;

import com.cout970.magneticraft.ManagerNetwork;
import com.cout970.magneticraft.api.base.IDataStorage;
import com.cout970.magneticraft.network.MessageServerUpdate;
import net.darkaqua.blacksmith.api.Game;
import net.darkaqua.blacksmith.api.common.storage.DataElementFactory;
import net.darkaqua.blacksmith.api.common.storage.IDataCompound;
import net.darkaqua.blacksmith.api.common.tileentity.defaults.DefaultTileEntityDefinition;

/**
 * Created by cout970 on 16/12/2015.
 */
public class TileBase extends DefaultTileEntityDefinition implements IDataStorage {

    public void sendUpdateToClient(){
        if (Game.isClient()) return;
        IDataCompound nbt = DataElementFactory.createDataCompound();
        writeUpdatePacket(nbt);
        MessageServerUpdate message = new MessageServerUpdate(getParent().getWorldRef(), nbt);
        ManagerNetwork.CHANNEL.sendToAllAround(message, getWorld().getWorldDimension(), 64, getPosition().toVect3d());
    }

    @Override
    public IDataCompound getUpdateData() {
        IDataCompound data = DataElementFactory.createDataCompound();
        writeUpdatePacket(data);
        return data;
    }

    @Override
    public void onUpdateDataArrives(IDataCompound data) {
        readUpdatePacket(data);
    }

    public void writeUpdatePacket(IDataCompound data) {
        saveData(data);
    }

    public synchronized void readUpdatePacket(IDataCompound dataCompound) {
        loadData(dataCompound);
    }

}
