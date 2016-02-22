package com.cout970.magneticraft.util.multiblock;

import net.darkaqua.blacksmith.api.common.block.IBlock;
import net.darkaqua.blacksmith.api.common.util.WorldRef;

import java.util.List;

public interface MB_Block {

    void mutates(WorldRef ref, IMultiBlockData data);

    void destroy(WorldRef ref, IMultiBlockData data);

    boolean matchesAny(WorldRef worldRef, List<IBlock> blocks);
}
