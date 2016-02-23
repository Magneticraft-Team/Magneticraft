package com.cout970.magneticraft.tileentity.kinetic.generators;

import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import com.cout970.magneticraft.tileentity.kinetic.TileKineticBase;
import net.darkaqua.blacksmith.api.common.block.blockdata.BlockDataFactory;
import net.darkaqua.blacksmith.api.common.block.blockdata.IBlockData;
import net.darkaqua.blacksmith.api.common.util.Direction;
import net.darkaqua.blacksmith.api.common.util.vectors.Vect3i;

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
            getNetwork().applyForce(5);
        }
    }

    public void resetCounter(){
        counter = 20;
    }

    @Override
    public double getLoss() {
        double speed = getNetwork().getSpeed();
        return super.getLoss() + speed * speed * 0.5D * 0.001;
    }

    @Override
    public double getMass() {
        return 0.5d;
    }

    @Override
    public boolean isAbleToConnect(IKineticConductor cond, Vect3i offset) {
        return getDirection().matches(offset);
    }

    public Direction getDirection() {
        IBlockData variant = parent.getWorldRef().getBlockData();
        return variant.getValue(BlockDataFactory.ATTRIBUTE_ALL_DIRECTIONS);
    }
}
