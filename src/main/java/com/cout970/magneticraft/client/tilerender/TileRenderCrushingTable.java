package com.cout970.magneticraft.client.tilerender;

import com.cout970.magneticraft.client.model.ModelTextures;
import com.cout970.magneticraft.tileentity.TileCrushingTable;
import net.darkaqua.blacksmith.api.render.tileentity.ITileEntityRenderer;
import net.darkaqua.blacksmith.api.render.tileentity.ITileEntityRendererHelper;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.tileentity.ITileEntityDefinition;
import net.darkaqua.blacksmith.api.util.Vect3d;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.ENTITY;

/**
 * Created by cout970 on 16/12/2015.
 */
public class TileRenderCrushingTable implements ITileEntityRenderer {

    @Override
    public void renderTileEntity(ITileEntity tile, ITileEntityDefinition def, ITileEntityRendererHelper helper, Vect3d pos, float partialTick, int breakingProgress) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glRotatef(180, 0, 0, 1);
        TileCrushingTable tile = (TileCrushingTable) t;

        helper.bindTexture(ModelTextures.CRUSHING_TABLE);
        model.renderStatic(0.0625f);
        if (tile.getInput() != null) {
            ItemStack itemstack = tile.getInput();
            if (itemstack.getItemSpriteNumber() == 0 && itemstack.getItem() instanceof ItemBlock
                    && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType())) {
                GL11.glRotatef(180, 0, 0, 1);
                GL11.glTranslatef(0, -0.5F, 0);
            } else if (MinecraftForgeClient.getItemRenderer(itemstack, ENTITY) == null) {
                GL11.glTranslatef(0, 0.605F, -0.1F);
                GL11.glRotatef(90, 1, 0, 0);
            } else {
                GL11.glRotatef(180, 0, 0, 1);
                GL11.glTranslatef(0, -0.5F, 0);
            }
            itemEntity.setEntityItemStack(tile.getInput());
            itemEntity.age = 0;
            itemEntity.hoverStart = 0;

            boolean config = RenderManager.instance.options.fancyGraphics;
            RenderManager.instance.options.fancyGraphics = true;
            RenderItemMG.doRender(itemEntity, 0, 0, 0, 0, 0);
            RenderManager.instance.options.fancyGraphics = config;
        }
        GL11.glPopMatrix();
    }
}
