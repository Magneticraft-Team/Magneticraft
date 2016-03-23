package com.cout970.magneticraft.block;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.kinetic.TileKineticGrinder;
import net.darkaqua.blacksmith.block.IStatefulBlock;
import net.darkaqua.blacksmith.modelloader.techne.TechneModelFactory;
import net.darkaqua.blacksmith.raytrace.Cube;
import net.darkaqua.blacksmith.render.providers.factory.IModelFactory;
import net.darkaqua.blacksmith.util.Direction;
import net.darkaqua.blacksmith.util.EntityRotation;
import net.darkaqua.blacksmith.vectors.Vect3i;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by cout970 on 20/01/2016.
 */
public class BlockKineticGrinder extends BlockModeled implements ITileEntityProvider, IStatefulBlock {

    @Override
    public String getBlockName() {
        return "kinetic_grinder";
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, ATTRIBUTE_HORIZONTAL_DIRECTIONS);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        IBlockState newstate = state.withProperty(ATTRIBUTE_HORIZONTAL_DIRECTIONS, new EntityRotation(placer).toHorizontalAxis());
        worldIn.setBlockState(pos, newstate);
    }

    @Override
    public Cube getBounds(IBlockAccess access, BlockPos pos, IBlockState state) {
        Direction dir = state.getValue(ATTRIBUTE_HORIZONTAL_DIRECTIONS);
        Direction left = dir.rotate(Direction.Axis.Y, true);
        Vect3i side = left.toVect3i().abs();
        double w = 1d / 16d;
        return new Cube(
                side.getX() == 0 ? 0 : w,
                side.getY() == 0 ? 0 : w,
                side.getZ() == 0 ? 0 : w,
                side.getX() == 0 ? 1 : 1 - w,
                side.getY() == 0 ? 1 : 1 - w,
                side.getZ() == 0 ? 1 : 1 - w);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileKineticGrinder();
    }

    @Override
    public IModelFactory getModelFactory() {
        return new TechneModelFactory(ModelConstants.KINETIC_GRINDER);
    }
}
