package com.cout970.magneticraft.api.multiblock;

import java.util.Map;

public interface IMultiblockManager {

    Map<String, IMultiblock> getRegisteredMultiblocks();

    // If the multiblock doesn't exist it will throw an exception
    IMultiblock getMultiblock(String name);
}
