package com.cout970.magneticraft.api.conveyorbelt

enum class Route(val leftSide: Boolean, val isRect: Boolean, val isShort: Boolean) {
    LEFT_FORWARD(true, true, false),
    RIGHT_FORWARD(false, true, false),
    LEFT_SHORT(true, false, true),
    LEFT_LONG(true, false, false),
    RIGHT_SHORT(false, false, true),
    RIGHT_LONG(false, false, false),
    LEFT_CORNER(true, false, false),
    RIGHT_CORNER(false, false, false);
}