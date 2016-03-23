package com.cout970.magneticraft.api.tool;


import net.darkaqua.blacksmith.util.WorldRef;
import net.minecraft.item.ItemStack;

/**
 * Used on the hammer table to smash ores
 *
 * @author cout970
 */
public interface IHammer {

    /**
     * called to damage the tool, return the item to leave in the player's hands
     */
    ItemStack tick(ItemStack hammer, WorldRef ref);

    /**
     * Return true if the tool can work or not on the hammer table
     */
    boolean canHammer(ItemStack hammer, WorldRef ref);

    /**
     * Return the number of hits needed to break the ore
     */
    int getMaxHits(ItemStack hammer, WorldRef ref);
}
