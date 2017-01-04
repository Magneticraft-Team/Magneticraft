package com.cout970.magneticraft.api.computer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by cout970 on 2016/09/30.
 */
public interface ICPU extends INBTSerializable<NBTTagCompound> {

    void reset();

    void setMotherboard(IMotherboard mb);

    void interrupt(IInterruption i);

    void iterate();

    interface IInterruption {

        int getCode();

        String getName();

        String getDescription();
    }
}
