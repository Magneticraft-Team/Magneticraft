package com.cout970.magneticraft.features.computers

import com.cout970.magneticraft.misc.inventory.InventoryRegion
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.registry.FORGE_ENERGY
import com.cout970.magneticraft.registry.ITEM_FLOPPY_DISK
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.systems.gui.DATA_ID_COMPUTER_BUTTON
import com.cout970.magneticraft.systems.gui.DATA_ID_COMPUTER_LIGHT
import com.cout970.magneticraft.systems.gui.containers.ContainerBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by cout970 on 2017/08/10.
 */

class ContainerComputer(val tile: TileComputer, player: EntityPlayer, world: World, blockPos: BlockPos) : ContainerBase(
    player, world, blockPos) {

    val motherboard = tile.computerModule.motherboard
    val monitor = tile.monitor
    val keyboard = tile.keyboard

    init {
        addSlotToContainer(SlotItemHandler(tile.invModule.inventory, 0, 35, 225))
        inventoryRegions += InventoryRegion(0..0)

        val hotBarIndex = inventorySlots.size
        repeat(9) { i ->
            addSlotToContainer(Slot(player.inventory, i, i * 18 + 95, 226))
        }
        inventoryRegions += InventoryRegion(hotBarIndex..hotBarIndex + 8)
    }

    override fun sendDataToClient(): IBD {
        val ibd = super.sendDataToClient() ?: IBD()
        monitor.saveToClient(ibd)
        ibd.setBoolean(DATA_ID_COMPUTER_LIGHT, motherboard.isOnline())
        return ibd
    }

    override fun sendDataToServer(): IBD {
        val ibd = IBD()
        keyboard.saveToServer(ibd)
        return ibd
    }

    override fun receiveDataFromServer(ibd: IBD) {
        monitor.loadFromServer(ibd)
        ibd.getBoolean(DATA_ID_COMPUTER_LIGHT) {
            if (it) motherboard.start() else motherboard.halt()
        }
        super.receiveDataFromServer(ibd)
    }

    override fun receiveDataFromClient(ibd: IBD) {
        ibd.getInteger(DATA_ID_COMPUTER_BUTTON) {
            if (motherboard.isOnline) {
                motherboard.halt()
            } else {
                motherboard.halt()
                motherboard.reset()
                motherboard.start()
            }
        }
        keyboard.loadFromClient(ibd)
        super.receiveDataFromClient(ibd)
    }
}

class ContainerMiningRobot(val tile: TileMiningRobot, player: EntityPlayer, world: World,
                           blockPos: BlockPos) : ContainerBase(
    player, world, blockPos) {

    val motherboard = tile.computerModule.motherboard
    val monitor = tile.monitor
    val keyboard = tile.keyboard

    init {
        tile.inventory.let { inv ->

            var index = 0
            (0..3).forEach { j ->
                (0..3).forEach { i ->
                    val x = i * 18 + 268
                    val y = j * 18 + 226
                    addSlotToContainer(SlotItemHandler(inv, index, x, y))
                    index++
                }
            }
            inventoryRegions += InventoryRegion(0..15)

            // floppy disk
            addSlotToContainer(SlotItemHandler(inv, 16, 69, 263))
            inventoryRegions += InventoryRegion(16..16, filter = { ITEM_FLOPPY_DISK!!.fromItem(it) != null })

            // battery slot
            addSlotToContainer(SlotItemHandler(inv, 17, 69, 284))
            inventoryRegions += InventoryRegion(17..17, filter = { FORGE_ENERGY!!.fromItem(it) != null })
        }

        bindPlayerInventory(player.inventory, vec2Of(87, 142))
    }

    override fun sendDataToClient(): IBD {
        val ibd = super.sendDataToClient() ?: IBD()
        monitor.saveToClient(ibd)
        ibd.setBoolean(DATA_ID_COMPUTER_LIGHT, motherboard.isOnline)
        return ibd
    }

    override fun sendDataToServer(): IBD {
        val ibd = IBD()
        keyboard.saveToServer(ibd)
        return ibd
    }

    override fun receiveDataFromServer(ibd: IBD) {
        monitor.loadFromServer(ibd)
        ibd.getBoolean(DATA_ID_COMPUTER_LIGHT) {
            if (it) motherboard.start() else motherboard.halt()
        }
        super.receiveDataFromServer(ibd)
    }

    override fun receiveDataFromClient(ibd: IBD) {
        ibd.getInteger(DATA_ID_COMPUTER_BUTTON) {
            if (motherboard.isOnline) {
                motherboard.halt()
            } else {
                motherboard.halt()
                motherboard.reset()
                motherboard.start()
            }
        }
        keyboard.loadFromClient(ibd)
        super.receiveDataFromClient(ibd)
    }
}