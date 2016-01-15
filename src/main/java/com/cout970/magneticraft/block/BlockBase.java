package com.cout970.magneticraft.block;

import com.cout970.magneticraft.Magneticraft;
import net.darkaqua.blacksmith.api.block.Blocks;
import net.darkaqua.blacksmith.api.block.defaults.DefaultBlockDefinition;
import net.darkaqua.blacksmith.api.creativetab.CreativeTabFactory;
import net.darkaqua.blacksmith.api.creativetab.ICreativeTab;
import net.darkaqua.blacksmith.api.render.model.IBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.defaults.SimpleBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.defaults.SimpleModelPartBlock;
import net.darkaqua.blacksmith.api.util.ResourceReference;

/**
 * Created by cout970 on 16/12/2015.
 */
public abstract class BlockBase extends DefaultBlockDefinition {

    public static ICreativeTab MAIN_CREATIVE_TAB = CreativeTabFactory.createCreativeTab(Magneticraft.NAME + " Main", Blocks.IRON_BLOCK.newItemStack(1));

    public abstract String getBlockName();

    @Override
    public String getUnlocalizedName() {
        return getBlockName();
    }

    @Override
    public ICreativeTab getCreativeTab() {
        return MAIN_CREATIVE_TAB;
    }

    public IBlockModelProvider getModelProvider() {
        SimpleModelPartBlock model = new SimpleModelPartBlock(new ResourceReference(Magneticraft.ID, "blocks/" + getBlockName().toLowerCase()));
        return new SimpleBlockModelProvider(model);
    }
}
