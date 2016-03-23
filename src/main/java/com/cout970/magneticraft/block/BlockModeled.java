package com.cout970.magneticraft.block;

import net.darkaqua.blacksmith.render.providers.IBlockModelProvider;
import net.darkaqua.blacksmith.render.providers.factory.IModelFactory;
import net.darkaqua.blacksmith.render.providers.block.ItemOnlyModelProvider;

/**
 * Created by cout970 on 03/01/2016.
 */
public abstract class BlockModeled extends BlockBase {

    @Override
    public int getLightOpacity() {
        return 0;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isBlockNormalCube() {
        return false;
    }

    public abstract IModelFactory getModelFactory();

    @Override
    public IBlockModelProvider getModelProvider() {
        IModelFactory factory = getModelFactory();
        return new ItemOnlyModelProvider(factory);
    }
}
