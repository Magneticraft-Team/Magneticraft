package com.cout970.magneticraft.block;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.TileWoodenShaft;
import net.darkaqua.blacksmith.api.block.IBlockContainerDefinition;
import net.darkaqua.blacksmith.api.block.variants.IBlockData;
import net.darkaqua.blacksmith.api.render.model.IBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.defaults.SimpleItemBlockModelProvider;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.util.Cube;
import net.darkaqua.blacksmith.api.world.IWorld;

/**
 * Created by cout970 on 28/12/2015.
 */
public class BlockWoodenShaft extends BlockModeled implements IBlockContainerDefinition {

    @Override
    public String getBlockName() {
        return "wooden_shaft";
    }

    public IBlockModelProvider getModelProvider(){
        return new SimpleItemBlockModelProvider(ModelConstants.ofTechne(ModelConstants.WOODEN_SHAFT));

    }

    @Override
    public Cube getBounds() {
        double w = 4 / 16d;
        return new Cube(0.5 - w, 0.5 - w, 0.5 - w, 0.5 + w, 0.5 + w, 0.5 + w);
    }

    @Override
    public ITileEntityDefinition createTileEntity(IWorld world, IBlockData state) {
        return new TileWoodenShaft();
    }
}
