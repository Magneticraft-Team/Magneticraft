package com.cout970.magneticraft.util.multiblock;

import net.darkaqua.blacksmith.util.WorldRef;
import net.darkaqua.blacksmith.vectors.Vect3i;

public interface MB_Component {

    boolean matches(final WorldRef ref, Vect3i rot, IMultiBlockData data);

    void setup(WorldRef ref, Vect3i rot, IMultiBlockData data);

    void destroy(WorldRef ref, Vect3i rot, IMultiBlockData data);

    String getErrorMessage(WorldRef ref, Vect3i rot, IMultiBlockData data);
}
