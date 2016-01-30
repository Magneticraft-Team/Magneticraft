package com.cout970.magneticraft.block;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.kinetic.generators.TileHandCrank;
import com.google.common.collect.Lists;
import net.darkaqua.blacksmith.api.block.IBlockContainerDefinition;
import net.darkaqua.blacksmith.api.block.blockdata.BlockDataFactory;
import net.darkaqua.blacksmith.api.block.blockdata.IBlockData;
import net.darkaqua.blacksmith.api.block.blockdata.IBlockDataGenerator;
import net.darkaqua.blacksmith.api.block.blockdata.defaults.BlockAttributeValueDirection;
import net.darkaqua.blacksmith.api.block.methods.BlockMethod;
import net.darkaqua.blacksmith.api.entity.IEntity;
import net.darkaqua.blacksmith.api.entity.ILivingEntity;
import net.darkaqua.blacksmith.api.entity.IPlayer;
import net.darkaqua.blacksmith.api.render.model.providers.IBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.providers.defaults.SimpleItemBlockModelProvider;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.util.Cube;
import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.Vect3d;
import net.darkaqua.blacksmith.api.util.WorldRef;
import net.darkaqua.blacksmith.api.world.IWorld;

import java.util.List;

/**
 * Created by cout970 on 03/01/2016.
 */
public class BlockHandCrank extends BlockModeled implements IBlockContainerDefinition, BlockMethod.OnPlaced, BlockMethod.OnActivated {

    @Override
    public String getBlockName() {
        return "hand_crank";
    }

    @Override
    public ITileEntityDefinition createTileEntity(IWorld world, IBlockData state) {
        return new TileHandCrank();
    }

    public IBlockModelProvider getModelProvider() {
        return new SimpleItemBlockModelProvider(ModelConstants.HAND_CRANK);
    }

    @Override
    public IBlockDataGenerator getBlockDataGenerator() {
        return BlockDataFactory.createBlockDataGenerator(parent, BlockAttributeValueDirection.DIRECTION);
    }

    @Override
    public IBlockData translateMetadataToVariant(int meta) {
        return parent.getDefaultBlockData().setValue(BlockAttributeValueDirection.DIRECTION, BlockAttributeValueDirection.VALUES[meta%6]);
    }

    @Override
    public int translateVariantToMetadata(IBlockData variant) {
        return ((Direction)variant.getValue(BlockAttributeValueDirection.DIRECTION).getValue()).ordinal();
    }

    @Override
    public IBlockData onPlaced(WorldRef ref, Direction side, ILivingEntity entity, Vect3d hit, int metadata) {
        IBlockData state = parent.getDefaultBlockData();
        state = state.setValue(BlockAttributeValueDirection.DIRECTION, BlockAttributeValueDirection.fromDirection(side.opposite()));
        return state;
    }

    @Override
    public boolean onActivated(WorldRef ref, IBlockData state, IPlayer player, Direction side, Vect3d vector3d) {
        ITileEntity tile = ref.getTileEntity();
        if(tile.getTileEntityDefinition() instanceof TileHandCrank){
            ((TileHandCrank) tile.getTileEntityDefinition()).resetCounter();
            return true;
        }
        return false;
    }

    public Cube getSelectionCube(WorldRef ref){
        return getBounds(ref);
    }

    public List<Cube> getCollisionCubes(WorldRef ref, IEntity entity) {
        return Lists.newArrayList(getBounds(ref));
    }

    public Cube getBounds(WorldRef ref){
        ITileEntity tile = ref.getTileEntity();
        if(tile.getTileEntityDefinition() instanceof TileHandCrank){
            Direction dir = ((TileHandCrank) tile.getTileEntityDefinition()).getDirection();
            double size = 2/16d;
            Vect3d min = new Vect3d(0.5-size, 0.5-size, 0.5-size);
            Vect3d max = new Vect3d(0.5+size, 0.5+size, 0.5+size);
            Cube base = new Cube(min, max);

            return base.extend(dir, 0.5-size);
        }
        return Cube.fullBlock();
    }
}
