package com.cout970.magneticraft.client.tilerender;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.kinetic.generators.TileWindTurbine;
import com.cout970.magneticraft.util.MiscUtils;
import net.darkaqua.blacksmith.api.render.model.IDynamicModel;
import net.darkaqua.blacksmith.api.render.techne.TechneModelLoader;
import net.darkaqua.blacksmith.api.render.tileentity.ITileEntityRendererHelper;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.util.Vect3d;
import org.lwjgl.opengl.GL11;

/**
 * Created by cout970 on 03/01/2016.
 */
public class TileRenderWindTurbine extends TileEntityRenderer<TileWindTurbine> {

    private static IDynamicModel model;

    @Override
    public void renderTileEntity(ITileEntity tile, TileWindTurbine def, ITileEntityRendererHelper helper, Vect3d pos, float partialTick, int breakingProgress) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) pos.getX() + 0.5f, (float) pos.getY() - 5, (float) pos.getZ() + 0.5f);

        GL11.glRotatef(MiscUtils.getRotation(def.getDirection()), 0, 1, 0);
        GL11.glTranslatef(-0.5f, 0, -0.5f);

        GL11.glTranslatef(0.5f, 5 + 0.5f, 0);
        GL11.glRotatef(def.getRotationAngle(partialTick), 0, 0, 1);//
        GL11.glTranslatef(-0.5f, -5-0.5f, 0);

        model.renderAll();
        GL11.glPopMatrix();
    }

    @Override
    public void initModels() {
        model = new TechneModelLoader.TechneModel(ModelConstants.WIND_TURBINE);
    }
}
