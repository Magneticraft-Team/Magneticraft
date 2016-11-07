package com.cout970.magneticraft.api.heat;

import com.cout970.magneticraft.api.energy.INodeHandler;

import java.util.List;

/**
 * Created by Yurgen on 19/10/2016.
 */
public interface IHeatHandler extends INodeHandler {

    /*
     * Returns list of directions with active heat connections
     */
    List<IHeatConnection> getConnections();

    /**
     * Re-scans directions which connect to heat containers.
     */
    void updateHeatConnections();

    /**
     * Adds a new heat connection to this handler.
     */
    void addConnection(IHeatConnection connection);

    /**
     * Notifies this handler that a connection needs to be removed, usually because the other block has been mined
     *
     * @param connection The connection that need to be removed
     */
    void removeConnection(IHeatConnection connection);

    /**
     * Notifies this handler that a connection needs to be removed, usually because the other block has been mined
     *
     * @param node The node corresponding to connections that need to be removed
     */
    void removeConnection(IHeatNode node);
}