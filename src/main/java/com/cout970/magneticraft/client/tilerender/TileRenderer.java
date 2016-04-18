package com.cout970.magneticraft.client.tilerender;

import net.darkaqua.blacksmith.render.tile.TileEntityRenderer;
import net.darkaqua.blacksmith.vectors.Vect3d;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by cout970 on 19/03/2016.
 */
public abstract class TileRenderer<T extends TileEntity> extends TileEntityRenderer<T> {
    @Override
    public void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage) {
        renderTileEntity(te, new Vect3d(x, y, z), partialTicks, destroyStage);
    }

    public abstract void initModels();
}
