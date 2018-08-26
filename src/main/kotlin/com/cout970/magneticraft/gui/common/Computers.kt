package com.cout970.magneticraft.gui.common

import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.gui.common.core.DATA_ID_COMPUTER_BUTTON
import com.cout970.magneticraft.gui.common.core.DATA_ID_COMPUTER_LIGHT
import com.cout970.magneticraft.misc.gui.SlotTakeOnly
import com.cout970.magneticraft.misc.inventory.InventoryRegion
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.registry.FORGE_ENERGY
import com.cout970.magneticraft.registry.ITEM_FLOPPY_DISK
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.tileentity.TileComputer
import com.cout970.magneticraft.tileentity.TileMiningRobot
import com.cout970.magneticraft.util.vector.vec2Of
import net.minecraft.entity.player.EntityPlayer
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
        addSlotToContainer(object : SlotTakeOnly(tile.invModule.inventory, 0, 64, 233) {
            override fun canTakeStack(playerIn: EntityPlayer?): Boolean {
                return false
            }
        })
        inventoryRegions += InventoryRegion(0..0)
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
            when (it) {
                0 -> motherboard.start()
                1 -> motherboard.halt()
                2 -> motherboard.reset()
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
        tile.inventory.let {

            var index = 0
            (0..3).forEach { j ->
                (0..3).forEach { i ->
                    val x = i * 18 + 268
                    val y = j * 18 + 235
                    addSlotToContainer(SlotItemHandler(it, index, x, y))
                    index++
                }
            }
            inventoryRegions += InventoryRegion(0..15)
            // floppy disk
            addSlotToContainer(SlotItemHandler(it, 16, 35, 292))
            inventoryRegions += InventoryRegion(16..16, filter = { ITEM_FLOPPY_DISK!!.fromItem(it) != null })
            // battery slot
            addSlotToContainer(SlotItemHandler(it, 17, 12, 292))
            inventoryRegions += InventoryRegion(17..17, filter = { FORGE_ENERGY!!.fromItem(it) != null })
        }

        bindPlayerInventory(player.inventory, vec2Of(87, 151))
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
            when (it) {
                0 -> motherboard.start()
                1 -> motherboard.halt()
                2 -> motherboard.reset()
            }
        }
        keyboard.loadFromClient(ibd)
        super.receiveDataFromClient(ibd)
    }
}