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
     * This distance is multiplied by the resistance of the nodes to get the resistance of this connection
     *
     * @return The distance between the two nodes
     */
    double getSeparationDistance();

    /**
     * This function balances the heat between the two nodes This should be called every tick and only in the server
     * side
     */
    void iterate();
}

