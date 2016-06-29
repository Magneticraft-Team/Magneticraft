package com.cout970.magneticraft.tileentity

import coffee.cypher.mcextlib.extensions.aabb.to
import coffee.cypher.mcextlib.extensions.inventories.get
import coffee.cypher.mcextlib.extensions.vectors.plus
import coffee.cypher.mcextlib.extensions.vectors.toDoubleVec
import com.cout970.magneticraft.block.FEEDING_TROUGH_SIDE_POSITION
import com.mojang.authlib.GameProfile
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.init.Items
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
class TileFeedingTrough : TileBase(), ITickable {
    val ACCEPTED_ITEMS = listOf(Items.WHEAT, Items.CARROT, Items.WHEAT_SEEDS)
    val MAX_ANIMALS = 30
    val FAKE_PLAYER_UUID = UUID.fromString("d0f15bc8-6eb3-4a1b-8b5d-d3fdf5140321")
    val FAKE_PROFILE = GameProfile(FAKE_PLAYER_UUID, "FeedingTrough")
    var WAIT_TIME = 600
    val inventory = ItemStackHandler()

    override fun update() {
        if (!worldObj.isRemote) {
            if ((worldObj.totalWorldTime + pos.hashCode()) % WAIT_TIME == 0L && inventory[0] != null) {
                //getting the bounding box to search animals
                var start = pos.toDoubleVec().addVector(-3.5, -1.0, -3.5)
                var end = pos.toDoubleVec().addVector(4.5, 2.0, 4.5)
                val dir = worldObj.getBlockState(pos).getValue(FEEDING_TROUGH_SIDE_POSITION)
                if (dir.axisDirection == EnumFacing.AxisDirection.POSITIVE) {
                    end += dir.directionVec.toDoubleVec();
                } else {
                    start += dir.directionVec.toDoubleVec()
                }
                val box = start to end
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

                        //applying love =)
                        animal.setInLove(FakePlayerFactory.get(worldObj as WorldServer, FAKE_PROFILE))
                    }
                }
            }
        }
    }

    fun insetItem(item: ItemStack?): ItemStack? {
        if (item != null && ACCEPTED_ITEMS.contains(item.item)) {
            return inventory.insertItem(0, item, false)
        }
        return item
    }

    fun extractItem(): ItemStack? {
        return inventory.extractItem(0, 64, false)
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        inventory.deserializeNBT(nbt?.getCompoundTag("inventory"))
        super.deserializeNBT(nbt)
    }

    override fun serializeNBT(): NBTTagCompound? {
        val nbt = super.serializeNBT()
        nbt.setTag("inventory", inventory.serializeNBT())
        return nbt
    }

    override fun onBreak() {
        super.onBreak()
        if (!worldObj.isRemote) {
            if (inventory[0] != null) {
                dropItem(inventory[0]!!, pos)
            }
        }
    }
}