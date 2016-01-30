package com.cout970.magneticraft.tileentity.kinetic;

import net.darkaqua.blacksmith.api.block.blockdata.IBlockData;
import net.darkaqua.blacksmith.api.block.blockdata.defaults.BlockAttributeValueDirection;
import net.darkaqua.blacksmith.api.util.Direction;

/**
 * Created by cout970 on 20/01/2016.
 */
public class TileKineticGrinder extends TileKineticBase {

    public Direction getDirection() {
        IBlockData variant = parent.getWorldRef().getBlockData();
        return (Direction) variant.getValue(BlockAttributeValueDirection.HORIZONTAL_DIRECTION).getValue();
    }
}
