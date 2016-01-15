package com.cout970.magneticraft.block;

import com.cout970.magneticraft.tileentity.TileHandCrank;
import net.darkaqua.blacksmith.api.block.IBlockContainerDefinition;
import net.darkaqua.blacksmith.api.block.blockdata.IBlockData;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.world.IWorld;

/**
 * Created by cout970 on 03/01/2016.
 */
public class BlockHandCrank extends BlockModeled implements IBlockContainerDefinition {

    @Override
    public String getBlockName() {
        return "hand_crank";
    }

    @Override
    public ITileEntityDefinition createTileEntity(IWorld world, IBlockData state) {
        return new TileHandCrank();
    }
}
