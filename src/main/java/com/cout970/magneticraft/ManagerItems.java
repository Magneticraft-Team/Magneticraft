package com.cout970.magneticraft;

import com.cout970.magneticraft.item.*;
import com.cout970.magneticraft.util.MiscUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Created by cout970 on 21/12/2015.
 */
public enum ManagerItems {

    StoneHammer(new ItemStoneHammer(), "Stone Hammer"),
    IronHammer(new ItemIronHammer(), "Iron Hammer"),
    HandSieve(new ItemHandSieve(), "Hand Sieve");

    private ItemBase item;
    private String identifier;

    ManagerItems(ItemBase definition, String englishName) {
        this.item = definition;
        identifier = definition.getUnlocalizedName();
        Magneticraft.LANG.addName("item." + definition.getUnlocalizedName(), englishName);
    }

    public static void initItems() {
        for (ManagerItems b : ManagerItems.values()) {
            GameRegistry.registerItem(b.item, b.identifier);
        }
        ItemOres.initItems();
    }

    public static void initItemRenders() {
        for (ManagerItems b : ManagerItems.values()) {
            ManagerRender.INSTANCE.registerItemModelProvider(b.item, b.item.getModelProvider());
        }
        ItemOres.initItemRenders();
    }


    public Item getItem() {
        return item;
    }

    public String getIdentifier() {
        return identifier;
    }

    public ItemStack toItemStack() {
        return new ItemStack(getItem());
    }

    public enum ItemOres {
        Copper("Copper", "Gold", "Iron"),
        Tungsten("Tungsten", "Gold", "Iron");

        private String base;
        private ItemOre item;
        private String identifier;
        private String[] extras;

        ItemOres(String base, String... extras) {
            this.base = base;
            this.item = new ItemOre(base);
            this.extras = extras;
            identifier = "itemOre" + base;
            for (int i = 0; i < item.maxMeta(); i++) {
                Magneticraft.LANG.addName("item." + item.getUnlocalizedNameForMeta(i), base + " " + MiscUtils.capitalize(ItemOre.prefixes[i]));
            }
        }

        public static void initItems() {
            for (ItemOres b : ItemOres.values()) {
                GameRegistry.registerItem(b.item, b.identifier);
                for (int i = 0; i < b.item.maxMeta(); i++) {
                    OreDictionary.registerOre(b.item.getOreDictName(i), b.item);
                }
            }
        }

        public static void initItemRenders() {
            for (ItemOres b : ItemOres.values()) {
                ManagerRender.INSTANCE.registerItemModelProvider(b.item, b.item.getModelProvider());
            }
        }

        public ItemOre getItem() {
            return item;
        }

        public String getIdentifier() {
            return identifier;
        }

        public String getExtra(int n) {
            if (n >= extras.length) return null;
            return extras[n];
        }

        public ItemStack getIngot() {
            return new ItemStack(item, 1, 0);
        }

        public ItemStack getDust() {
            return new ItemStack(item, 1, 1);
        }

        public ItemStack getChunk() {
            return new ItemStack(item, 1, 2);
        }

        public ItemStack getOreBlock() {
            return ManagerOreDict.getOreWithPriority("ore"+base);
        }
    }
}
