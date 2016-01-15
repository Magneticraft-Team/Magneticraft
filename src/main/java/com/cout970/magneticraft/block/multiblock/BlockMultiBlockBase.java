package com.cout970.magneticraft.block.multiblock;

import com.cout970.magneticraft.block.BlockBase;
import com.cout970.magneticraft.util.multiblock.IMultiBlockData;
import com.cout970.magneticraft.util.multiblock.MB_Block;
import net.darkaqua.blacksmith.api.block.variants.BlockVariantCreatorFactory;
import net.darkaqua.blacksmith.api.block.IBlock;
import net.darkaqua.blacksmith.api.block.variants.IIProperty;
import net.darkaqua.blacksmith.api.util.WorldRef;

import java.util.List;

/**
 * Created by cout970 on 15/01/2016.
 */
public abstract class BlockMultiBlockBase extends BlockBase implements MB_Block {

    public static IIProperty<Boolean> ACTIVATION = BlockVariantCreatorFactory.createPropertyBoolean("activation");

    @Override
    public void mutates(WorldRef ref, IMultiBlockData data) {
        ref.setBlockVariant(getBlock().getDefaultVariant().withProperty(ACTIVATION, true));
    }

    @Override
    public void destroy(WorldRef ref, IMultiBlockData data) {
        ref.setBlockVariant(getBlock().getDefaultVariant().withProperty(ACTIVATION, false));
    }

    public boolean matchesAny(WorldRef worldRef, List<IBlock> blocks){
        return blocks.contains(getBlock());
    }
}
