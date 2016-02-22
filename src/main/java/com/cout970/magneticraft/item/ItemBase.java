package com.cout970.magneticraft.item;

import com.cout970.magneticraft.Magneticraft;
import com.cout970.magneticraft.block.BlockBase;
import net.darkaqua.blacksmith.api.client.creativetab.ICreativeTab;
import net.darkaqua.blacksmith.api.common.item.defaults.DefaultItemDefinition;
import net.darkaqua.blacksmith.api.client.render.item.IItemModelProvider;
import net.darkaqua.blacksmith.api.client.render.item.defaults.PlaneItemModelProvider;
import net.darkaqua.blacksmith.api.common.util.ResourceReference;

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
        return new PlaneItemModelProvider(Magneticraft.IDENTIFIER, new ResourceReference(Magneticraft.ID, "items/" + getItemName().toLowerCase()));
    }
}
