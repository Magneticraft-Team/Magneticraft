package com.cout970.magneticraft.item;

import com.cout970.magneticraft.Magneticraft;
import com.cout970.magneticraft.block.BlockBase;
import net.darkaqua.blacksmith.api.creativetab.ICreativeTab;
import net.darkaqua.blacksmith.api.item.defaults.DefaultItemDefinition;
import net.darkaqua.blacksmith.api.render.model.IItemModelProvider;
import net.darkaqua.blacksmith.api.render.model.defaults.ItemFlatModelProvider;
import net.darkaqua.blacksmith.api.util.ResourceReference;

/**
 * Created by cout970 on 21/12/2015.
 */
public abstract class ItemBase extends DefaultItemDefinition {

    public ItemBase() {
        super(null);
        name = getItemName();
    }

    @Override
    public ICreativeTab getCreativeTab() {
        return BlockBase.MAIN_CREATIVE_TAB;
    }

    public abstract String getItemName();

    public IItemModelProvider getModelProvider() {
        return new ItemFlatModelProvider(new ResourceReference(Magneticraft.ID, "items/" + getItemName().toLowerCase()));
    }
}
