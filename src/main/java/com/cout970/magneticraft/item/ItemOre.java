package com.cout970.magneticraft.item;

import com.cout970.magneticraft.Magneticraft;
import com.cout970.magneticraft.util.MiscUtils;
import net.darkaqua.blacksmith.api.creativetab.ICreativeTab;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.inventory.ItemStackFactory;
import net.darkaqua.blacksmith.api.item.IItem;
import net.darkaqua.blacksmith.api.registry.IModelRegistry;
import net.darkaqua.blacksmith.api.render.model.IItemModelProvider;
import net.darkaqua.blacksmith.api.render.model.IRenderModel;
import net.darkaqua.blacksmith.api.render.model.defaults.ItemFlatModelProvider;
import net.darkaqua.blacksmith.api.util.ResourceReference;

import java.util.List;

/**
 * Created by cout970 on 16/01/2016.
 */
public class ItemOre extends ItemBase {

    public static final String[] prefixes = {"ingot", "dust", "chunk"};
    private String base;

    public ItemOre(String base) {
        this.base = base;
    }

    public int maxMeta() {
        return prefixes.length;
    }

    @Override
    public void getSubItems(IItem item, ICreativeTab tab, List<IItemStack> subItems) {
        for (int i = 0; i < maxMeta(); i++) {
            subItems.add(ItemStackFactory.createItemStack(parent, 1, i));
        }
    }

    public String getOreDictName(int i){
        return prefixes[i % prefixes.length] + MiscUtils.capitalize(base);
    }

    @Override
    public String getItemName() {
        return base;
    }

    @Override
    public String getUnlocalizedName() {
        return "itemOre_"+base;
    }

    @Override
    public String getUnlocalizedName(IItemStack stack) {
        return getUnlocalizedNameForMeta(stack.getDamage());
    }

    public IItemModelProvider getModelProvider() {
        return new ModelProvider(this);
    }

    public String getBase() {
        return base;
    }

    public String getUnlocalizedNameForMeta(int i) {
        return base + "." + prefixes[i % prefixes.length];
    }

    private class ModelProvider implements IItemModelProvider {

        private IRenderModel[] models;
        private ItemOre item;

        public ModelProvider(ItemOre item) {
            this.models = new IRenderModel[item.maxMeta()];
            this.item = item;
        }

        @Override
        public IRenderModel getModelForVariant(IItemStack stack) {
            return models[stack.getDamage() % models.length];
        }

        @Override
        public void registerModels(IModelRegistry registry) {
            for (int i = 0; i < item.maxMeta(); i++) {
                models[i] = new ItemFlatModelProvider.ItemFlatModel(
                        registry.registerFlatItemModel(
                                new ResourceReference(Magneticraft.ID,
                                        "items/" + item.getBase() + "/" + prefixes[i])));
            }
        }
    }
}
