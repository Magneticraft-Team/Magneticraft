package com.cout970.magneticraft.client.tilerender;

import com.cout970.magneticraft.client.model.ModelCrushingTable;
import com.cout970.magneticraft.client.model.ModelTextures;
import com.cout970.magneticraft.tileentity.TileCrushingTable;
import net.darkaqua.blacksmith.api.block.Blocks;
import net.darkaqua.blacksmith.api.inventory.IItemStack;
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

    private static ModelCrushingTable model = new ModelCrushingTable();

    @Override
    public void renderTileEntity(ITileEntity tile, TileCrushingTable def, ITileEntityRendererHelper helper, Vect3d pos, float partialTick, int breakingProgress) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) pos.getX() + 0.5F, (float) pos.getY() + 1.5F, (float) pos.getZ() + 0.5F);
        GL11.glRotatef(180, 0, 0, 1);

        helper.bindTexture(ModelTextures.CRUSHING_TABLE);
        model.renderStatic(0.0625f);

        IItemStack stack = def.getInventory().getStackInSlot(null, 0);
        stack = Blocks.IRON_ORE.newItemStack(1);
        if(stack != null) {
            GL11.glRotatef(180, 0, 0, 1);
            if(!stack.getItem().is3DItem()) {
                GL11.glTranslatef(0, -0.5f, 0);
                float size = 0.5f;
                GL11.glScalef(size, size, size);
            }else {
                GL11.glTranslatef(0, -39/64f, 0);
                GL11.glRotatef(90, 1, 0, 0);
            }
            StaticAccess.GAME.getRenderManager().bindTexture(StaticAccess.GAME.getRenderManager().getBlockTextureAtlas());
            StaticAccess.GAME.getRenderManager().renderItemStack(stack, RenderPlace.NONE);
        }
        GL11.glPopMatrix();
    }
}
