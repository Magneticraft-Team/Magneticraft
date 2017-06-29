package com.cout970.magneticraft.api.core;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by cout970 on 2017/06/23.
 */
public interface ITileRef {

    /**
     * @return The world where the TileEntity lives
     */
    World getWorld();

    /**
     * @return The position where the TileEntity is located
     */
    BlockPos getPos();

}
