package com.cout970.magneticraft.api.energy;

import java.util.List;

/**
 * Created by cout970 on 29/06/2016.
 */
public interface INodeHandler {

    List<INode> getNodes();

    List<IElectricConnection> getConnections();

    boolean canBeConnected(IElectricNode nodeA, IElectricNode nodeB);
}
