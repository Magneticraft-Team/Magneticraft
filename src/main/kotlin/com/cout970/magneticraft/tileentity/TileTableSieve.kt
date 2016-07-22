package com.cout970.magneticraft.tileentity

import coffee.cypher.mcextlib.extensions.inventories.get
import com.cout970.magneticraft.api.registries.machines.tablesieve.TableSieveRegistry
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.registry.fromTile
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.items.ItemStackHandler
import java.util.*

/**
 * Created by cout970 on 16/06/2016.
 */
class TileTableSieve : TileBase(), ITickable {

    val inventory = ItemStackHandler(1)
    val output = mutableListOf<ItemStack>()
    val updateTime = 20
    val craftingTime = 20
    var tickCounter = 0
    var size = 0

    override fun update() {

        //gets the item on top of the block
        if ((worldObj.totalWorldTime + pos.hashCode()) % updateTime == 0L) {
            suckItems()
        }

        if (!output.isEmpty()) {
            ejectItems()
        }
        if (inventory[0] != null && output.isEmpty()) {
            //waits until the item crafting is done
            if (tickCounter > craftingTime) {
                tickCounter = 0
                craftItem()
            } else {
                tickCounter++
            }
        }
    }

    fun ejectItems() {
        val state = worldObj.getBlockState(pos.down())
        val tile = worldObj.getTileEntity(pos.down())
        if (tile != null) {
            val inventory = ITEM_HANDLER!!.fromTile(tile)
            if (inventory != null) {
                val iterator = output.iterator()
                while (iterator.hasNext()) {
                    val output = iterator.next()
                    for (slot in 0 until inventory.slots) {
                        val result = inventory.insertItem(slot, output.copy(), true)
                        if (result == null) {
                            inventory.insertItem(slot, output.copy(), false)
                            iterator.remove()
                            break
                        }
                    }
                }
            }
        }
        //TODO find a better way to know if you can drop the item or not
        if (!state.isFullCube) {
            while (!output.isEmpty()) {
                dropOutput(output.first().copy())
                output.removeAt(0)
            }
        }
    }

    fun dropOutput(item: ItemStack) {
        if (!world.isRemote) {
            val entityItem = EntityItem(world, pos.x.toDouble() + 0.5, pos.y.toDouble() - 0.5, pos.z.toDouble() + 0.5, item)
            entityItem.motionX = 0.0
            entityItem.motionY = 0.0
            entityItem.motionZ = 0.0
            entityItem.setDefaultPickupDelay()
            world.spawnEntityInWorld(entityItem)
        }
    }

    fun suckItems() {
        val aabb = AxisAlignedBB(pos, pos.up().add(1.0, 1.0, 1.0))
        val items = worldObj.getEntitiesInAABBexcluding(null, aabb, { it is EntityItem })
        for (i in items) {
            if (i !is EntityItem) continue
            val item = i.entityItem
            if (TableSieveRegistry.findRecipe(item) == null) continue
            val inserted = inventory.insertItem(0, item, true)
            if (inserted == null) {
                inventory.insertItem(0, item.copy(), false)
                i.setDead()
            } else if (inserted.stackSize != item.stackSize) {
                inventory.insertItem(0, item.copy(), false)
                item.stackSize = inserted.stackSize
            }
        }
    }

    fun craftItem() {
        val stack = inventory[0]!!
        val recipe = TableSieveRegistry.findRecipe(stack)
        output.add(recipe.primaryOutput)
        val extra = recipe.secondaryOutput
        if (extra != null && Random().nextFloat() < recipe.probability) {
            output.add(extra)
        }
        stack.stackSize--;
        if (stack.stackSize <= 0) {
            inventory.setStackInSlot(0, null)
        }
    }

    override fun save(): NBTTagCompound =
            NBTTagCompound().apply { setTag("inventory", inventory.serializeNBT()) }

    override fun load(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"))
    }

    override fun onBreak() {
        super.onBreak()
        if (!worldObj.isRemote) {
            if (inventory[0] != null) {
                dropItem(inventory[0]!!, pos)
            }
            for (i in output) {
                dropItem(i, pos)
            }
        }
    }
}