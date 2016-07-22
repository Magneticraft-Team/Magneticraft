package com.cout970.magneticraft.api.energy;

import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * Created by cout970 on 29/06/2016.
 */
public interface INodeHandler {

    List<INode> getNodes();

    BlockPos getPos();
}
