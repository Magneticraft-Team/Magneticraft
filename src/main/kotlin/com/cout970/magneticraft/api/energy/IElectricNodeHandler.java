package com.cout970.magneticraft.api.energy;

import net.minecraft.util.EnumFacing;

import java.util.List;

/**
 * Created by cout970 on 18/07/2016.
 */
public interface IElectricNodeHandler extends INodeHandler {

    /**
     * The list with all the connections between electric nodes in this handler
     *
     * @return a list with all the connections in this handler
     */
    List<IElectricConnection> getConnections();

    /**
     * Tries to create a connection between otherNode and thisNode, if this has success then the
     * connection will be stored in this handler and a reference will be returned to be stored
     * in the other handler
     * If the connection is with an adjacent block the this will be the side of this block where the connection will be created,
     * If the connection is using wires then side will be null
     *
     * @param other     The handler that contains the otherNode
     * @param otherNode The external node that want to be connected with this
     * @param thisNode  The internal node in this handler that will be connected
     * @param side      The side of the block or null
     * @return The connections between nodes, this will be null if the connection can't be established
     */
    IElectricConnection createConnection(IElectricNodeHandler other, IElectricNode otherNode, IElectricNode thisNode, EnumFacing side);

    /**
     * Notifies this handler that a connection needs to be removed, usually because the other block has been mined
     *
     * @param connection The connection that need to be removed
     */
    void removeConnection(IElectricConnection connection);
}
