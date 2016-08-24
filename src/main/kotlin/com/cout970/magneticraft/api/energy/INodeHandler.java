package com.cout970.magneticraft.api.energy;

import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * Created by cout970 on 29/06/2016.
 */
public interface INodeHandler {

    /**
     * List of nodes in this handler
     * @return all the nodes in this handler
     */
    List<INode> getNodes();

    /**
     * The position in world of this handler
     * @return The position of this handler
     */
    BlockPos getPos();
}
