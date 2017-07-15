package com.cout970.magneticraft.gui.common

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.misc.gui.SlotTakeOnly
import com.cout970.magneticraft.misc.inventory.InventoryRegion
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.registry.FORGE_ENERGY
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.tileentity.*
import com.cout970.magneticraft.tileentity.modules.ModuleShelvingUnit
import com.cout970.magneticraft.util.vector.vec2Of
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by cout970 on 2017/06/12.
 */

class ContainerBox(val tile: TileBox, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        tile.invModule.inventory.let { inv ->
            for (index in 0 until inv.slots) {
                val pos = getPosFromIndex(index)
                addSlotToContainer(SlotItemHandler(inv, index, pos.xi, pos.yi))
            }
            inventoryRegions += InventoryRegion(0..26)
        }
        bindPlayerInventory(player.inventory)
    }

    private fun getPosFromIndex(index: Int): IVector2 {
        return vec2Of(index % 9, index / 9) * 18 + vec2Of(8, 8)
    }
}

class ContainerBattery(val tile: TileBattery, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        tile.invModule.inventory.let { inv ->
            addSlotToContainer(SlotItemHandler(inv, 0, 102, 48))
            addSlotToContainer(SlotItemHandler(inv, 1, 102, 16))

            inventoryRegions += InventoryRegion(0..0, filter = { FORGE_ENERGY!!.fromItem(it)?.canReceive() ?: false })
            inventoryRegions += InventoryRegion(1..1, filter = { FORGE_ENERGY!!.fromItem(it)?.canExtract() ?: false })
        }
        bindPlayerInventory(player.inventory)
    }
}

class ContainerElectricFurnace(tile: TileElectricFurnace, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        tile.invModule.inventory.let { inv ->
            addSlotToContainer(SlotItemHandler(inv, 0, 91, 16))
            addSlotToContainer(SlotTakeOnly(inv, 1, 91, 48))

            inventoryRegions += InventoryRegion(0..0,
                    filter = { FurnaceRecipes.instance().getSmeltingResult(it).isNotEmpty })
            inventoryRegions += InventoryRegion(1..1, filter = { false })
        }
        bindPlayerInventory(player.inventory)
    }
}

class ContainerShelvingUnit(val tile: TileShelvingUnit, player: EntityPlayer, world: World, blockPos: BlockPos,
                            val level: ModuleShelvingUnit.Level)
    : ContainerBase(player, world, blockPos) {

    init {
        tile.invModule.inventory.let { inv ->
            val slots = tile.shelvingUnitModule.getAvaliableSlots(level)
            slots.take(5 * 9).forEach {
                val index = it - slots.start
                val x = index % 9 * 18 + 8
                val y = index / 9 * 18 + 21
                addSlotToContainer(SlotItemHandler(inv, it, x, y))
            }
        }
        bindPlayerInventory(player.inventory, offset = vec2Of(0, 41))
    }
}

class ContainerComputer(val tile: TileComputer, player: EntityPlayer, world: World, blockPos: BlockPos) : ContainerBase(
        player, world, blockPos) {

    val motherboard = tile.computerModule.motherboard
    val monitor = tile.monitorModule.monitor

    init {
        addSlotToContainer(SlotTakeOnly(tile.invModule.inventory, 0, 64, 233))
    }

    override fun transferStackInSlot(playerIn: EntityPlayer?, index: Int): ItemStack? = null

    override fun sendDataToClient(): IBD {
        val ibd = super.sendDataToClient() ?: IBD()
        monitor.saveToClient(ibd)
        ibd.setBoolean(3, motherboard.isOnline())
        return ibd
    }

    override fun sendDataToServer(): IBD {
        val ibd = IBD()
        monitor.saveToServer(ibd)
        return ibd
    }

    override fun receiveDataFromServer(ibd: IBD) {
        monitor.loadFromServer(ibd)
        ibd.getBoolean(3) {
            if (it) motherboard.start() else motherboard.halt()
        }
        super.receiveDataFromServer(ibd)
    }

    override fun receiveDataFromClient(ibd: IBD) {
        ibd.getInteger(50) {
            when (it) {
                0 -> motherboard.start()
                1 -> motherboard.halt()
                2 -> motherboard.reset()
            }
        }
        monitor.loadFromClient(ibd)
        super.receiveDataFromClient(ibd)
    }
}

class ContainerCombustionChamber(val tile: TileCombustionChamber, player: EntityPlayer, world: World,
                                 blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        tile.invModule.inventory.let { inv ->
            addSlotToContainer(SlotItemHandler(inv, 0, 91, 32))
            inventoryRegions += InventoryRegion(0..0, filter = { it.isNotEmpty && it.item == Items.COAL })
        }
        bindPlayerInventory(player.inventory)
    }
}