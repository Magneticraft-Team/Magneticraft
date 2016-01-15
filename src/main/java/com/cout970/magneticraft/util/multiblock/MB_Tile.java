package com.cout970.magneticraft.util.multiblock;

import net.darkaqua.blacksmith.api.util.WorldRef;

public interface MB_Tile {

    IMultiBlockData getMultiBlockData();

    void onDestroy(WorldRef ref, IMultiBlockData data);

    void onActivate(WorldRef ref, IMultiBlockData data);
}
