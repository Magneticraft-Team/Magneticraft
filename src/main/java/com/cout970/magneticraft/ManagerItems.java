package com.cout970.magneticraft;

import com.cout970.magneticraft.item.*;
import com.cout970.magneticraft.util.MiscUtils;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.inventory.ItemStackFactory;
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
        ItemOres.initItems();
    }

    public static void initItemRenders() {
        for (ManagerItems b : ManagerItems.values()) {
            StaticAccess.GAME.getRenderRegistry().registerItemModelProvider(b.definition, b.definition.getModelProvider());
        }
        ItemOres.initItemRenders();
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

    public IItemStack toItemStack() {
        return ItemStackFactory.createItemStack(getItem());
    }

    public enum ItemOres {
        Copper("Copper", "Gold", "Iron"),
        Tungsten("Tungsten", "Gold", "Iron");

        private ItemOre definition;
        private IItem item;
        private String identifier;
        private String[] extras;

        ItemOres(String base, String... extras) {
            this.definition = new ItemOre(base);
            this.extras = extras;
            identifier = "itemOre" + base;
            for (int i = 0; i < definition.maxMeta(); i++) {
                LangHelper.addName("item." + definition.getUnlocalizedNameForMeta(i), base + " " + MiscUtils.capitalize(ItemOre.prefixes[i]));
            }
        }

        public static void initItems() {
            for (ItemOres b : ItemOres.values()) {
                b.item = StaticAccess.GAME.getItemRegistry().registerItemDefinition(b.definition, b.identifier);
                for (int i = 0; i < b.definition.maxMeta(); i++) {
                    StaticAccess.GAME.getOreDictionary().registerOre(b.definition.getOreDictName(i), ItemStackFactory.createItemStack(b.item, 1, i));
                }
            }
        }

        public static void initItemRenders() {
            for (ItemOres b : ItemOres.values()) {
                StaticAccess.GAME.getRenderRegistry().registerItemModelProvider(b.definition, b.definition.getModelProvider());
            }
        }

        public ItemOre getDefinition() {
            return definition;
        }

        public IItem getItem() {
            return item;
        }

        public String getIdentifier() {
            return identifier;
        }

        public String getExtra(int n) {
            if (n >= extras.length) return null;
            return extras[n];
        }

        public IItemStack getIngot() {
            return ItemStackFactory.createItemStack(item, 1, 0);
        }

        public IItemStack getDust() {
            return ItemStackFactory.createItemStack(item, 1, 1);
        }
    }
}
