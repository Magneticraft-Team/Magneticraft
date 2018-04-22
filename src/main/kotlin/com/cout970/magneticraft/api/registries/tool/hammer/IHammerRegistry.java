package com.cout970.magneticraft.api.registries.tool.hammer;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IHammerRegistry {

    @Nullable
    IHammer findHammer(@NotNull ItemStack stack);

    boolean registerHammer(@NotNull ItemStack stack, @NotNull IHammer hammer);

    boolean removeHammer(@NotNull ItemStack stack);

    IHammer createHammer(int level, int speed, int cost);
}
