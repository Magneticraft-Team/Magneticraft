package com.cout970.magneticraft.item;

import net.darkaqua.blacksmith.util.WorldRef;
import net.minecraft.item.ItemStack;

/**
 * Created by cout970 on 21/12/2015.
 */
public class ItemIronHammer extends ItemAbstractHammer {

    public ItemIronHammer() {
        setMaxDamage(250);
    }

    @Override
    public String getItemName() {
        return "iron_hammer";
    }

    @Override
    public int getMaxHits(ItemStack hammer, WorldRef ref) {
        return 8;
    }
}
