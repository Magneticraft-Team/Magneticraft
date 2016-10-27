package com.cout970.magneticraft.api.heat;

/**
 * Created by cout970 on 16/06/2016.
 */
public interface IHeatConnection {

    /**
     * The first node of this connection
     */
    IHeatNode getFirstNode();

    /**
     * The second node of this connection
     */
    IHeatNode getSecondNode();

    /**
     * This function balances the voltage between the two nodes
     * This should be called every tick and only in the server side
     */
    void iterate();
}

