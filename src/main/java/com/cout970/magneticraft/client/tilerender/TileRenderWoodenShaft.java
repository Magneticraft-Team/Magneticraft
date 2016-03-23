package com.cout970.magneticraft.client.tilerender;

import com.cout970.magneticraft.client.model.ModelConstants;
import com.cout970.magneticraft.tileentity.kinetic.TileWoodenShaft;
import net.darkaqua.blacksmith.modelloader.techne.TechneDynamicModel;
import net.darkaqua.blacksmith.render.model.IDynamicModel;
import net.darkaqua.blacksmith.util.Direction;
import net.darkaqua.blacksmith.vectors.Vect3d;
import org.lwjgl.opengl.GL11;

/**
 * Created by cout970 on 29/12/2015.
 */
public class TileRenderWoodenShaft extends TileRenderer<TileWoodenShaft> {

    private static IDynamicModel model;
    private static IDynamicModel.IPartSet[] connections;
    private static IDynamicModel.IPartSet center;

    @Override
    public void renderTileEntity(TileWoodenShaft def, Vect3d pos, float partialTick, int breakingProgress) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
        int sides = def.getConnections();
        float rotation = -def.getRotationAngle(partialTick);

        if ((sides & 0x40) > 0) {
            model.renderPartSet(center); // center
        }
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5f, 0, 0.5f);
        GL11.glRotatef(rotation, 0, 1, 0);
        GL11.glTranslatef(-0.5f, 0, -0.5f);
        if ((sides & 0x1) > 0) {
            model.renderPartSet(connections[0]); // down
        }
        if ((sides & 0x2) > 0) {
            model.renderPartSet(connections[1]); // up
        }
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5f, 0.5f, 0);
        GL11.glRotatef(rotation, 0, 0, 1);
        GL11.glTranslatef(-0.5f, -0.5f, 0);
        if ((sides & 0x4) > 0) {
            model.renderPartSet(connections[2]); // north
        }
        if ((sides & 0x8) > 0) {
            model.renderPartSet(connections[3]); // south
        }
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0.5f, 0.5f);
        GL11.glRotatef(rotation, 1, 0, 0);
        GL11.glTranslatef(0, -0.5f, -0.5f);
        if ((sides & 0x10) > 0) {
            model.renderPartSet(connections[4]); // west
        }
        if ((sides & 0x20) > 0) {
            model.renderPartSet(connections[5]); // east
        }
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }

    @Override
    public void initModels() {
        model = new TechneDynamicModel(ModelConstants.WOODEN_SHAFT);
        center = model.createAllContains("center");
        connections = new IDynamicModel.IPartSet[6];
        for (Direction dir : Direction.values()) {
            connections[dir.ordinal()] = model.createAllContains(dir.name().toLowerCase());
        }
    }
}
