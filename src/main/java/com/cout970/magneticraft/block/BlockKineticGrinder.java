package com.cout970.magneticraft.block;

import com.cout970.magneticraft.Magneticraft;
import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.kinetic.TileKineticGrinder;
import net.darkaqua.blacksmith.api.common.block.IBlockContainerDefinition;
import net.darkaqua.blacksmith.api.common.block.blockdata.BlockDataFactory;
import net.darkaqua.blacksmith.api.common.block.blockdata.IBlockData;
import net.darkaqua.blacksmith.api.common.block.blockdata.IBlockDataHandler;
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
    public IBlockDataHandler getBlockDataGenerator() {
        return BlockDataFactory.createBlockDataHandler(parent, BlockDataFactory.ATTRIBUTE_HORIZONTAL_DIRECTIONS);
    }

    @Override
    public void onPlacedBy(WorldRef ref, IBlockData state, ILivingEntity placer, IItemStack stack) {
        state = state.getBlockDataHandler().setValue(state, BlockDataFactory.ATTRIBUTE_HORIZONTAL_DIRECTIONS, placer.getEntityRotation().toHorizontalAxis());
        ref.setBlockData(state);
    }

    @Override
    public ITileEntityDefinition createTileEntity(IWorld world, IBlockData state) {
        return new TileKineticGrinder();
    }

    @Override
    public IBlockData translateMetadataToVariant(int meta) {
        return parent.getDefaultBlockData().getBlockDataHandler().setValue(parent.getDefaultBlockData(), BlockDataFactory.ATTRIBUTE_HORIZONTAL_DIRECTIONS, Direction.HORIZONTAL_DIRECTIONS[meta%4]);
    }

    @Override
    public int translateVariantToMetadata(IBlockData variant) {
        return variant.getValue(BlockDataFactory.ATTRIBUTE_HORIZONTAL_DIRECTIONS).ordinal();
    }
}
