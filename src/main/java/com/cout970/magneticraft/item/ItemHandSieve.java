package com.cout970.magneticraft.item;

import com.cout970.magneticraft.Magneticraft;
import net.darkaqua.blacksmith.api.render.model.IItemModelProvider;
import net.darkaqua.blacksmith.api.render.model.IRenderModel;
import net.darkaqua.blacksmith.api.render.model.RenderPlace;
import net.darkaqua.blacksmith.api.render.model.RenderTransformation;
import net.darkaqua.blacksmith.api.render.model.defaults.SimpleItemModelProvider;
import net.darkaqua.blacksmith.api.render.techne.TechneModelLoader;
import net.darkaqua.blacksmith.api.util.ResourceReference;
import net.darkaqua.blacksmith.api.util.Vect3d;

/**
 * Created by cout970 on 28/12/2015.
 */
public class ItemHandSieve extends ItemBase {

    public IItemModelProvider getModelProvider() {
        return new SimpleItemModelProvider(TechneModelLoader.loadModel(
                new ResourceReference(Magneticraft.ID, "models/" + getItemName().toLowerCase() + ".tcn"),
                new ResourceReference(Magneticraft.ID, "misc/" + getItemName().toLowerCase()))) {

            public IRenderModel createRenderModel() {
                return new ItemModel(identifier) {
                    @Override
                    public RenderTransformation getTransformation(RenderPlace place) {
                        if (place == RenderPlace.THIRD_PERSON) {
                            return new RenderTransformation(new Vect3d(0, 5.5, -1).multiply(1 / 16d), new Vect3d(20, 0, 0), new Vect3d(1, 1, 1));
                        } else if (place == RenderPlace.FIRST_PERSON) {
                            return new RenderTransformation(new Vect3d(0, 3, -7).multiply(1 / 16d), new Vect3d(90, -120, 15), new Vect3d(2.5, 2.5, 2.5));
                        } else if (place == RenderPlace.GUI) {
                            return new RenderTransformation(new Vect3d(0, 0, 0).multiply(1 / 16d), new Vect3d(90, 0, 0), new Vect3d(1, 1, 1));
                        } else if (place == RenderPlace.NONE) {
                            return new RenderTransformation(new Vect3d(0, 0, 4.5).multiply(1 / 16d), new Vect3d(90, 0, 0), new Vect3d(1.2, 1.2, 1.2));
                        }
                        return null;
                    }

                    @Override
                    public boolean needsInventoryRotation() {
                        return false;
                    }
                };
            }
        };
    }

    @Override
    public String getItemName() {
        return "hand_sieve";
    }
}
