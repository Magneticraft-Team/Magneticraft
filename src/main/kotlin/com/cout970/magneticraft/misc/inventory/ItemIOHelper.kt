package com.cout970.magneticraft.misc.inventory

import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.xd
import com.cout970.magneticraft.util.vector.yd
import com.cout970.magneticraft.util.vector.zd
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by Yurgen on 13/12/2016.
 */

//TODO remove
class ItemInputHelper(val world: World, val inBox: AxisAlignedBB, var inv: ItemStackHandler) {

    fun suckItems() {
        val items = world.getEntitiesWithinAABB(EntityItem::class.java, inBox)
        for (i in items) {
            val item = i.entityItem
            for (j in 0 until inv.slots) {
                val inserted = inv.insertItem(j, item, false)
                if (inserted != null) {
                    i.setEntityItemStack(inserted)
                } else {
                    i.setDead()
                    break
                }
            }
        }
    }
}

class ItemOutputHelper(val world: World, val outPos: BlockPos, val off: Vec3d) {

    val outDirection = EnumFacing.getFacingFromVector(off.xd.toFloat(), off.yd.toFloat(), off.zd.toFloat()).opposite

    fun ejectItems(item: ItemStack?, simulate: Boolean): ItemStack? {
        if (item == null) return null
        val state = world.getBlockState(outPos + BlockPos(off))
        val tile = world.getTileEntity(outPos + BlockPos(off))
        if (tile != null) {
            val inventory = ITEM_HANDLER!!.fromTile(tile, outDirection)
            if (inventory != null) {
                for (slot in 0 until inventory.slots) {
                    val result = inventory.insertItem(slot, item.copy(), true)
                    if (result == null) {
                        if (!simulate) inventory.insertItem(slot, item.copy(), false)
                        return null
                    }
                }
            }
        }
        //TODO find a better way to know if you can drop the item or not
        if (!state.isFullCube) {
            if (!simulate) dropOutput(item.copy())
            return null
        }
        return item
    }

    fun dropOutput(item: ItemStack) {
        if (world.isServer) {
            val entityItem = EntityItem(world, outPos.x.toDouble() + 0.5, outPos.y.toDouble() + 0.5, outPos.z.toDouble() + 0.5, item)
            entityItem.motionX = 0.0
            entityItem.motionY = 0.0
            entityItem.motionZ = 0.0
            entityItem.setDefaultPickupDelay()
            world.spawnEntityInWorld(entityItem)
        }
    }
}