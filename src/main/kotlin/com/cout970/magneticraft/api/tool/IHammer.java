package com.cout970.magneticraft.api.tool;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created by cout970 on 2017/06/12.
 */
public interface IHammer {

    int getMiningLevel();

    int getBreakingSpeed();

    ItemStack applyDamage(ItemStack item, EntityPlayer player);
}
