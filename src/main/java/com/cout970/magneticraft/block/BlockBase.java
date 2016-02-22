package com.cout970.magneticraft.block;

import com.cout970.magneticraft.LangHelper;
import com.cout970.magneticraft.Magneticraft;
import net.darkaqua.blacksmith.api.common.block.defaults.DefaultBlockDefinition;
import net.darkaqua.blacksmith.api.client.creativetab.ICreativeTab;
import net.darkaqua.blacksmith.api.common.entity.IEntity;
import net.darkaqua.blacksmith.api.client.render.model.defaults.SimpleModelPartBlock;
import net.darkaqua.blacksmith.api.client.render.block.IBlockModelProvider;
import net.darkaqua.blacksmith.api.client.render.block.defaults.SimpleBlockModelProvider;
import net.darkaqua.blacksmith.api.common.util.raytrace.Cube;
import net.darkaqua.blacksmith.api.common.util.ResourceReference;
import net.darkaqua.blacksmith.api.common.util.WorldRef;

import java.util.Collections;
import java.util.List;

/**
 * Created by cout970 on 16/12/2015.
 */
public abstract class BlockBase extends DefaultBlockDefinition {

    public static ICreativeTab MAIN_CREATIVE_TAB;

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
        SimpleModelPartBlock model = new SimpleModelPartBlock(
                new ResourceReference(Magneticraft.ID, "blocks/" + getBlockName().toLowerCase()));
        return new SimpleBlockModelProvider(iModelRegistry -> new SimpleBlockModelProvider.BlockModel(
                iModelRegistry.registerModelPart(Magneticraft.IDENTIFIER, model)));
    }

    public void registerName(String name) {
        LangHelper.addName("tile." + getUnlocalizedName(), name);
    }
}
