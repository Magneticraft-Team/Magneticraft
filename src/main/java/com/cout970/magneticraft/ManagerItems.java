package com.cout970.magneticraft;

import com.cout970.magneticraft.item.ItemBase;
import com.cout970.magneticraft.item.ItemHandSieve;
import com.cout970.magneticraft.item.ItemIronHammer;
import com.cout970.magneticraft.item.ItemStoneHammer;
import net.darkaqua.blacksmith.api.item.IItem;
import net.darkaqua.blacksmith.api.registry.StaticAccess;

/**
 * Created by cout970 on 21/12/2015.
 */
public enum ManagerItems {

    StoneHammer(new ItemStoneHammer(), "Stone Hammer"),
    IronHammer(new ItemIronHammer(), "Iron Hammer"),
    HandSieve(new ItemHandSieve(), "Hand Sieve");

    private ItemBase definition;
    private IItem item;
    private String identifier;

    ManagerItems(ItemBase definition, String englishName) {
        this.definition = definition;
        identifier = definition.getUnlocalizedName();
        LangHelper.addName("item." + definition.getUnlocalizedName(), englishName);
    }

    public static void initItems() {
        for (ManagerItems b : ManagerItems.values()) {
            b.item = StaticAccess.GAME.getItemRegistry().registerItemDefinition(b.definition, b.identifier);
        }
    }

    public static void initBlockRenders() {
        for (ManagerItems b : ManagerItems.values()) {
            StaticAccess.GAME.getRenderRegistry().registerItemModelProvider(b.definition, b.definition.getModelProvider());
        }
    }

    public ItemBase getDefinition() {
        return definition;
    }

    public IItem getItem() {
        return item;
    }

    public String getIdentifier() {
        return identifier;
    }
}
