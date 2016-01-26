package com.cout970.magneticraft.util;

import net.darkaqua.blacksmith.api.intermod.IInterfaceIdentifier;
import net.darkaqua.blacksmith.api.storage.IDataCompound;
import net.darkaqua.blacksmith.api.util.Direction;

/**
 * Created by cout970 on 25/01/2016.
 */
public class EmptyStorageHandler implements IInterfaceIdentifier.IStorageHandler {

    @Override
    public IDataCompound saveData(IInterfaceIdentifier identifier, Object instance, Direction dir) {
        return null;
    }

    @Override
    public void loadData(IInterfaceIdentifier identifier, Object instance, Direction dir, IDataCompound data) {

    }
}
