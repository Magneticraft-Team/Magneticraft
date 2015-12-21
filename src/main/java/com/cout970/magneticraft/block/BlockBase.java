package com.cout970.magneticraft.block;

import com.cout970.magneticraft.Magneticraft;
import net.darkaqua.blacksmith.api.block.Blocks;
import net.darkaqua.blacksmith.api.block.IBlock;
import net.darkaqua.blacksmith.api.block.IBlockDefinition;
import net.darkaqua.blacksmith.api.creativetab.CreativeTabFactory;
import net.darkaqua.blacksmith.api.creativetab.ICreativeTab;
import net.darkaqua.blacksmith.api.render.model.IBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.defaults.SimpleBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.defaults.SimpleModelPartCube;
import net.darkaqua.blacksmith.api.render.model.defaults.SimpleRenderModel;
import net.darkaqua.blacksmith.api.util.Cube;
import net.darkaqua.blacksmith.api.util.ResourceReference;

/**
 * Created by cout970 on 16/12/2015.
 */
public abstract class BlockBase implements IBlockDefinition {

    public static ICreativeTab MAIN_CREATIVE_TAB = CreativeTabFactory.createCreatibeTab(Magneticraft.NAME+" Main", Blocks.IRON_BLOCK.newItemStack(1));
    protected IBlock parent;

    public abstract String getBlockName();

    @Override
    public void onCreate(IBlock block) {
        parent = block;
    }

    @Override
    public String getUnlocalizedName() {
        return getBlockName();
    }

    @Override
    public Cube getBounds() {
        return Cube.fullBlock();
    }

    @Override
    public float getHardness() {
        return 1.5f;
    }

    @Override
    public float getLightEmitted() {
        return 0;
    }

    @Override
    public float getLightOpacity() {
        return 1f;
    }

    @Override
    public float getResistance() {
        return 10f;
    }

    @Override
    public ICreativeTab getCreativeTab() {
        return MAIN_CREATIVE_TAB;
    }

    @Override
    public boolean shouldRender() {
        return true;
    }

    @Override
    public boolean isFullCube() {
        return true;
    }

    public IBlockModelProvider getModelProvider(){
        SimpleRenderModel model = new SimpleRenderModel();
        model.addModelPart(new SimpleModelPartCube(new ResourceReference(Magneticraft.ID, "blocks/"+getBlockName().toLowerCase())));
        return new SimpleBlockModelProvider(model);
    }
}
