package com.cout970.magneticraft.block;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.TileWindTurbine;
import net.darkaqua.blacksmith.api.block.*;
import net.darkaqua.blacksmith.api.block.methods.BlockMethod;
import net.darkaqua.blacksmith.api.entity.ILivingEntity;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.render.model.IBlockModelProvider;
import net.darkaqua.blacksmith.api.render.model.defaults.SimpleItemBlockModelProvider;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.WorldRef;
import net.darkaqua.blacksmith.api.world.IWorld;

/**
 * Created by cout970 on 03/01/2016.
 */
public class BlockWindTurbine extends BlockModeled implements BlockMethod.OnPlacedBy, IBlockContainerDefinition {

    public static IIProperty<Direction> DIRECTION = BlockVariantCreatorFactory.createPropertyEnum("direction", Direction.class);

    @Override
    public IBlockVariantCreator getBlockVariantCreator() {
        return BlockVariantCreatorFactory.createBlockVariantCreator(parent, DIRECTION);
    }

    @Override
    public String getBlockName() {
        return "wind_turbine";
    }

    @Override
    public void onPlacedBy(WorldRef ref, IBlockVariant state, ILivingEntity placer, IItemStack stack) {
        ref.setBlockVariant(state.withProperty(DIRECTION, Direction.SOUTH));
    }

    public IBlockModelProvider getModelProvider(){
        return new SimpleItemBlockModelProvider(ModelConstants.ofTechne(ModelConstants.MODEL_WOODEN_SHAFT, ModelConstants.TEXTURE_WOODEN_SHAFT));
    }

    @Override
    public ITileEntityDefinition createTileEntity(IWorld world, IBlockVariant state) {
        return new TileWindTurbine();
    }
}
