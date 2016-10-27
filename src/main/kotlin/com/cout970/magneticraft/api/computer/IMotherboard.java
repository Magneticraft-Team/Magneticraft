package com.cout970.magneticraft.api.computer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

/**
 * Created by cout970 on 2016/09/30.
 */
public interface IMotherboard extends INBTSerializable<NBTTagCompound> {

    IBus getBus();

    ICPU getCPU();

    IROM getROM();

    IMemory getMemory();

    List<IDevice> getDevices();

    int getClock();

    void start();

    void reset();

    void halt();
}
