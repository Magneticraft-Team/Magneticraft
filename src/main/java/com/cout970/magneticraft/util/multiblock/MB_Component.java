package com.cout970.magneticraft.util.multiblock;

import net.darkaqua.blacksmith.api.common.util.vectors.Vect3i;
import net.darkaqua.blacksmith.api.common.util.WorldRef;

public interface MB_Component {

    boolean matches(final WorldRef ref, Vect3i rot, IMultiBlockData data);

    void setup(WorldRef ref, Vect3i rot, IMultiBlockData data);

    void destroy(WorldRef ref, Vect3i rot, IMultiBlockData data);

    String getErrorMessage(WorldRef ref, Vect3i rot, IMultiBlockData data);
}
