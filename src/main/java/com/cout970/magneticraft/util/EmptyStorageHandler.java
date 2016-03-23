package com.cout970.magneticraft.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

/**
 * Created by cout970 on 25/01/2016.
 */
public class EmptyStorageHandler<T> implements Capability.IStorage<T> {

    @Override
    public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
        return new NBTTagCompound();
    }

    @Override
    public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {

    }
}
