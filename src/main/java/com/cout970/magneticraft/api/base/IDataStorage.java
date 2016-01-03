package com.cout970.magneticraft.api.base;

import net.darkaqua.blacksmith.api.storage.IDataCompound;

/**
 * Created by cout970 on 29/12/2015.
 */
public interface IDataStorage {

    void load(IDataCompound data);

    void save(IDataCompound data);
}
