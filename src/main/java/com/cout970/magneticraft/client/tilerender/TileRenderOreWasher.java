package com.cout970.magneticraft.client.tilerender;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.kinetic.TileOreWasher;
import net.darkaqua.blacksmith.modelloader.techne.TechneDynamicModel;
import net.darkaqua.blacksmith.render.model.IDynamicModel;
import net.darkaqua.blacksmith.vectors.Vect3d;
import org.lwjgl.opengl.GL11;

/**
 * Created by cout970 on 05/03/2016.
 */
public class TileRenderOreWasher extends TileRenderer<TileOreWasher> {

    private static IDynamicModel model;
    private static IDynamicModel.IPartSet base;
    private static IDynamicModel.IPartSet blades;

    @Override
    public void initModels() {
        model = new TechneDynamicModel(ModelConstants.ORE_WASHER);
        base = model.createAllContains("base");
        blades = model.createAllContains("rot");
    }

    @Override
    public void renderTileEntity(TileOreWasher def, Vect3d offset, float partialTick, int breakingProgress) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) offset.getX(), (float) offset.getY(), (float) offset.getZ());
        model.renderPartSet(base);
        float rotation = def.getRotationAngle(partialTick);

        GL11.glTranslated(0.5, 0, 0.5);
        GL11.glRotated(-rotation, 0, 1, 0);
        GL11.glTranslated(-0.5, 0, -0.5);
        model.renderPartSet(blades);

        GL11.glPopMatrix();
    }
}
