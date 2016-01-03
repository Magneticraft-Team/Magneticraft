package com.cout970.magneticraft.api.network;

import java.util.List;

/**
 * Created by cout970 on 29/12/2015.
 */
public interface INetwork {

    INetworkNode getFirstNode();

    int getNodeCount();

    List<INetworkNode> getNetworkNodes();

    void addNetworkNode(INetworkNode node);

    void removeNetworkNode(INetworkNode node);

    void refreshNetwork();

    void onNetworkChange();

    void mergerNetwork(INetwork net);

    void destroyNetwork();

    boolean canAddToNetwork(INetworkNode node);
}
