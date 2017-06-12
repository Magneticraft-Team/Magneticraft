@file:Suppress("unused")

package com.cout970.magneticraft.misc.world

import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/02/20.
 */

inline val World.isServer: Boolean get() = !isRemote
inline val World.isClient: Boolean get() = isRemote

fun World.dropItem(item: ItemStack, pos: BlockPos) {
    if (isServer) {
        val f = 0.05f
        val d0 = (rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
        val d1 = (rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
        val d2 = (rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
        val entityItem = EntityItem(this, pos.x.toDouble() + d0, pos.y.toDouble() + d1, pos.z.toDouble() + d2, item)
        entityItem.setDefaultPickupDelay()
        spawnEntity(entityItem)
    }
}