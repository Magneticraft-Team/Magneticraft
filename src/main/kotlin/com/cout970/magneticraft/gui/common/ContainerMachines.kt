package com.cout970.magneticraft.gui.common

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.misc.gui.SlotShelvingUnit
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
            addSlotToContainer(SlotItemHandler(inv, 0, 102, 16))
            addSlotToContainer(SlotItemHandler(inv, 1, 102, 48))

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
                            val level: ModuleShelvingUnit.Level) : ContainerBase(player, world, blockPos) {

    val allSlots: List<SlotShelvingUnit>
    var currentSlots = emptyList<SlotShelvingUnit>()
    var scroll = 0f
    var currentLevel = level

    init {
        val inv = tile.invModule.inventory

        allSlots = (0 until inv.slots).map {
            val x = it % 9 * 18 + 8
            val y = it / 9 * 18 + 21
            SlotShelvingUnit(inv, it, x, y)
        }

        allSlots.forEach { addSlotToContainer(it) }
        bindPlayerInventory(player.inventory, offset = vec2Of(0, 41))
        switchLevel(level)
    }

    fun switchLevel(newLevel: ModuleShelvingUnit.Level) {
        currentLevel = newLevel
        val available = tile.shelvingUnitModule.getAvailableSlots(currentLevel)
        updateCurrentSlots(allSlots.filterIndexed { index, _ -> index in available })
    }

    fun updateCurrentSlots(list: List<SlotShelvingUnit>) {
        allSlots.forEach { it.hide(); it.lock() }
        currentSlots = list
        withScroll(0f)
    }

    fun withScroll(scrollLevel: Float) {
        scroll = scrollLevel
        currentSlots.forEach { it.hide(); it.lock() }
        val column = Math.round(scroll * ((currentSlots.size / 9f) - 5))
        var min = Int.MAX_VALUE
        var max = Int.MIN_VALUE
        currentSlots.forEachIndexed { index, it ->
            val pos = index - column * 9
            it.unlock()
            if ((pos) >= 0 && (pos) < 5 * 9) {
                it.show()
                it.xPos = pos % 9 * 18 + 8
                it.yPos = pos / 9 * 18 + 21
            }
            if (it.slotIndex > max) max = it.slotIndex
            if (it.slotIndex < min) min = it.slotIndex
        }
        inventoryRegions.clear()
        inventoryRegions += InventoryRegion(min..max)
        inventoryRegions += InventoryRegion(648..674)
        inventoryRegions += InventoryRegion(675..683)
    }

    fun filterSlots(filter: String) {
        updateCurrentSlots(allSlots.filter {
            it.stack.isNotEmpty && it.stack.displayName.contains(filter)
        })
    }

    override fun sendDataToServer(): IBD? {
        return IBD().apply { setFloat(0, scroll) }
    }

    override fun receiveDataFromClient(ibd: IBD) {
        ibd.getFloat(0) { withScroll(it) }
        super.receiveDataFromClient(ibd)
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