package com.cout970.magneticraft.api.registries.generation;

import java.util.Map;

/**
 * Created by cout970 on 2017/07/12.
 */
public interface IOreGenerationRegistry {

    boolean isRegistered(String oreDictName);

    OreGeneration getOreGeneration(String oreDictName);

    Map<String, OreGeneration> getRegisteredOres();

    boolean registerOreGeneration(OreGeneration gen);
}
