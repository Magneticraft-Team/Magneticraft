package com.cout970.magneticraft.api.network;


/**
 * Created by cout970 on 29/12/2015.
 */
public interface INetworkNode<T extends Network, D extends INetworkNode> extends IConnectable<D>{

    T getNetwork();

    void setNetwork(T net);

    boolean isValid();

    void onNetworkChange();

    void createNetwork();
}
