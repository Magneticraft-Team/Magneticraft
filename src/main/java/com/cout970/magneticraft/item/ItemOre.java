package com.cout970.magneticraft.item;

import com.cout970.magneticraft.Magneticraft;
import com.cout970.magneticraft.util.MiscUtils;
import net.darkaqua.blacksmith.render.ITexture;
import net.darkaqua.blacksmith.render.TextureManager;
import net.darkaqua.blacksmith.render.model.IModelIdentifier;
import net.darkaqua.blacksmith.render.model.WrapperBakedModel;
import net.darkaqua.blacksmith.render.providers.IItemModelProvider;
import net.darkaqua.blacksmith.render.providers.factory.DefaultItemModelFactory;
import net.darkaqua.blacksmith.util.ResourceReference;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * Created by cout970 on 16/01/2016.
 */
public class ItemOre extends ItemBase {

    public static final String[] prefixes = {"ingot", "dust", "chunk"};
    private String base;

    public ItemOre(String base) {
        this.base = base;
        this.hasSubtypes = true;
    }

    public int maxMeta() {
        return prefixes.length;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        for (int i = 0; i < maxMeta(); i++) {
            subItems.add(new ItemStack(item, 1, i));
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
    public String getUnlocalizedName(ItemStack stack) {
        return getUnlocalizedNameForMeta(stack.getItemDamage());
    }

    @Override
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

        private IModelIdentifier[] models;
        private WrapperBakedModel wrapper;
        private ItemOre item;

        public ModelProvider(ItemOre item) {
            this.item = item;
            models = new IModelIdentifier[item.maxMeta()];
            wrapper = new WrapperBakedModel(getTransformation());
        }

        @Override
        public IBakedModel getModelForItemStack(ItemStack stack) {
            wrapper.setModel(models[stack.getItemDamage() % models.length].getBakedModel());
            return wrapper;
        }

        @Override
        public void reloadModels() {
            for (int i = 0; i < item.maxMeta(); i++) {
                ITexture texture = TextureManager.registerTexture(new ResourceReference(Magneticraft.ID, "items/" + item.getBase().toLowerCase() + "/" + prefixes[i]));
                models[i] =  new DefaultItemModelFactory(Collections.singletonList(texture)).createModels().values().stream().findFirst().get();
            }
        }
    }
}
