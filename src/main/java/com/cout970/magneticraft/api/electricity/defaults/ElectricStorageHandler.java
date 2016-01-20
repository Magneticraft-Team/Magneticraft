package com.cout970.magneticraft.api.electricity.defaults;

import net.darkaqua.blacksmith.api.intermod.IInterfaceIdentifier;
import net.darkaqua.blacksmith.api.storage.IDataCompound;
import net.darkaqua.blacksmith.api.util.Direction;

/**
 * Created by cypheraj on 1/18/16.
 */
public class ElectricStorageHandler implements IInterfaceIdentifier.IStorageHandler {
    @Override
    public IDataCompound saveData(IInterfaceIdentifier iInterfaceIdentifier, Object o, Direction direction) {
        return null;
    }

    @Override
    public void loadData(IInterfaceIdentifier iInterfaceIdentifier, Object o, Direction direction, IDataCompound iDataCompound) {

    }
}
