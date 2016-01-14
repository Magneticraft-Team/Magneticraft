package com.cout970.magneticraft.client.model.item;

import net.darkaqua.blacksmith.api.render.model.IModelPartIdentifier;
import net.darkaqua.blacksmith.api.render.model.RenderPlace;
import net.darkaqua.blacksmith.api.render.model.RenderTransformation;
import net.darkaqua.blacksmith.api.render.model.defaults.ItemFlatModelProvider;
import net.darkaqua.blacksmith.api.util.Vect3d;

/**
 * Created by cout970 on 28/12/2015.
 */
public class SwordRenderModel extends ItemFlatModelProvider.ItemFlatModel {

    public SwordRenderModel(IModelPartIdentifier component) {
        super(component);
    }

    @Override
    public RenderTransformation getTransformation(RenderPlace place) {
        if (place == RenderPlace.THIRD_PERSON) {
            return new RenderTransformation(new Vect3d(0, 1.25, -3.5).multiply(1 / 16d), new Vect3d(0, 90, -35), new Vect3d(0.85, 0.85, 0.85));
        } else if (place == RenderPlace.FIRST_PERSON) {
            return new RenderTransformation(new Vect3d(0, 4, 2).multiply(1 / 16d), new Vect3d(0, -135, 25), new Vect3d(1.7, 1.7, 1.7));
        }
        return null;
    }
}
