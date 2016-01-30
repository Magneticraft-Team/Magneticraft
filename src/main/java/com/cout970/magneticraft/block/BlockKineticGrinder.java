package com.cout970.magneticraft.block;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.kinetic.TileKineticGrinder;
import com.cout970.magneticraft.util.ItemOnlyModelProvider;
import net.darkaqua.blacksmith.api.block.IBlockContainerDefinition;
import net.darkaqua.blacksmith.api.block.blockdata.BlockDataFactory;
import net.darkaqua.blacksmith.api.block.blockdata.IBlockData;
import net.darkaqua.blacksmith.api.block.blockdata.IBlockDataGenerator;
import net.darkaqua.blacksmith.api.block.blockdata.defaults.BlockAttributeValueDirection;
import net.darkaqua.blacksmith.api.block.methods.BlockMethod;
import net.darkaqua.blacksmith.api.entity.ILivingEntity;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.render.model.providers.IBlockModelProvider;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.WorldRef;
import net.darkaqua.blacksmith.api.world.IWorld;

/**
 * Created by cout970 on 20/01/2016.
 */
public class BlockKineticGrinder extends BlockModeled implements IBlockContainerDefinition, BlockMethod.OnPlacedBy{

    public IBlockModelProvider getModelProvider() {
        return new ItemOnlyModelProvider(ModelConstants.KINETIC_GRINDER);
    }

    @Override
    public String getBlockName() {
        return "kinetic_grinder";
    }

    @Override
    public IBlockDataGenerator getBlockDataGenerator() {
        return BlockDataFactory.createBlockDataGenerator(parent, BlockAttributeValueDirection.HORIZONTAL_DIRECTION);
    }

    @Override
    public void onPlacedBy(WorldRef ref, IBlockData state, ILivingEntity placer, IItemStack stack) {
        state = state.setValue(BlockAttributeValueDirection.HORIZONTAL_DIRECTION, BlockAttributeValueDirection.fromDirection(placer.getEntityRotation().toHorizontalAxis()));
        ref.setBlockData(state);
    }

    @Override
    public ITileEntityDefinition createTileEntity(IWorld world, IBlockData state) {
        return new TileKineticGrinder();
    }

    @Override
    public IBlockData translateMetadataToVariant(int meta) {
        return parent.getDefaultBlockData().setValue(BlockAttributeValueDirection.HORIZONTAL_DIRECTION, BlockAttributeValueDirection.HORIZONTAL_VALUES[meta%4]);
    }

    @Override
    public int translateVariantToMetadata(IBlockData variant) {
        return ((Direction)variant.getValue(BlockAttributeValueDirection.HORIZONTAL_DIRECTION).getValue()).ordinal();
    }
}
