package com.cout970.magneticraft.client.tilerender;

import net.darkaqua.blacksmith.render.tile.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by cout970 on 19/03/2016.
 */
public abstract class TileRenderer<T extends TileEntity> extends TileEntityRenderer<T> {

    public abstract void initModels();
}
