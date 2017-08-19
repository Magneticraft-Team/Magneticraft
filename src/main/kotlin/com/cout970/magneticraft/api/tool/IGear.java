package com.cout970.magneticraft.api.tool;

import net.minecraft.item.ItemStack;

/**
 * Created by cout970 on 2017/08/19.
 */
public interface IGear {

    float getSpeedMultiplier();

    int getMaxDurability();

    int getDurability();

    ItemStack applyDamage();
}
