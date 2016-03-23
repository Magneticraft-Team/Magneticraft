package com.cout970.magneticraft.block.multiblock;

import com.cout970.magneticraft.block.BlockProperties;
import com.cout970.magneticraft.util.multiblock.IMultiBlockData;
import com.cout970.magneticraft.util.multiblock.MB_ControlBlock;
import com.cout970.magneticraft.util.multiblock.MultiBlock;
import com.cout970.magneticraft.util.multiblock.MultiblockData;
import net.darkaqua.blacksmith.util.WorldRef;

/**
 * Created by cout970 on 15/01/2016.
 */
public abstract class BlockMultiblockControl extends BlockMultiBlockBase implements MB_ControlBlock {

    @Override
    public IMultiBlockData getMultiBlockData(WorldRef ref) {
        return new MultiblockData(ref.getPosition(), ref.getBlockState().getValue(BlockProperties.ATTRIBUTE_HORIZONTAL_DIRECTIONS), getMultiBlock());
    }

    public abstract MultiBlock getMultiBlock();
}
