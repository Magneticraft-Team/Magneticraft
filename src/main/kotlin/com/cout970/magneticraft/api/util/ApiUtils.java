package com.cout970.magneticraft.api.util;

import net.minecraft.item.ItemStack;

/**
 * Created by cout970 on 16/06/2016.
 */
public class ApiUtils {

    private ApiUtils() {
    }

    public static boolean equalsIgnoreSize(ItemStack a, ItemStack b) {
        return a == b || !(a == null || b == null)
                         && (a.getItem().equals(b.getItem()))
                         && (!a.getItem().getHasSubtypes()
                             || (a.getMetadata() == b.getMetadata()))
                         && equals(a.getTagCompound(), b.getTagCompound());
    }

    public static boolean equals(Object a, Object b) {
        if (a == null) return b == null;
        return a.equals(b);
    }
}
