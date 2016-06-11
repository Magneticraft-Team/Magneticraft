package com.cout970.magneticraft.block.states;

import net.minecraft.util.IStringSerializable;

/**
 * Created by cout970 on 11/06/2016.
 */
public enum BlockLimestoneStates implements IStringSerializable {

    //this will be extended in the future for new types of limestone blocks
    NORMAL,
    BRICK,
    COBBLE;

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
