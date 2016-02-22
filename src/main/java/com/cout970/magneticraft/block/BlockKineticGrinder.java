package com.cout970.magneticraft.block;

import com.cout970.magneticraft.Magneticraft;
import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.kinetic.TileKineticGrinder;
import net.darkaqua.blacksmith.api.common.block.IBlockContainerDefinition;
import net.darkaqua.blacksmith.api.common.block.blockdata.BlockDataFactory;
import net.darkaqua.blacksmith.api.common.block.blockdata.IBlockData;
import net.darkaqua.blacksmith.api.common.block.blockdata.IBlockDataGenerator;
import net.darkaqua.blacksmith.api.common.block.blockdata.defaults.BlockAttributeValueDirection;
import net.darkaqua.blacksmith.api.common.block.methods.BlockMethod;
import net.darkaqua.blacksmith.api.common.entity.ILivingEntity;
import net.darkaqua.blacksmith.api.common.inventory.IItemStack;
import net.darkaqua.blacksmith.api.client.render.block.IBlockModelProvider;
import net.darkaqua.blacksmith.api.client.render.block.defaults.SimpleBlockModelProvider;
import net.darkaqua.blacksmith.api.client.render.item.defaults.SimpleItemBlockModelProvider;
import net.darkaqua.blacksmith.api.common.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.common.util.Direction;
import net.darkaqua.blacksmith.api.common.util.WorldRef;
import net.darkaqua.blacksmith.api.common.world.IWorld;

/**
 * Created by cout970 on 20/01/2016.
 */
public class BlockKineticGrinder extends BlockModeled implements IBlockContainerDefinition, BlockMethod.OnPlacedBy{

    public IBlockModelProvider getModelProvider() {
        return new SimpleItemBlockModelProvider(iModelRegistry -> new SimpleBlockModelProvider.BlockModel(
                iModelRegistry.registerModelPart(Magneticraft.IDENTIFIER, ModelConstants.KINETIC_GRINDER)));
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
