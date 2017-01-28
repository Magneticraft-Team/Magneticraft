package com.cout970.magneticraft.api.computer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by cout970 on 2016/09/30.
 */
public interface IMemory extends IRW, INBTSerializable<NBTTagCompound> {

    boolean isLittleEndian();

    int getMemorySize();
}
