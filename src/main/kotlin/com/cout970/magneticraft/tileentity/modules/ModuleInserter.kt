package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.inventory.set
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.tileentity.TileConveyorBelt
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/06/20.
 */
class ModuleInserter(
        val facingGetter: () -> EnumFacing,
        val inventory: Inventory,
        override val name: String = "module_conveyor_belt"
) : IModule {

    override lateinit var container: IModuleContainer
    val facing get() = facingGetter()
    var state: State = State.IDLE
    var counter = 0
    var limit = 0

    override fun update() {
        if (world.isClient) return
        state = when (state) {
            State.IDLE -> {
                limit = 20
                counter = 0
                if (inventory[0].isEmpty) {
                    State.WAITING_FOR_PICK
                } else {
                    State.WAITING_FOR_DROP
                }
            }
            State.WAITING_FOR_PICK -> {
                if (counter < limit) {
                    counter++
                    State.WAITING_FOR_PICK
                } else {
                    State.PICKING
                }
            }
            State.PICKING -> {
                if (counter < limit) {
                    counter++
                    State.PICKING
                } else {
                    limit = 20
                    counter = 0
                    if (tryPick()) {
                        State.WAITING_FOR_DROP
                    } else {
                        State.PICKING
                    }
                }
            }
            State.WAITING_FOR_DROP -> {
                if (counter < limit) {
                    counter++
                    State.WAITING_FOR_DROP
                } else {
                    State.DROPPING
                }
            }
            State.DROPPING -> {
                if (counter < limit) {
                    counter++
                    State.DROPPING
                } else {
                    limit = 20
                    counter = 0
                    if (tryDrop()) {
                        State.WAITING_FOR_PICK
                    } else {
                        State.DROPPING
                    }
                }
            }
        }
    }

    fun tryPick(): Boolean {
        if (inventory[0].isNotEmpty) return true
        val backTile = world.getTileEntity(pos.offset(facing.opposite))
        if (backTile != null) {
            if (tryPickFromInv(backTile, facing)) {
                return true
            } else if (backTile is TileConveyorBelt) {
                val item = backTile.conveyorModule.removeItem()
                if (item.isNotEmpty) {
                    inventory[0] = item
                }
            }
        }

        val backDownTile = world.getTileEntity(pos.offset(facing.opposite).down())
        if (backDownTile != null) {
            if (tryPickFromInv(backDownTile, EnumFacing.UP)) {
                return true
            } else if (backDownTile is TileConveyorBelt) {
                val item = backDownTile.conveyorModule.removeItem()
                if (item.isNotEmpty) {
                    inventory[0] = item
                }
            }
        }

        return false
    }

    fun tryPickFromInv(tile: TileEntity, side: EnumFacing): Boolean {
        val inv = tile.getOrNull(ITEM_HANDLER, side) ?: return false

        for (index in 0 until inv.slots) {
            if (inv.getStackInSlot(index).isEmpty) continue

            val res = inv.extractItem(index, 64, true)
            if (res.isNotEmpty) {
                val item = inv.extractItem(index, 64, false)
                inventory[0] = item
                return true
            }
        }
        return false
    }

    fun tryDrop(): Boolean {
        if (inventory[0].isEmpty) return true
        val frontTile = world.getTileEntity(pos.offset(facing))
        if (frontTile != null) {
            if (tryDropToInv(frontTile, facing.opposite)) {
                return true
            } else if (frontTile is TileConveyorBelt) {
                val res = frontTile.conveyorModule.addItem(inventory[0], false)
                if (res) {
                    inventory[0] = ItemStack.EMPTY
                }
            }
        }

        val frontDownTile = world.getTileEntity(pos.offset(facing).down())
        if (frontDownTile != null) {
            if (tryDropToInv(frontDownTile, EnumFacing.UP)) {
                return true
            } else if (frontDownTile is TileConveyorBelt) {
                val res = frontDownTile.conveyorModule.addItem(inventory[0], false)
                if (res) {
                    inventory[0] = ItemStack.EMPTY
                }
            }
        }
        return false
    }

    fun tryDropToInv(tile: TileEntity, side: EnumFacing): Boolean {
        val inv = tile.getOrNull(ITEM_HANDLER, side) ?: return false

        for (index in 0 until inv.slots) {
            val res = inv.insertItem(index, inventory[0], true)
            if (res.isEmpty) {
                inv.insertItem(index, inventory[0], false)
                inventory[0] = ItemStack.EMPTY
                return true
            }
        }
        return false
    }

    enum class State {
        IDLE,
        WAITING_FOR_PICK,
        PICKING,
        WAITING_FOR_DROP,
        DROPPING
    }
}