package com.cout970.magneticraft.client.tilerender;

import com.cout970.magneticraft.tileentity.TileCrushingTable;
import net.darkaqua.blacksmith.render.RenderManager;
import net.darkaqua.blacksmith.vectors.Vect3d;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

/**
 * Created by cout970 on 16/12/2015.
 */
public class TileRenderCrushingTable extends TileRenderer<TileCrushingTable> {

    @Override
    public void renderTileEntity(TileCrushingTable def, Vect3d pos, float partialTick, int breakingProgress) {
        ItemStack stack = def.getContent();
        if (stack != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) pos.getX() + 0.5F, (float) pos.getY() + 1.5F, (float) pos.getZ() + 0.5F);

            if (stack.getItem() instanceof ItemBlock) {
                GL11.glTranslatef(0, -0.5f, 0);
                float size = 0.8f;
                GL11.glScalef(size, size, size);
            } else {
                GL11.glTranslatef(0, -39 / 64f, 0);
                GL11.glRotatef(90, 1, 0, 0);
            }
            RenderManager.renderItemStack(stack, new Vect3d(0, 0, 0), ItemCameraTransforms.TransformType.NONE);
            GL11.glPopMatrix();
        }
    }

    @Override
    public void initModels() {}
}
