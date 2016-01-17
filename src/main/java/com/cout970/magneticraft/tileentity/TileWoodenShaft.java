package com.cout970.magneticraft.tileentity;

import com.cout970.magneticraft.api.kinetic.IKineticConductor;
import com.cout970.magneticraft.api.kinetic.defaults.KineticConductor;
import com.cout970.magneticraft.tileentity.base.TileKineticBase;
import net.darkaqua.blacksmith.api.tileentity.ITileEntity;
import net.darkaqua.blacksmith.api.util.Direction;
import net.darkaqua.blacksmith.api.util.ObjectScanner;

/**
 * Created by cout970 on 28/12/2015.
 */
public class TileWoodenShaft extends TileKineticBase {

    private int connections;

    @Override
    protected IKineticConductor createKineticConductor() {
        return new KineticConductor(getParent());
    }

    @Override
    public void update() {
        super.update();
        if (getWorld().getWorldTime() % 20 == 0) {
            connections = 0;
            int count = 0;
            Direction first = null;
            Direction second = null;
            for (Direction d : Direction.values()) {
                ITileEntity t = parent.getWorldRef().move(d).getTileEntity();
                IKineticConductor k = (IKineticConductor) ObjectScanner.findInTileEntity(t, IKineticConductor.IDENTIFIER, d);
                if (k != null) {
                    connections |= 1 << d.ordinal();
                    count++;
                    if (first == null) {
                        first = d;
                    } else if (second == null) {
                        second = d;
                    }
                }
            }
            if (count > 2) {
                connections |= 0x40;
            } else if (count == 2 && first != second && first.isPerpendicular(second)) {
                connections |= 0x40;
            } else if (count == 0) {
                connections |= 0x40;
            }
        }
    }

    public int getConnections() {
        //6 first bits, sides, 7th bit center
        return connections;
    }
}
