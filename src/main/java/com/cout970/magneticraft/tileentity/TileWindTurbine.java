package com.cout970.magneticraft.tileentity;

import com.cout970.magneticraft.api.base.IConnectable;
import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import com.cout970.magneticraft.api.kinetic.defaults.KineticConductor;
import com.cout970.magneticraft.block.BlockWindTurbine;
import com.cout970.magneticraft.tileentity.base.TileKineticBase;
import com.cout970.magneticraft.util.FractalLib;
import net.darkaqua.blacksmith.api.block.IBlockVariant;
import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.Vect3i;
import net.darkaqua.blacksmith.api.world.IWorld;

/**
 * Created by cout970 on 03/01/2016.
 */
public class TileWindTurbine extends TileKineticBase {

    private int tracer;
    private byte[] rayTrace;
    private int efficiency;
    private float speed;

    @Override
    public void update() {
        super.update();
        if (getWorld().getWorldTime() % 20 == 0) {
            double wind = 10000.0d * getWindSpeed();
            int power = (int) (wind * efficiency / 5780d);
            speed = (float) (Math.sqrt(power) * 0.1d);
            traceAir1();
        }
        cond.getNetwork().applyForce(speed, 20 * speed);
    }

    private void traceAir1() {
        int yHeight = this.tracer / 17;
        int var2 = this.tracer % 17;
        Direction rightHand = getDirection().step(Direction.UP);
        Vect3i pos = parent.getWorldRef().getPosition();
        pos.add(getDirection().toVect3i().multiply(2));
        pos.add(rightHand.toVect3i().multiply(var2 - 8));
        pos.add(0, yHeight, 0);
        int air;

        IWorld w = parent.getWorldRef().getWorld();
        for (air = 0; air < 20 && net.darkaqua.blacksmith.api.block.Blocks.AIR.getBlock().equals(w.getBlockVariant(pos).getBlock()); ++air) {
            pos.add(getDirection());
        }

        if (this.rayTrace == null) {
            this.rayTrace = new byte[289];
        }

        this.efficiency = this.efficiency - this.rayTrace[this.tracer] + air;
        this.rayTrace[this.tracer] = (byte) air;
        ++this.tracer;

        if (this.tracer >= 289) {
            this.tracer = 0;
        }
    }

    @Override
    protected IKineticConductor createKineticConductor() {
        return new KineticConductor(getParent()) {
            @Override
            public boolean isAbleToConnect(IConnectable cond, Vect3i offset) {
                return cond instanceof IKineticConductor && getDirection().matches(offset);
            }
        };
    }

    public Direction getDirection() {
        IBlockVariant variant = parent.getWorldRef().getBlockVariant();
        return variant.getValue(BlockWindTurbine.DIRECTION);
    }

    public double getWindSpeed() {
        double tot = FractalLib.noise1D(2576710L, (double) getWorld().getWorldTime() * 1.0E-4D, 0.6F, 5);
        tot = Math.max(0.0D, 1.6D * (tot - 0.5D) + 0.5D);

        if (getWorld().isThundering()) {
            return 4.0D * tot;
        }

        if (getWorld().isRaining()) {
            return 0.5D + 0.5D * tot;
        }
        return tot;
    }
}
