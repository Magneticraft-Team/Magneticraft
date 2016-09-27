package com.cout970.magneticraft.api.energy.item;

/**
 * Created by cout970 on 2016/09/20.
 */
public interface IEnergyStorageItem {

    /**
     * @return the current amount of Joules stored in this item
     */
    double getStoredEnergy();

    /**
     * @return the max amount of energy that this item can store
     */
    double getCapacity();
}
