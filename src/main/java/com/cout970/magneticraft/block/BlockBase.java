package com.cout970.magneticraft.block;

import com.cout970.magneticraft.LangHelper;
import com.cout970.magneticraft.Magneticraft;
import net.darkaqua.blacksmith.api.block.Blocks;
import net.darkaqua.blacksmith.api.block.defaults.DefaultBlockDefinition;
import net.darkaqua.blacksmith.api.creativetab.CreativeTabFactory;
import net.darkaqua.blacksmith.api.creativetab.ICreativeTab;
import net.darkaqua.blacksmith.api.entity.IEntity;
import net.darkaqua.blacksmith.api.render.model.IBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.defaults.SimpleBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.defaults.SimpleModelPartBlock;
import net.darkaqua.blacksmith.api.util.Cube;
import net.darkaqua.blacksmith.api.util.ResourceReference;
import net.darkaqua.blacksmith.api.util.WorldRef;

import java.util.Collections;
import java.util.List;

/**
 * Created by cout970 on 16/12/2015.
 */
public abstract class BlockBase extends DefaultBlockDefinition {

    public static ICreativeTab MAIN_CREATIVE_TAB = CreativeTabFactory.createCreativeTab(Magneticraft.ID+"_main", Blocks.IRON_BLOCK.newItemStack(1));

    public abstract String getBlockName();

    public Cube getSelectionCube(WorldRef ref){
        return getBounds(ref);
    }

    public List<Cube> getCollisionCubes(WorldRef ref, IEntity entity) {
        return Collections.singletonList(getBounds(ref));
    }

    public Cube getBounds(WorldRef ref){
        return Cube.fullBlock();
    }

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

    public void registerName(String name) {
        LangHelper.addName("tile." + getUnlocalizedName(), name);
    }
}
