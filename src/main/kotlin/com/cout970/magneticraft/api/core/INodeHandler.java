package com.cout970.magneticraft.api.core;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
     * Gets a specific node from the handler
     * Will return null if the node was not found
     *
     * @param id of the node
     * @return the selected node
     */
    @Nullable
    INode getNode(@Nonnull NodeID id);

    /**
     * The position in world of this handler
     * @return The position of this handler
     */
    BlockPos getPos();
}
