package com.cout970.magneticraft.api.network;

import net.darkaqua.blacksmith.api.util.WorldRef;

/**
 * Created by cout970 on 29/12/2015.
 */
public interface INetworkNode<T extends Network> {

    WorldRef getWorldReference();

    T getNetwork();

    void setNetwork(T net);

    boolean isValid();

    void onNetworkChange();

    void createNetwork();
}
