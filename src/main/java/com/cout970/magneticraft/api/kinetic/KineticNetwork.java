package com.cout970.magneticraft.api.kinetic;

import com.cout970.magneticraft.api.network.Network;
import net.darkaqua.blacksmith.api.intermod.IInterfaceIdentifier;

/**
 * Created by cout970 on 30/12/2015.
 */
public class KineticNetwork extends Network<IKineticConductor> {

    public static final double R = 1;
    protected double inputForce;
    protected double torque;
    protected double speed;
    protected double angle;
    protected long lastTick;

    public KineticNetwork(IKineticConductor start) {
        super(start);
    }

    @Override
    public void iterate() {
        super.iterate();
        if (getMasterNode() == null) return;
        if (lastTick != getMasterNode().getWorldReference().getWorld().getWorldTime()) {
            lastTick = getMasterNode().getWorldReference().getWorld().getWorldTime();

            torque = Math.max(0, inputForce - getForceConsumed());
//            Log.debug(String.format("input force: %.3f, force consumed: %.3f , lose: %.3f, torque: %.3f, speed: %.3f rpm",
//                    inputForce, getForceConsumed(), getLoss(), torque, speed));
            //torque (M = r x F)
            double efectiveTorque = R * (torque - getLoss());
            //momentum of inertia (I = m*r^2)
            double I = getMass() * R * R;
            //acceleration (M = I*a) -> (a = M/I)
            double a = efectiveTorque / I;
            speed += (a * 360 / 60) / 20d;
            inputForce = 0;

            angle += (speed * 360 / 60) / 20d;
            if (Double.isNaN(angle)) angle = 0;
            angle %= 360;
        }
    }

    public double getMass() {
        return nodes.stream().mapToDouble(IKineticConductor::getMass).sum();
    }

    public double getLoss() {
        return nodes.stream().mapToDouble(IKineticConductor::getLoss).sum();
    }

    public double getForceConsumed() {
        return nodes.stream().mapToDouble(IKineticConductor::getForceConsumed).sum();
    }

    public double getTorque(){
        return torque;
    }

    // The unit of the speed is RPM, Revolutions per minute
    public double getSpeed() {
        return speed;
    }

    public double applyForce(double force) {
        inputForce += force;
        return force;
    }

    public double getRotationAngle() {
        return angle;
    }

    @Override
    public IInterfaceIdentifier<IKineticConductor> getInterfaceIdentifier() {
        return IKineticConductor.IDENTIFIER;
    }
}
