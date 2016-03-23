package com.cout970.magneticraft.block;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.kinetic.generators.TileHandCrank;
import com.google.common.collect.Lists;
import net.darkaqua.blacksmith.block.IStatefulBlock;
import net.darkaqua.blacksmith.modelloader.techne.TechneModelFactory;
import net.darkaqua.blacksmith.raytrace.Cube;
import net.darkaqua.blacksmith.render.providers.factory.IModelFactory;
import net.darkaqua.blacksmith.util.Direction;
import net.darkaqua.blacksmith.util.WorldRef;
import net.darkaqua.blacksmith.vectors.Vect3d;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by cout970 on 03/01/2016.
 */
public class BlockHandCrank extends BlockModeled implements ITileEntityProvider, IStatefulBlock {

    public BlockHandCrank() {
    }

    @Override
    public String getBlockName() {
        return "hand_crank";
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileHandCrank();
    }

    @Override
    public BlockState createBlockState() {
        return new BlockState(this, ATTRIBUTE_ALL_DIRECTIONS);
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
                                     int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(BlockProperties.ATTRIBUTE_ALL_DIRECTIONS, Direction.getDirection(facing).opposite());
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileHandCrank) {
            ((TileHandCrank) tile).resetCounter();
            return true;
        }
        return false;
    }

    @Override
    public Cube getSelectionCube(WorldRef ref) {
        return getBounds(ref);
    }

    @Override
    public List<Cube> getCollisionCubes(WorldRef ref, Entity entity) {
        return Lists.newArrayList(getBounds(ref));
    }

    @Override
    public Cube getBounds(IBlockAccess blockAccess, BlockPos pos, IBlockState state) {
        Direction dir = state.getValue(ATTRIBUTE_ALL_DIRECTIONS);
        double size = 2 / 16d;
        Vect3d min = new Vect3d(0.5 - size, 0.5 - size, 0.5 - size);
        Vect3d max = new Vect3d(0.5 + size, 0.5 + size, 0.5 + size);
        Cube base = new Cube(min, max);
        return base.extend(dir, 0.5 - size);
    }

    @Override
    public IModelFactory getModelFactory() {
        return new TechneModelFactory(ModelConstants.HAND_CRANK);
    }
}
