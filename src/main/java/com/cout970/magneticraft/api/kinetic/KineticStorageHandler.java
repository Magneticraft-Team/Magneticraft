package com.cout970.magneticraft.api.kinetic;

import net.darkaqua.blacksmith.api.common.intermod.IInterfaceIdentifier;
import net.darkaqua.blacksmith.api.common.storage.IDataCompound;
import net.darkaqua.blacksmith.api.common.util.Direction;

/**
 * Created by cout970 on 16/01/2016.
 */
public class KineticStorageHandler implements IInterfaceIdentifier.IStorageHandler {

    @Override
    public IDataCompound saveData(IInterfaceIdentifier identifier, Object instance, Direction dir) {
        return null;
    }

    @Override
    public void loadData(IInterfaceIdentifier identifier, Object instance, Direction dir, IDataCompound data) {}
}
