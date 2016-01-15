package com.cout970.magneticraft.client.tilerender;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.client.model.ModelWoodenShaft;
import com.cout970.magneticraft.tileentity.TileWoodenShaft;
import net.darkaqua.blacksmith.api.render.tileentity.ITileEntityRenderer;
import net.darkaqua.blacksmith.api.render.tileentity.ITileEntityRendererHelper;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.util.Vect3d;
import org.lwjgl.opengl.GL11;

/**
 * Created by cout970 on 29/12/2015.
 */
public class TileRenderWoodenShaft implements ITileEntityRenderer<TileWoodenShaft> {

    public static final ModelWoodenShaft model = new ModelWoodenShaft();

    @Override
    public void renderTileEntity(ITileEntity tile, TileWoodenShaft def, ITileEntityRendererHelper helper, Vect3d pos, float partialTick, int breakingProgress) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) pos.getX() + 0.5F, (float) pos.getY() + 1.5F, (float) pos.getZ() + 0.5F);
        GL11.glRotatef(180, 1,0,0);
        helper.bindTexture(ModelConstants.ofTexture(ModelConstants.WOODEN_SHAFT));
        model.render(0.0625f, def.getConnections(), (float) Math.toRadians(def.getRotationAngle(partialTick)));
        GL11.glPopMatrix();
    }
}
