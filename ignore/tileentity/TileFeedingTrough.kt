package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.block.BlockFeedingTrough
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.toAABBWith
import com.cout970.magneticraft.util.vector.toVec3d
import com.mojang.authlib.GameProfile
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.world.WorldServer
import net.minecraftforge.common.util.FakePlayerFactory
import net.minecraftforge.items.ItemStackHandler
import java.util.*

/**
 * Created by cout970 on 24/06/2016.
 */
@TileRegister("feeding_trough")
class TileFeedingTrough : TileBase(), ITickable {
    val ACCEPTED_ITEMS: List<Item> = listOf(Items.WHEAT, Items.CARROT, Items.WHEAT_SEEDS)
    val MAX_ANIMALS = 30
    val FAKE_PLAYER_UUID = UUID.fromString("d0f15bc8-6eb3-4a1b-8b5d-d3fdf5140321")!!
    val FAKE_PROFILE = GameProfile(FAKE_PLAYER_UUID, "FeedingTrough")
    var WAIT_TIME = 400
    val inventory = ItemStackHandler()

    override fun update() {
        if (worldObj.isServer) {
            if (shouldTick(200)) {
                sendUpdateToNearPlayers()
            }
            if (shouldTick(WAIT_TIME) && inventory[0] != null) {
                //getting the bounding box to search animals
                var start = pos.toVec3d().addVector(-3.5, -1.0, -3.5)
                var end = pos.toVec3d().addVector(4.5, 2.0, 4.5)
                val dir = worldObj.getBlockState(pos).getValue(BlockFeedingTrough.FEEDING_TROUGH_SIDE_POSITION)
                if (dir.axisDirection == EnumFacing.AxisDirection.POSITIVE) {
                    end += dir.directionVec.toVec3d()
                } else {
                    start += dir.directionVec.toVec3d()
                }
                val box = start toAABBWith end
                //getting the animals
                val totalAnimals = worldObj.getEntitiesInAABBexcluding(null, box, { it is EntityAnimal })
                val validAnimals = totalAnimals.map { it as EntityAnimal }
                        .filter { !it.isInLove && !it.isChild && it.isBreedingItem(inventory[0]) }.toMutableList()

                if (validAnimals.size >= 2 && totalAnimals.size < MAX_ANIMALS) {
                    for (i in 0..1) {
                        val index = Random().nextInt(validAnimals.size)
                        val animal = validAnimals[index]
                        validAnimals.removeAt(index)
                        inventory.extractItem(0, 1, false)
                        sendUpdateToNearPlayers()

                        //applying love =)
                        animal.setInLove(FakePlayerFactory.get(worldObj as WorldServer, FAKE_PROFILE))
                    }
                }
            }
        }
    }

    fun insertItem(item: ItemStack?): ItemStack? {
        if (item != null && ACCEPTED_ITEMS.contains(item.item)) {
            return inventory.insertItem(0, item, false)
        }
        return item
    }

    fun extractItem(): ItemStack? {
        return inventory.extractItem(0, 64, false)
    }

    override fun save(): NBTTagCompound {
        val nbt = newNbt {
            add("inventory", inventory.serializeNBT())
        }
        return super.save().also { it.merge(nbt) }
    }

    override fun load(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"))
        super.load(nbt)
    }

    override fun onBreak() {
        super.onBreak()
        if (worldObj.isServer) {
            if (inventory[0] != null) {
                dropItem(inventory[0]!!, pos)
            }
        }
    }
}