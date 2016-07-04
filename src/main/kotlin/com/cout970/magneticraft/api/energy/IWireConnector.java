package com.cout970.magneticraft.api.energy;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

/**
 * Created by cout970 on 03/07/2016.
 */
public interface IWireConnector extends IElectricNode {

    //List of points in the node where a wire can be attached
    ImmutableList<Vec3d> getConnections();

    default int getConnectionIndex(int index, @NotNull IWireConnector connector, @NotNull IElectricConnection connection){
        return index;
    }
}
