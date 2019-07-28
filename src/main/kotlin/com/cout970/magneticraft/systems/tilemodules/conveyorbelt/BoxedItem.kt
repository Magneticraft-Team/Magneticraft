package com.cout970.magneticraft.systems.tilemodules.conveyorbelt

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.IVector3
import com.cout970.magneticraft.api.conveyorbelt.Route
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.systems.tilerenderers.PIXEL
import com.cout970.magneticraft.systems.tilerenderers.Utilities
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

data class BoxedItem(
    val item: ItemStack,
    var position: Float,
    val route: Route,
    var lastTick: Long,
    var locked: Boolean = false
) {

    constructor(nbt: NBTTagCompound) : this(
        ItemStack(nbt.getCompoundTag("item")),
        nbt.getFloat("position"),
        Route.values()[nbt.getInteger("route")],
        -1L
    )

    fun move(amount: Float) {
        position += amount
    }

    fun getPos(partialTicks: Float): IVector3 {
        val pos = position + if (locked) 0f else partialTicks

        if (route.isRect) {
            val z = Utilities.interpolate(16f, 0f, pos / 16.0f) * PIXEL
            val x = when (route) {
                Route.LEFT_FORWARD -> 5 * PIXEL
                Route.RIGHT_FORWARD -> 11 * PIXEL
                else -> 0.0
            }
            return vec3Of(x, 0, z)
        } else if (route.leftSide) {
            val x: Double
            val z: Double

            if (route.isShort) {
                if (pos < 8f) {
                    x = Utilities.interpolate(0f, 5f, pos / 8.0f) * PIXEL
                    z = 5 * PIXEL
                } else {
                    x = 5 * PIXEL
                    z = Utilities.interpolate(5f, 0f, (pos - 8f) / 8f) * PIXEL
                }

            } else if (route == Route.LEFT_CORNER) {

                if (pos < 8f) {
                    x = Utilities.interpolate(16f, 5f, pos / 8f) * PIXEL
                    z = 11 * PIXEL
                } else {
                    x = 5f * PIXEL
                    z = Utilities.interpolate(11f, 0f, (pos - 8f) / 8f) * PIXEL
                }

            } else {
                if (pos < 5f) {
                    x = Utilities.interpolate(0f, 5f, pos / 5.0f) * PIXEL
                    z = 11 * PIXEL
                } else {
                    x = 5 * PIXEL
                    z = Utilities.interpolate(11f, 0f, (pos - 5) / 11.0f) * PIXEL
                }
            }
            return vec3Of(x, 0, z)
        } else if (!route.leftSide) {
            val x: Double
            val z: Double

            if (route.isShort) {
                if (pos < 8f) {
                    x = Utilities.interpolate(16f, 11f, pos / 8f) * PIXEL
                    z = 5 * PIXEL
                } else {
                    x = 11 * PIXEL
                    z = Utilities.interpolate(5f, 0f, (pos - 8f) / 8f) * PIXEL
                }

            } else if (route == Route.RIGHT_CORNER) {

                if (pos < 8f) {
                    x = Utilities.interpolate(0f, 11f, pos / 8f) * PIXEL
                    z = 11f * PIXEL
                } else {
                    x = 11f * PIXEL
                    z = Utilities.interpolate(11f, 0f, (pos - 8f) / 8f) * PIXEL
                }

            } else {
                if (pos < 5f) {
                    x = Utilities.interpolate(16f, 11f, pos / 5f) * PIXEL
                    z = 11 * PIXEL
                } else {
                    x = 11 * PIXEL
                    z = Utilities.interpolate(11f, 0f, (pos - 5f) / 11f) * PIXEL
                }
            }
            return vec3Of(x, 0.0, z)
        }
        return vec3Of(0.0, 0.0, 0.0)
    }

    fun getHitBox(): AABB {
        val pos = getPos(0f)
        return (pos - vec3Of(2, 0, 2) * PIXEL) createAABBUsing (pos + vec3Of(2, 4, 2) * PIXEL)
    }

    fun serializeNBT() = newNbt {
        add("item", item.serializeNBT())
        add("position", position)
        add("route", route.ordinal)
    }
}