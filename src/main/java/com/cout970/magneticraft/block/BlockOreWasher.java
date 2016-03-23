package com.cout970.magneticraft.block;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.kinetic.TileOreWasher;
import net.darkaqua.blacksmith.modelloader.techne.TechneModelFactory;
import net.darkaqua.blacksmith.raytrace.Cube;
import net.darkaqua.blacksmith.render.providers.factory.IModelFactory;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by cout970 on 05/03/2016.
 */
public class BlockOreWasher extends BlockModeled implements ITileEntityProvider {

    @Override
    public String getBlockName() {
        return "ore_washer";
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileOreWasher();
    }

    @Override
    public IModelFactory getModelFactory() {
        return new TechneModelFactory(ModelConstants.ORE_WASHER);
    }

    @Override
    public Cube getBounds(IBlockAccess worldAccess, BlockPos pos, IBlockState state){
        float dif = 0.0625f * 1.5f;
        return new Cube(dif, 0, dif, 1-dif, 1, 1-dif);
    }
}
