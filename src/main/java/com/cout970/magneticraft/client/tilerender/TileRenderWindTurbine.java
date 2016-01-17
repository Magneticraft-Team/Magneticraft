package com.cout970.magneticraft.client.tilerender;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.client.model.ModelWindTurbine;
import com.cout970.magneticraft.tileentity.TileWindTurbine;
import com.cout970.magneticraft.util.MiscUtils;
import net.darkaqua.blacksmith.api.render.tileentity.ITileEntityRenderer;
import net.darkaqua.blacksmith.api.render.tileentity.ITileEntityRendererHelper;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.util.Vect3d;
import org.lwjgl.opengl.GL11;

/**
 * Created by cout970 on 03/01/2016.
 */
public class TileRenderWindTurbine implements ITileEntityRenderer<TileWindTurbine> {

    public static final ModelWindTurbine model = new ModelWindTurbine();

    @Override
    public void renderTileEntity(ITileEntity tile, TileWindTurbine def, ITileEntityRendererHelper helper, Vect3d pos, float partialTick, int breakingProgress) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) pos.getX() + 0.5F, (float) pos.getY() + 0.5F, (float) pos.getZ() + 0.5F);

        MiscUtils.rotate(def.getDirection());
        GL11.glRotatef(90, 0, 0, 1);
        GL11.glRotatef(90, 1, 0, 0);
        GL11.glTranslatef(0, -1, 0);

        helper.bindTexture(ModelConstants.ofTexture(ModelConstants.WIND_TURBINE));
        GL11.glRotatef(-def.getRotationAngle(partialTick), 0,1,0);
        model.renderStatic(0.0625f);
        GL11.glPopMatrix();
    }
}
