package com.cout970.magneticraft.api.kinetic.defaults;

import com.cout970.magneticraft.api.base.defaults.NetworkBase;
import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import com.cout970.magneticraft.api.kinetic.IKineticNetwork;
import com.cout970.magneticraft.api.network.INetworkNode;
import net.darkaqua.blacksmith.api.intermod.IInterfaceIdentifier;

/**
 * Created by cout970 on 30/12/2015.
 */
public class KineticNetwork extends NetworkBase implements IKineticNetwork {

    protected double addedSpeed;
    protected double maxSpeed;
    protected double removedSpeed;
    protected double angle;
    protected long lastTick;

    public KineticNetwork(INetworkNode start) {
        super(start);
    }

    @Override
    protected IInterfaceIdentifier getInterfaceIdentifier() {
        return IKineticConductor.IDENTIFIER;
    }

    @Override
    public boolean canAddToNetwork(INetworkNode node) {
        return node instanceof IKineticConductor;
    }

    @Override
    public void iterate() {
        if (lastTick != getFirstNode().getWorldReference().getWorld().getWorldTime()) {
            lastTick = getFirstNode().getWorldReference().getWorld().getWorldTime();
            //apply lose
            maxSpeed = addedSpeed - addedSpeed * getLose();
            if (maxSpeed < 0) maxSpeed = 0;
            //reset output buffer
            removedSpeed = maxSpeed;
            //clear input buffer
            addedSpeed = 0;
            //updated rotation
            angle += maxSpeed / 20;
            angle %= 360;
        }
    }

    @Override
    public double getMass() {
        double mass = 0;
        for (INetworkNode n : nodes) {
            if (n instanceof IKineticConductor) {
                mass += ((IKineticConductor) n).getMass();
            }
        }
        return mass;
    }

    @Override
    public double getLose() {
        double lose = 0;
        for (INetworkNode n : nodes) {
            if (n instanceof IKineticConductor) {
                lose += ((IKineticConductor) n).getLose();
            }
        }
        return lose;
    }

    @Override
    public double getSpeed() {
        return maxSpeed;
    }

    @Override
    public void applyForce(double force) {
        addedSpeed += force / getMass();
    }

    @Override
    public double applyForce(double force, double maxSpeed) {
        double mass = getMass();
        double speedAdded = Math.max(0, Math.min(maxSpeed - removedSpeed, force / mass));
        addedSpeed += speedAdded;
        return speedAdded * mass;
    }

    @Override
    public double drainForce(double force) {
        double mass = getMass();
        double speedLose = Math.min(removedSpeed, force / mass);
        removedSpeed -= speedLose;
        return speedLose * mass;
    }

    @Override
    public double getRotationAngle() {
        return angle;
    }
}
