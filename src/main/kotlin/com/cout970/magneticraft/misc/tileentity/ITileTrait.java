package com.cout970.magneticraft.misc.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by cout970 on 2017/02/21.
 */
public interface ITileTrait extends ICapabilityProvider {

    BlockPos getPos();

    World getWorld();

    void update();

    void deserialize(@NotNull NBTTagCompound nbt);

    @Nullable
    NBTTagCompound serialize();

    default void onLoad() {
    }

    default void onBreak() {
    }

    @Override
    default boolean hasCapability(@NotNull Capability<?> capability, @javax.annotation.Nullable EnumFacing enumFacing) {
        return false;
    }

    @Override
    default <T> T getCapability(@NotNull Capability<T> capability, @javax.annotation.Nullable EnumFacing enumFacing) {
        return null;
    }
}
