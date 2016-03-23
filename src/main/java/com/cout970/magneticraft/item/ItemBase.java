package com.cout970.magneticraft.item;

import com.cout970.magneticraft.Magneticraft;
import com.cout970.magneticraft.block.BlockBase;
import net.darkaqua.blacksmith.render.ITexture;
import net.darkaqua.blacksmith.render.ItemCameraHelper;
import net.darkaqua.blacksmith.render.TextureManager;
import net.darkaqua.blacksmith.render.model.WrapperBakedModel;
import net.darkaqua.blacksmith.render.providers.IItemModelProvider;
import net.darkaqua.blacksmith.render.providers.factory.DefaultItemModelFactory;
import net.darkaqua.blacksmith.render.providers.factory.IModelFactory;
import net.darkaqua.blacksmith.render.providers.item.UniqueModelProvider;
import net.darkaqua.blacksmith.util.ResourceReference;
import net.darkaqua.blacksmith.vectors.Vect3d;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.TRSRTransformation;

import javax.vecmath.Matrix4f;
import java.util.Collections;
import java.util.function.Function;

/**
 * Created by cout970 on 21/12/2015.
 */
public abstract class ItemBase extends Item {

    public ItemBase() {
        setUnlocalizedName(getItemName());
        setCreativeTab(BlockBase.CREATIVE_TAB_MAIN);
    }

    public abstract String getItemName();

    public IItemModelProvider getModelProvider() {
        IModelFactory factory = getModelFactory();
        WrapperBakedModel wrapper = new WrapperBakedModel(getTransformation());
        return new UniqueModelProvider(factory){
            @Override
            public IBakedModel getModelForItemStack(ItemStack stack) {
                wrapper.setModel(model.getBakedModel());
                return wrapper;
            }
        };
    }

    public IModelFactory getModelFactory(){
        ITexture texture = TextureManager.registerTexture(new ResourceReference(Magneticraft.ID, "items/" + getItemName().toLowerCase()));
        return new DefaultItemModelFactory(Collections.singletonList(texture));
    }

    public Function<ItemCameraTransforms.TransformType,Matrix4f> getTransformation() {
        return (place) -> {
            if (place == ItemCameraTransforms.TransformType.THIRD_PERSON) {
                return ItemCameraHelper.getMatrix(new Vect3d(0, 1, -3).multiply(1 / 16d), new Vect3d(-90, 0, 0), new Vect3d(0.55, 0.55, 0.55));
            } else if (place == ItemCameraTransforms.TransformType.FIRST_PERSON) {
                return ItemCameraHelper.getMatrix(new Vect3d(0, 4, 2).multiply(1 / 16d), new Vect3d(0, -135, 25), new Vect3d(1.7, 1.7, 1.7));
            }
            return TRSRTransformation.blockCornerToCenter(new TRSRTransformation(ItemTransformVec3f.DEFAULT)).getMatrix();
        };
    }
}
