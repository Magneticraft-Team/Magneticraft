package com.cout970.magneticraft.api.kinetic;

import com.cout970.magneticraft.api.network.Network;

import javax.annotation.Nonnull;

/**
 * Created by cout970 on 30/12/2015.
 */
public class KineticNetwork extends Network<IKineticConductor> {

    protected double addedSpeed;
    protected double maxSpeed;
    protected double removedSpeed;
    protected double angle;
    protected long lastTick;

    public KineticNetwork(IKineticConductor start) {
        super(start);
    }

    @Override
    public boolean canAddToNetwork(IKineticConductor node) {
        return true;
    }

    public void iterate() {
        if (getMasterNode() == null) return;
        if (lastTick != getMasterNode().getWorldReference().getWorld().getWorldTime()) {
            lastTick = getMasterNode().getWorldReference().getWorld().getWorldTime();
            //apply loss
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

    public double getMass() {
        return nodes.stream().mapToDouble(IKineticConductor::getMass).sum();
    }

    public double getLose() {
        return nodes.stream().mapToDouble(IKineticConductor::getLoss).sum();
    }

    public double getSpeed() {
        return maxSpeed;
    }

    public void applyForce(double force) {
        addedSpeed += force / getMass();
    }

    public double applyForce(double force, double maxSpeed) {
        double mass = getMass();
        double speedAdded = Math.max(0, Math.min(maxSpeed - removedSpeed, force / mass));
        addedSpeed += speedAdded;
        return speedAdded * mass;
    }

    public double drainForce(double force) {
        double mass = getMass();
        double speedLose = Math.min(removedSpeed, force / mass);
        removedSpeed -= speedLose;
        return speedLose * mass;
    }

    public double getRotationAngle() {
        return angle;
    }

    @Override
    public void addNetworkNode(@Nonnull IKineticConductor node) {

    }

    @Override
    public void removeNetworkNode(@Nonnull IKineticConductor node) {

    }
}
