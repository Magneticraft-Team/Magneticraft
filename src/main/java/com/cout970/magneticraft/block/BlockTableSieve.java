package com.cout970.magneticraft.block;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.TileTableSieve;
import net.darkaqua.blacksmith.modelloader.techne.TechneModelFactory;
import net.darkaqua.blacksmith.render.providers.IBlockModelProvider;
import net.darkaqua.blacksmith.render.providers.factory.IModelFactory;
import net.darkaqua.blacksmith.render.providers.block.UniqueModelProvider;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by cout970 on 28/12/2015.
 */
public class BlockTableSieve extends BlockModeled implements ITileEntityProvider {

    @Override
    public String getBlockName() {
        return "table_sieve";
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileTableSieve();
    }

    @Override
    public IModelFactory getModelFactory() {
        return new TechneModelFactory(ModelConstants.SIEVE_TABLE);
    }

    @Override
    public IBlockModelProvider getModelProvider() {
        IModelFactory factory = getModelFactory();
        return new UniqueModelProvider(factory);
    }
}
