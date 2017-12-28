package com.cout970.magneticraft.api.multiblock;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public interface IMultiblock {

    String getMultiblockName();

    BlockPos getMultiblockSize();

    BlockPos getMultiblockCenter();

    Block getControllerBlock();
}
