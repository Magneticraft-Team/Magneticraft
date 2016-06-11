package com.cout970.magneticraft.block.states;

import net.minecraft.util.IStringSerializable;

/**
 * Created by cout970 on 12/05/2016.
 */
public enum BlockOreStates implements IStringSerializable {

    COPPER,
    LEAD,
    COBALT,
    TUNGSTEN;

    @Override
    public String getName(){
        return name().toLowerCase();
    }
}
