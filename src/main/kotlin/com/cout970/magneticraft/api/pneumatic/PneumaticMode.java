package com.cout970.magneticraft.api.pneumatic;

/**
 * Indicates the state of an item in a pneumatic tube
 */
public enum PneumaticMode {
    TRAVELING, // moving through tubes to reach a destine
    GOING_BACK, // moving back to any buffer in the system to wait for a new route
}
