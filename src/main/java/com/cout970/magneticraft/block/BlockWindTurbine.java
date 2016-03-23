package com.cout970.magneticraft.block;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.kinetic.generators.TileWindTurbine;
import net.darkaqua.blacksmith.block.IStatefulBlock;
import net.darkaqua.blacksmith.modelloader.techne.TechneModelFactory;
import net.darkaqua.blacksmith.render.ItemCameraHelper;
import net.darkaqua.blacksmith.render.model.WrapperBakedModel;
import net.darkaqua.blacksmith.render.providers.IBlockModelProvider;
import net.darkaqua.blacksmith.render.providers.block.ItemOnlyModelProvider;
import net.darkaqua.blacksmith.render.providers.factory.IModelFactory;
import net.darkaqua.blacksmith.util.EntityRotation;
import net.darkaqua.blacksmith.vectors.Vect3d;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.TRSRTransformation;

/**
 * Created by cout970 on 03/01/2016.
 */
public class BlockWindTurbine extends BlockModeled implements ITileEntityProvider, IStatefulBlock {

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, ATTRIBUTE_HORIZONTAL_DIRECTIONS);
    }

    @Override
    public String getBlockName() {
        return "wind_turbine";
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        IBlockState newstate = state.withProperty(ATTRIBUTE_HORIZONTAL_DIRECTIONS, new EntityRotation(placer).toHorizontalAxis());
        worldIn.setBlockState(pos, newstate);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileWindTurbine();
    }


    @Override
    public IBlockModelProvider getModelProvider() {
        IModelFactory factory = getModelFactory();
        return new ModelProvider(factory);
    }

    @Override
    public IModelFactory getModelFactory() {
        return new TechneModelFactory(ModelConstants.WIND_TURBINE_ITEM);
    }


    private static class ModelProvider extends ItemOnlyModelProvider {

        private WrapperBakedModel wrapper;

        public ModelProvider(IModelFactory factory) {
            super(factory);
            wrapper = new WrapperBakedModel(place -> {
                if (place == ItemCameraTransforms.TransformType.THIRD_PERSON) {
                    return ItemCameraHelper.getMatrix(new Vect3d(0, 1, -3).multiply(1 / 16d), new Vect3d(-90, 0, 0), new Vect3d(0.55, 0.55, 0.55).multiply(0.5f));
                } else if (place == ItemCameraTransforms.TransformType.FIRST_PERSON) {
                    return ItemCameraHelper.getMatrix(new Vect3d(0, 4, 2).multiply(1 / 16d), new Vect3d(0, -135, 25), new Vect3d(1.7, 1.7, 1.7).multiply(0.5f));
                } else if (place == ItemCameraTransforms.TransformType.GUI) {
                    return ItemCameraHelper.getMatrix(Vect3d.nullVector(), new Vect3d(90, 0, 0), new Vect3d(1, 1, 1).multiply(0.3f));
                }
                return TRSRTransformation.blockCornerToCenter(new TRSRTransformation(ItemTransformVec3f.DEFAULT)).getMatrix();
            });
        }

        @Override
        public IBakedModel getModelForItemStack(ItemStack stack) {
            wrapper.setModel(model.getBakedModel());
            return wrapper;
        }
    }
}
