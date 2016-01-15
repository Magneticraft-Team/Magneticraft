package com.cout970.magneticraft.block;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.TileTableSieve;
import net.darkaqua.blacksmith.api.block.IBlockContainerDefinition;
import net.darkaqua.blacksmith.api.block.blockdata.IBlockData;
import net.darkaqua.blacksmith.api.render.model.IBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.defaults.SimpleBlockModelProvider;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.world.IWorld;

/**
 * Created by cout970 on 28/12/2015.
 */
public class BlockTableSieve extends BlockModeled implements IBlockContainerDefinition {

    @Override
    public String getBlockName() {
        return "table_sieve";
    }

    public IBlockModelProvider getModelProvider() {
        return new SimpleBlockModelProvider(ModelConstants.ofTechne(ModelConstants.SIEVE_TABLE));
    }

    @Override
    public ITileEntityDefinition createTileEntity(IWorld world, IBlockData state) {
        return new TileTableSieve();
    }
}
