package com.cout970.magneticraft.client.tilerender;

import com.cout970.magneticraft.tileentity.TileCrushingTable;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
import net.darkaqua.blacksmith.api.item.IItemBlock;
import net.darkaqua.blacksmith.api.registry.StaticAccess;
import net.darkaqua.blacksmith.api.render.model.RenderPlace;
import net.darkaqua.blacksmith.api.render.tileentity.ITileEntityRenderer;
import net.darkaqua.blacksmith.api.render.tileentity.ITileEntityRendererHelper;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.util.Vect3d;
import org.lwjgl.opengl.GL11;

/**
 * Created by cout970 on 16/12/2015.
 */
public class TileRenderCrushingTable implements ITileEntityRenderer<TileCrushingTable> {

    @Override
    public void renderTileEntity(ITileEntity tile, TileCrushingTable def, ITileEntityRendererHelper helper, Vect3d pos, float partialTick, int breakingProgress) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) pos.getX() + 0.5F, (float) pos.getY() + 1.5F, (float) pos.getZ() + 0.5F);
        IItemStack stack = def.getContent();
        if (stack != null) {
            if (stack.getItem() instanceof IItemBlock) {
                GL11.glTranslatef(0, -0.5f, 0);
                float size = 0.5f;
                GL11.glScalef(size, size, size);
            } else {
                GL11.glTranslatef(0, -39 / 64f, 0);
                GL11.glRotatef(90, 1, 0, 0);
            }
            StaticAccess.GAME.getRenderManager().renderItemStack(stack, new Vect3d(0, 0, 0), RenderPlace.NONE);
        }
        GL11.glPopMatrix();
    }
}
