package com.cout970.magneticraft.api.kinetic;

import com.cout970.magneticraft.api.network.INetwork;

/**
 * Created by cout970 on 03/01/2016.
 */
public interface IKineticNetwork extends INetwork {

    void iterate();

    double getMass();

    double getLose();

    double getSpeed();

    void applyForce(double force);

    double applyForce(double force, double maxSpeed);

    double drainForce(double force);

    double getRotationAngle();
}
