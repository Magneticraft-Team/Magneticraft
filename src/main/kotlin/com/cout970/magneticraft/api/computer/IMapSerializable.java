package com.cout970.magneticraft.api.computer;

import java.util.Map;

/**
 * This class is meant to imitate INBTSerializable, but removing any dependency to the Minecraft, so this can be used in
 * the external emulator
 */
public interface IMapSerializable {

    Map<String, Object> serialize();

    void deserialize(Map<String, Object> map);
}
