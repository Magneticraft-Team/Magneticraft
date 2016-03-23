package com.cout970.magneticraft.item;

import com.cout970.magneticraft.ManagerApi;
import com.cout970.magneticraft.api.tool.IHammer;
import net.darkaqua.blacksmith.render.ItemCameraHelper;
import net.darkaqua.blacksmith.util.WorldRef;
import net.darkaqua.blacksmith.vectors.Vect3d;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.vecmath.Matrix4f;
import java.util.function.Function;

/**
 * Created by cout970 on 22/03/2016.
 */
public abstract class ItemAbstractHammer extends ItemBase implements IHammer, ICapabilityProvider {

    public ItemAbstractHammer() {
        maxStackSize = 1;
    }

    @Override
    public ItemStack tick(ItemStack hammer, WorldRef ref) {
        if (hammer.getItemDamage() < hammer.getMaxDamage()) {
            hammer.setItemDamage(hammer.getItemDamage() + 1);
            return hammer;
        } else {
            return null;
        }
    }

    @Override
    public boolean canHammer(ItemStack hammer, WorldRef ref) {
        return true;
    }

    @Override
    public Function<ItemCameraTransforms.TransformType, Matrix4f> getTransformation() {
        return (place) -> {
            if (place == ItemCameraTransforms.TransformType.THIRD_PERSON) {
                return ItemCameraHelper.getMatrix(new Vect3d(0, 1.25, -3.5).multiply(1 / 16d),
                        new Vect3d(0, 90, -35), new Vect3d(0.85, 0.85, 0.85));
            } else if (place == ItemCameraTransforms.TransformType.FIRST_PERSON) {
                return ItemCameraHelper.getMatrix(new Vect3d(0, 4, 2).multiply(1 / 16d),
                        new Vect3d(0, -135, 25), new Vect3d(1.7, 1.7, 1.7));
            }
            return TRSRTransformation.blockCornerToCenter(new TRSRTransformation(ItemTransformVec3f.DEFAULT)).getMatrix();
        };
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing){
        return capability == ManagerApi.HAMMER;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing){
        return (T) this;
    }
}
