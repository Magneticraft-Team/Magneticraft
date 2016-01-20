package com.cout970.magneticraft.tileentity.kinetic.generators;

import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import com.cout970.magneticraft.tileentity.kinetic.TileKineticBase;
import net.darkaqua.blacksmith.api.block.blockdata.IBlockData;
import net.darkaqua.blacksmith.api.block.blockdata.defaults.BlockAttributeValueDirection;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.Vect3i;

/**
 * Created by cout970 on 03/01/2016.
 */
public class TileHandCrank extends TileKineticBase {

    private int counter = 0;

    @Override
    public void update(){
        super.update();
        if (counter > 0){
            counter--;
            getNetwork().applyForce(5, 10);
        }
    }

    public void resetCounter(){
        counter = 20;
    }

    @Override
    public boolean isAbleToConnect(IKineticConductor cond, Vect3i offset) {
        return getDirection().matches(offset);
    }

    public Direction getDirection() {
        IBlockData variant = parent.getWorldRef().getBlockData();
        return (Direction) variant.getValue(BlockAttributeValueDirection.DIRECTION).getValue();
    }
}
