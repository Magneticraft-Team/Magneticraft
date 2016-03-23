package com.cout970.magneticraft.block.multiblock;

import com.cout970.magneticraft.block.BlockBase;
import com.cout970.magneticraft.util.multiblock.IMultiBlockData;
import com.cout970.magneticraft.util.multiblock.MB_Block;
import net.darkaqua.blacksmith.util.WorldRef;
import net.minecraft.block.Block;

import java.util.List;

/**
 * Created by cout970 on 15/01/2016.
 */
public abstract class BlockMultiBlockBase extends BlockBase implements MB_Block {

    @Override
    public void mutates(WorldRef ref, IMultiBlockData data) {
        ref.setBlockState(ref.getBlockState().withProperty(ACTIVATION, true));
    }

    @Override
    public void destroy(WorldRef ref, IMultiBlockData data) {
        ref.setBlockState(ref.getBlockState().withProperty(ACTIVATION, false));
    }

    @Override
    public boolean matchesAny(WorldRef worldRef, List<Block> blocks){
        return blocks.contains(this);
    }
}
