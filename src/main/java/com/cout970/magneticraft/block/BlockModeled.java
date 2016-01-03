package com.cout970.magneticraft.block;

/**
 * Created by cout970 on 03/01/2016.
 */
public abstract class BlockModeled extends BlockBase {

    @Override
    public float getLightOpacity() {
        return 0f;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }
}
