package com.cout970.magneticraft.block.multiblock;

import com.cout970.magneticraft.util.multiblock.IMultiBlockData;
import com.cout970.magneticraft.util.multiblock.MB_ControlBlock;
import com.cout970.magneticraft.util.multiblock.MultiBlock;
import com.cout970.magneticraft.util.multiblock.MultiblockData;
import net.darkaqua.blacksmith.api.block.variants.BlockVariantCreatorFactory;
import net.darkaqua.blacksmith.api.block.variants.IIProperty;
import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.WorldRef;

/**
 * Created by cout970 on 15/01/2016.
 */
public abstract class BlockMultiblockControl extends BlockMultiBlockBase implements MB_ControlBlock {

    public static IIProperty<Direction> DIRECTION = BlockVariantCreatorFactory.createPropertyEnum("direction", Direction.class);

    @Override
    public IMultiBlockData getMultiBlockData(WorldRef ref) {

        return new MultiblockData(ref.getPosition(), ref.getBlockVariant().getValue(DIRECTION), getMultiBlock());
    }

    public abstract MultiBlock getMultiBlock();
}
