package com.cout970.magneticraft.block.multiblock;

import com.cout970.magneticraft.util.multiblock.IMultiBlockData;
import com.cout970.magneticraft.util.multiblock.MB_ControlBlock;
import com.cout970.magneticraft.util.multiblock.MultiBlock;
import com.cout970.magneticraft.util.multiblock.MultiblockData;
import net.darkaqua.blacksmith.api.common.block.blockdata.BlockDataFactory;
import net.darkaqua.blacksmith.api.common.block.blockdata.IBlockAttribute;
import net.darkaqua.blacksmith.api.common.block.blockdata.defaults.BlockAttributeValueDirection;
import net.darkaqua.blacksmith.api.common.util.Direction;
import net.darkaqua.blacksmith.api.common.util.WorldRef;

/**
 * Created by cout970 on 15/01/2016.
 */
public abstract class BlockMultiblockControl extends BlockMultiBlockBase implements MB_ControlBlock {

    public static IBlockAttribute DIRECTION = BlockDataFactory.createBlockAttribute("direction", BlockAttributeValueDirection.HORIZONTAL_VALUES);

    @Override
    public IMultiBlockData getMultiBlockData(WorldRef ref) {
        return new MultiblockData(ref.getPosition(), (Direction) ref.getBlockData().getValue(DIRECTION).getValue(), getMultiBlock());
    }

    public abstract MultiBlock getMultiBlock();
}
