package com.cout970.magneticraft.tileentity.kinetic.generators;

import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import com.cout970.magneticraft.block.BlockProperties;
import com.cout970.magneticraft.tileentity.kinetic.TileKineticBase;
import net.darkaqua.blacksmith.util.Direction;
import net.darkaqua.blacksmith.vectors.Vect3i;
import net.minecraft.block.state.IBlockState;

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
        IBlockState variant = getWorldRef().getBlockState();
        return variant.getValue(BlockProperties.ATTRIBUTE_ALL_DIRECTIONS);
    }
}
