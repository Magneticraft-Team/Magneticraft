package com.cout970.magneticraft.client.tilerender;

import com.cout970.magneticraft.tileentity.TileCrushingTable;
import net.darkaqua.blacksmith.api.Game;
import net.darkaqua.blacksmith.api.client.render.model.RenderPlace;
import net.darkaqua.blacksmith.api.client.render.tileentity.ITileEntityRendererHelper;
import net.darkaqua.blacksmith.api.common.inventory.IItemStack;
import net.darkaqua.blacksmith.api.common.item.IItemBlock;
import net.darkaqua.blacksmith.api.common.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.common.util.vectors.Vect3d;
import org.lwjgl.opengl.GL11;

/**
 * Created by cout970 on 16/12/2015.
 */
public class TileRenderCrushingTable extends TileEntityRenderer<TileCrushingTable> {

    @Override
    public void renderTileEntity(ITileEntity tile, TileCrushingTable def, ITileEntityRendererHelper helper, Vect3d pos, float partialTick, int breakingProgress) {
        IItemStack stack = def.getContent();
        if (stack != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) pos.getX() + 0.5F, (float) pos.getY() + 1.5F, (float) pos.getZ() + 0.5F);

            if (stack.getItem() instanceof IItemBlock) {
                GL11.glTranslatef(0, -0.5f, 0);
                float size = 0.8f;
                GL11.glScalef(size, size, size);
            } else {
                GL11.glTranslatef(0, -39 / 64f, 0);
                GL11.glRotatef(90, 1, 0, 0);
            }
            Game.getClientHandler().getRenderManager().renderItemStack(stack, new Vect3d(0, 0, 0), RenderPlace.NONE);
            GL11.glPopMatrix();
        }
    }

    @Override
    public void initModels() {}
}
