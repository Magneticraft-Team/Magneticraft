package com.cout970.magneticraft.util.multiblock;

import net.darkaqua.blacksmith.util.WorldRef;
import net.minecraft.block.Block;

import java.util.List;

public interface MB_Block {

    void mutates(WorldRef ref, IMultiBlockData data);

    void destroy(WorldRef ref, IMultiBlockData data);

    boolean matchesAny(WorldRef worldRef, List<Block> blocks);
}
