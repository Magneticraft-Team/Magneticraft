package com.cout970.magneticraft.block.states

import net.minecraft.util.IStringSerializable
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 30/06/2016.
 */
enum class ElectricPoleTypes(
        val offset: Vec3d,
        val offsetY: Int = 0
) : IStringSerializable {

    NORTH(Vec3d(1.0, 0.0, 0.0)),
    NORTH_EAST(Vec3d(0.707106, 0.0, 0.707106)),
    EAST(Vec3d(0.0, 0.0, 1.0)),
    SOUTH_EAST(Vec3d(-0.707106, 0.0, 0.707106)),
    SOUTH(Vec3d(-1.0, 0.0, 0.0)),
    SOUTH_WEST(Vec3d(-0.707106, 0.0, -0.707106)),
    WEST(Vec3d(0.0, 0.0, -1.0)),
    NORTH_WEST(Vec3d(0.707106, 0.0, -0.707106)),
    DOWN_1(Vec3d.ZERO, 1),
    DOWN_2(Vec3d.ZERO, 2),
    DOWN_3(Vec3d.ZERO, 3),
    DOWN_4(Vec3d.ZERO, 4);

    override fun getName() = name.toLowerCase()

    fun isMainBlock() = offsetY == 0
}