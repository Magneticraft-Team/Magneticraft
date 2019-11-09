@file:Suppress("unused")

package com.cout970.magneticraft.misc.world

import net.minecraft.entity.item.ItemEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/02/20.
 */

inline val World.isServer: Boolean get() = !isRemote
inline val World.isClient: Boolean get() = isRemote

fun World.dropItem(item: ItemStack, pos: BlockPos, jump: Boolean = true, config: ((ItemEntity) -> Unit)? = null) {
    if (!isServer) return

    val d0: Double
    val d1: Double
    val d2: Double
    if (jump) {
        val f = 0.05f
        d0 = (rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
        d1 = (rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
        d2 = (rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
    } else {
        d0 = 0.5
        d1 = 0.5
        d2 = 0.5
    }
    val entityItem = ItemEntity(this, pos.x.toDouble() + d0, pos.y.toDouble() + d1, pos.z.toDouble() + d2, item)
    entityItem.setDefaultPickupDelay()
    if (!jump) {
        entityItem.motion = Vec3d.ZERO
    }

    config?.invoke(entityItem)
    addEntity(entityItem)
}

fun worldSaveDirectory(): String = TODO()