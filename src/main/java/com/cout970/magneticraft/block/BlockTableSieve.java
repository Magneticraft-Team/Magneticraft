package com.cout970.magneticraft.block;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.TileTableSieve;
import net.darkaqua.blacksmith.api.block.IBlockContainerDefinition;
import net.darkaqua.blacksmith.api.block.blockdata.IBlockData;
import net.darkaqua.blacksmith.api.block.methods.BlockMethod;
import net.darkaqua.blacksmith.api.entity.IPlayer;
import net.darkaqua.blacksmith.api.render.model.providers.IBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.providers.defaults.SimpleBlockModelProvider;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.Vect3d;
import net.darkaqua.blacksmith.api.util.WorldRef;
import net.darkaqua.blacksmith.api.world.IWorld;

/**
 * Created by cout970 on 28/12/2015.
 */
public class BlockTableSieve extends BlockModeled implements IBlockContainerDefinition, BlockMethod.OnActivated {

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

    @Override
    public boolean onActivated(WorldRef ref, IBlockData state, IPlayer player, Direction side, Vect3d vector3d) {
        ITileEntity tile = ref.getTileEntity();
        if(tile.getTileEntityDefinition() instanceof TileTableSieve){
            ((TileTableSieve) tile.getTileEntityDefinition()).resetTimer();
            return true;
        }
        return false;
    }
}
