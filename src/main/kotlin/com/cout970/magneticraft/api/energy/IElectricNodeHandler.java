package com.cout970.magneticraft.api.energy;

import com.cout970.magneticraft.api.core.INodeHandler;
import java.util.List;
import net.minecraft.util.EnumFacing;
import org.jetbrains.annotations.Nullable;

/**
 * Created by cout970 on 18/07/2016.
 */
public interface IElectricNodeHandler extends INodeHandler {

    /**
     * This method retrieves all the connections from other nodes to nodes in this handler All the connections must have
     * the first node in other handler and the second node in this handler
     *
     * @return list of input connections in this handler
     */
    List<IElectricConnection> getInputConnections();

    /**
     * This method retrieves all the connections from nodes in this handler to nodes in other handlers All the
     * connections must have the first node in this handler and the second node in other handler
     *
     * @return list of input connections in this handler
     */
    List<IElectricConnection> getOutputConnections();

    /**
     * Check whether or not this handler allow to make connections between a node from this handler and a node from
     * other handler Side is used to separate connections with wires and connections with adjacent blocks
     *
     * @param thisNode The node in this handler
     * @param other The other handler
     * @param otherNode The node in the other handler
     * @param side The side where the other handler is or null if the connections will be using a wire
     *
     * @return true if the connections is allowed or false if is not allowed
     */
    boolean canConnect(IElectricNode thisNode, IElectricNodeHandler other, IElectricNode otherNode,
                       @Nullable EnumFacing side);

    /**
     * Adds a connections to the handler All the connections must be in two handlers, in one handler the connection must
     * be as output connection and in the other handler must be as input connection
     * <p>
     * if output is true then the connection will be updated every tick and the connection first node must be one of the
     * nodes in this handler if output is false then the the connection will be used to notify then this block is mined
     * to remove it in the other handler
     * <p>
     * Side is used to separate connections with wires and connections with adjacent blocks
     *
     * @param connection The connection to add to this handler
     * @param side The side where the other handler is or null if the connections will be using a wire
     * @param output true if this connection starts in this handler and go to other handler, false if this connection
     * starts in other handler and go to this handler
     */
    void addConnection(IElectricConnection connection, @Nullable EnumFacing side, boolean output);

    /**
     * Notifies this handler that a connection needs to be removed, usually because the other block has been mined
     *
     * @param connection The connection that need to be removed
     */
    void removeConnection(IElectricConnection connection);
}
