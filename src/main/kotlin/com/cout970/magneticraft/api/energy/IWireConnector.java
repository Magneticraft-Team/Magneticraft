package com.cout970.magneticraft.api.energy;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.math.Vec3d;

/**
 * Created by cout970 on 03/07/2016.
 */
public interface IWireConnector extends IElectricNode {

    /**
     * List of points in the node where a wire can be attached
     */
    ImmutableList<Vec3d> getConnectors();

    /**
     * Size of the list of connector to avoid creating a list only for checking connectivity
     *
     * @return Amount of connectors in this node
     */
    int getConnectorsSize();

    //this method return the order of the connectors in an specific connection, this is used to avoid crossed wires
    default int getConnectorIndex(int index, IWireConnector connector, IElectricConnection connection) {
        return index;
    }
}
