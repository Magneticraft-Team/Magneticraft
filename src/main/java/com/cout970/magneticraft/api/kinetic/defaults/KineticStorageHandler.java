package com.cout970.magneticraft.api.kinetic.defaults;

import net.darkaqua.blacksmith.api.intermod.IInterfaceIdentifier;
import net.darkaqua.blacksmith.api.storage.IDataCompound;
import net.darkaqua.blacksmith.api.util.Direction;

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
