package com.cout970.magneticraft.api.energy;

/**
 * Created by cout970 on 16/06/2016.
 */
public interface IElectricConnection {

    /**
     * The first node of this connection
     */
    IElectricNode getFirstNode();

    /**
     * The second node of this connection
     */
    IElectricNode getSecondNode();

    /**
     * This distance is multiplied by the resistance of the nodes to get the resistance of this connection
     *
     * @return The distance between the two nodes
     */
    double getSeparationDistance();

    /**
     * This function balances the voltage between the two nodes This should be called every tick and only in the server
     * side
     */
    void iterate();
}
