package com.cout970.magneticraft.util.multiblock;

import net.darkaqua.blacksmith.api.util.WorldRef;

public interface MB_ControlBlock extends MB_Block {

    IMultiBlockData getMultiBlockData(WorldRef ref);
}
