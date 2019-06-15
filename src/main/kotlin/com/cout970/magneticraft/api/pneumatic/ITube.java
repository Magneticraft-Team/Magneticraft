package com.cout970.magneticraft.api.pneumatic;

import net.minecraft.util.EnumFacing;

/**
 * This interface is provided by tubes and the pathfinder will treat it a bit differently than ITubeConnectable
 */
public interface ITube extends ITubeConnectable {

    /**
     * Indicates that this tube can let item move toward that side
     *
     * @param side the output side of the tube
     *
     * @return if the item can move towards that side
     */
    boolean canRouteItemsTo(EnumFacing side);
}
