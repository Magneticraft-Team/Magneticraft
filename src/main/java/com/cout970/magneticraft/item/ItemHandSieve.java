package com.cout970.magneticraft.item;

import com.cout970.magneticraft.client.model.ModelConstants;
import net.darkaqua.blacksmith.modelloader.techne.TechneModelFactory;
import net.darkaqua.blacksmith.render.ItemCameraHelper;
import net.darkaqua.blacksmith.render.providers.factory.IModelFactory;
import net.darkaqua.blacksmith.vectors.Vect3d;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraftforge.client.model.TRSRTransformation;

import javax.vecmath.Matrix4f;
import java.util.function.Function;

/**
 * Created by cout970 on 28/12/2015.
 */
public class ItemHandSieve extends ItemBase {

    public ItemHandSieve() {
        maxStackSize = 1;
    }

    @Override
    public Function<ItemCameraTransforms.TransformType, Matrix4f> getTransformation() {
        return (place) -> {
            if (place == ItemCameraTransforms.TransformType.THIRD_PERSON) {
                return ItemCameraHelper.getMatrix(new Vect3d(0, 5.5, -1).multiply(1 / 16d), new Vect3d(20, 0, 0), new Vect3d(1, 1, 1));
            } else if (place == ItemCameraTransforms.TransformType.FIRST_PERSON) {
                return ItemCameraHelper.getMatrix(new Vect3d(0, 3, -7).multiply(1 / 16d), new Vect3d(90, -120, 15), new Vect3d(2.5, 2.5, 2.5));
            } else if (place == ItemCameraTransforms.TransformType.GUI) {
                return ItemCameraHelper.getMatrix(new Vect3d(0, 0, 0).multiply(1 / 16d), new Vect3d(90, 0, 0), new Vect3d(1, 1, 1));
            } else if (place == ItemCameraTransforms.TransformType.NONE) {
                return ItemCameraHelper.getMatrix(new Vect3d(0, 0, 4.5).multiply(1 / 16d), new Vect3d(90, 0, 0), new Vect3d(1.2, 1.2, 1.2));
            }
            return TRSRTransformation.blockCornerToCenter(new TRSRTransformation(ItemTransformVec3f.DEFAULT)).getMatrix();
        };
    }

    @Override
    public IModelFactory getModelFactory() {
        return new TechneModelFactory(ModelConstants.HAND_SIEVE);
    }

    @Override
    public String getItemName() {
        return "hand_sieve";
    }
}
