package com.cout970.magneticraft.tileentity.modules.conveyor_belt

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.tilerenderer.core.Utilities
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.vector.*
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

class Box(
        val item: ItemStack,
        position: Float,
        val route: Route,
        var lastTick: Long,
        var locked: Boolean = false
) {

    var position: Float = position
        private set

    constructor(nbt: NBTTagCompound) : this(
            ItemStack(nbt.getCompoundTag("item")),
            nbt.getFloat("position"),
            Route.values()[nbt.getInteger("route")],
            -1L
    )

    fun getPosition(now: Long): Float {
        return position
    }

    fun move(amount: Float) {
        position += amount
    }

    fun getPos(partialTicks: Float): IVector3 {
        val pos = position + if (locked) 0f else 0f //partialTicks
        if (route.isRect) {
            val z = Utilities.interpolate(16f, 0f,
                    pos / 16.0f) * Utilities.PIXEL
            val x = when (route) {
                Route.LEFT_FORWARD -> 5 * Utilities.PIXEL
                Route.RIGHT_FORWARD -> 11 * Utilities.PIXEL
                else -> 0.0
            }
            return vec3Of(x, 0, z)
        } else if (route.leftSide) {
            val x: Double
            val z: Double
            if (route.isShort) {
                if (pos < 8f) {
                    x = Utilities.interpolate(0f, 5f,
                            pos / 8.0f) * Utilities.PIXEL
                    z = 5 * Utilities.PIXEL
                } else {
                    x = 5 * Utilities.PIXEL
                    z = Utilities.interpolate(5f, 0f,
                            (pos - 8f) / 8f) * Utilities.PIXEL
                }
            } else {
                if (pos < 5f) {
                    x = Utilities.interpolate(0f, 5f,
                            pos / 5.0f) * Utilities.PIXEL
                    z = 11 * Utilities.PIXEL
                } else {
                    x = 5 * Utilities.PIXEL
                    z = Utilities.interpolate(11f, 0f,
                            (pos - 5) / 11.0f) * Utilities.PIXEL
                }
            }
            return vec3Of(x, 0, z)
        } else if (!route.leftSide) {
            val x: Double
            val z: Double

            if (route.isShort) {
                if (pos < 8f) {
                    x = Utilities.interpolate(16f, 11f,
                            pos / 8f) * Utilities.PIXEL
                    z = 5 * Utilities.PIXEL
                } else {
                    x = 11 * Utilities.PIXEL
                    z = Utilities.interpolate(5f, 0f,
                            (pos - 8f) / 8f) * Utilities.PIXEL
                }
            } else {
                if (pos < 5f) {
                    x = Utilities.interpolate(16f, 11f,
                            pos / 5f) * Utilities.PIXEL
                    z = 11 * Utilities.PIXEL
                } else {
                    x = 11 * Utilities.PIXEL
                    z = Utilities.interpolate(11f, 0f,
                            (pos - 5f) / 11f) * Utilities.PIXEL
                }
            }
            return vec3Of(x, 0, z)
        }
        return vec3Of(0, 0, 0)
    }

    fun getHitBox(): AABB {
        val pos = getPos(0f)
        return (pos - vec3Of(2, 0,
                2) * Utilities.PIXEL) toAABBWith (pos + vec3Of(
                2, 4, 2) * Utilities.PIXEL)
    }

    fun serializeNBT() = newNbt {
        add("item", item.serializeNBT())
        add("position", position)
        add("locked", locked)
        add("route", route.ordinal)
    }
}