package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.vector.createAABBUsing
import com.cout970.magneticraft.misc.vector.plus
import com.cout970.magneticraft.misc.vector.toVec3d
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.systems.blocks.CommonMethods
import com.cout970.magneticraft.systems.blocks.IOnActivated
import com.cout970.magneticraft.systems.blocks.OnActivatedArgs
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import com.mojang.authlib.GameProfile
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.world.WorldServer
import net.minecraftforge.common.util.FakePlayerFactory
import java.util.*

/**
 * Created by cout970 on 2017/08/10.
 */

class ModuleFeedingTrough(
    val inventory: Inventory,
    override val name: String = "module_feeding_trough"
) : IModule, IOnActivated {

    companion object {
        val ACCEPTED_ITEMS: List<Item> = listOf(Items.WHEAT, Items.CARROT, Items.WHEAT_SEEDS)
        val MAX_ANIMALS = 30
        val FAKE_PLAYER_UUID = UUID.fromString("d0f15bc8-6eb3-4a1b-8b5d-d3fdf5140321")!!
        val FAKE_PROFILE = GameProfile(FAKE_PLAYER_UUID, "FeedingTrough")
        var WAIT_TIME = 400
    }

    override lateinit var container: IModuleContainer

    override fun update() {
        if (world.isServer) {
            if (container.shouldTick(200)) {
                container.sendUpdateToNearPlayers()
            }
            if (container.shouldTick(WAIT_TIME) && inventory[0].isNotEmpty) {
                //getting the bounding box to search animals
                var start = pos.toVec3d().addVector(-3.5, -1.0, -3.5)
                var end = pos.toVec3d().addVector(4.5, 2.0, 4.5)
                val dir = world.getBlockState(pos)[CommonMethods.PROPERTY_CENTER_ORIENTATION] ?: return
                if (dir.facing.axisDirection == EnumFacing.AxisDirection.POSITIVE) {
                    end += dir.facing.directionVec.toVec3d()
                } else {
                    start += dir.facing.directionVec.toVec3d()
                }
                val box = start createAABBUsing end
                //getting the animals
                val totalAnimals = world.getEntitiesInAABBexcluding(null, box, { it is EntityAnimal })
                val validAnimals = totalAnimals.map { it as EntityAnimal }
                    .filter {
                        !it.isInLove && it.growingAge == 0 && it.isBreedingItem(inventory[0])
                    }.toMutableList()

                if (validAnimals.size >= 2 && totalAnimals.size < MAX_ANIMALS) {
                    for (i in 0..1) {
                        val index = Random().nextInt(validAnimals.size)
                        val animal = validAnimals[index]
                        validAnimals.removeAt(index)
                        inventory.extractItem(0, 1, false)
                        container.sendUpdateToNearPlayers()

                        //applying love =)
                        animal.setInLove(FakePlayerFactory.get(world as WorldServer, FAKE_PROFILE))
                    }
                }
            }
        }
    }

    override fun onActivated(args: OnActivatedArgs): Boolean {

        return if (args.heldItem.isNotEmpty) {
            val result = insertItem(args.heldItem)
            args.playerIn.setHeldItem(args.hand, result)
            true
        } else {
            val result = extractItem()
            args.playerIn.setHeldItem(args.hand, result)
            true
        }
    }

    fun insertItem(item: ItemStack): ItemStack {
        if (item.isNotEmpty && ACCEPTED_ITEMS.contains(item.item)) {
            return inventory.insertItem(0, item, false)
        }
        return item
    }

    fun extractItem(): ItemStack {
        return inventory.extractItem(0, 64, false)
    }
}