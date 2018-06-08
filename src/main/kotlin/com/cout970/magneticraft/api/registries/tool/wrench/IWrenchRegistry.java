package com.cout970.magneticraft.api.registries.tool.wrench;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IWrenchRegistry {

    boolean isWrench(@NotNull ItemStack stack);

    boolean registerWrench(@NotNull ItemStack stack);

    boolean removeWrench(@NotNull ItemStack stack);
}
