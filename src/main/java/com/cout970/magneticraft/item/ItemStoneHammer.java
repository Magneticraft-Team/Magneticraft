package com.cout970.magneticraft.item;

import net.darkaqua.blacksmith.util.WorldRef;
import net.minecraft.item.ItemStack;

/**
 * Created by cout970 on 21/12/2015.
 */
public class ItemStoneHammer extends ItemAbstractHammer {

    public ItemStoneHammer() {
        setMaxDamage(131);
    }

    @Override
    public String getItemName() {
        return "stone_hammer";
    }

    @Override
    public int getMaxHits(ItemStack hammer, WorldRef ref) {
        return 10;
    }

}
