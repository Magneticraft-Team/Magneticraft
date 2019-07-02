package com.cout970.magneticraft.features.multiblocks

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.HydraulicPressMode
import com.cout970.magneticraft.features.multiblocks.tileentities.*
import com.cout970.magneticraft.misc.gui.SlotShelvingUnit
import com.cout970.magneticraft.misc.gui.SlotTakeOnly
import com.cout970.magneticraft.misc.gui.SlotUnmodifiableItemHandler
import com.cout970.magneticraft.misc.gui.SlotUnmodifiableItemHandlerTakeOnly
import com.cout970.magneticraft.misc.inventory.InventoryRegion
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.*
import com.cout970.magneticraft.systems.gui.components.buttons.AbstractButton
import com.cout970.magneticraft.systems.gui.containers.ContainerBase
import com.cout970.magneticraft.systems.tilemodules.ModuleShelvingUnitMb
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by cout970 on 2017/08/10.
 */


class ContainerShelvingUnit(val tile: TileShelvingUnit, player: EntityPlayer, world: World, blockPos: BlockPos,
                            val level: ModuleShelvingUnitMb.Level) : ContainerBase(player, world, blockPos) {

    val allSlots: List<SlotShelvingUnit>
    var currentSlots = emptyList<SlotShelvingUnit>()
    var scroll = 0f
    var currentLevel = level
    var filterText: String = ""

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

    fun updateCurrentSlots(list: List<SlotShelvingUnit>) {
        allSlots.forEach { it.hide(); it.lock() }
        currentSlots = list
        withScroll(0f)
    }

    fun switchLevel(newLevel: ModuleShelvingUnitMb.Level) {
        currentLevel = newLevel
        val available = tile.shelvingUnitModule.getAvailableSlots(currentLevel)
        updateCurrentSlots(allSlots.filterIndexed { index, _ -> index in available })
    }

    fun withScroll(scrollLevel: Float) {
        scroll = scrollLevel
        currentSlots.forEach { it.hide(); it.lock() }
        val column = Math.max(0, Math.round(scroll * ((currentSlots.size / 9f) - 5)))
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
        filterText = filter
        if (filter.isEmpty() || filter.isBlank()) {
            switchLevel(currentLevel)
            return
        }
        updateCurrentSlots(allSlots.filter {
            it.stack.isNotEmpty && it.stack.displayName.contains(filter, ignoreCase = true)
        })
    }

    fun serverFilterSlots(slots: IntArray) {
        if (slots.isEmpty()) {
            switchLevel(currentLevel)
            return
        }
        updateCurrentSlots(allSlots.filter {
            it.stack.isNotEmpty && it.slotNumber in slots
        })
    }

    override fun receiveDataFromClient(ibd: IBD) {
        ibd.getBoolean(DATA_ID_SHELVING_UNIT_SORT) { tile.shelvingUnitModule.sortStacks(it) }
        ibd.getInteger(DATA_ID_SHELVING_UNIT_LEVEL) { switchLevel(ModuleShelvingUnitMb.Level.values()[it]) }
        ibd.getFloat(DATA_ID_SHELVING_UNIT_SCROLL) { withScroll(it) }
        ibd.getIntArray(DATA_ID_SHELVING_UNIT_FILTER) { serverFilterSlots(it) }
    }

    fun setFilter(text: String) {
        filterSlots(text)
        sendUpdate(IBD().apply {
            setIntArray(DATA_ID_SHELVING_UNIT_FILTER, currentSlots.map { it.slotNumber }.toIntArray())
        })
    }
}

class ContainerGrinder(val tile: TileGrinder, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        tile.invModule.inventory.let { inv ->
            addSlotToContainer(SlotItemHandler(inv, 0, 96, 16))
            addSlotToContainer(SlotTakeOnly(inv, 1, 85, 48))
            addSlotToContainer(SlotTakeOnly(inv, 2, 107, 48))

            inventoryRegions += InventoryRegion(0..0, filter = {
                MagneticraftApi.getGrinderRecipeManager().findRecipe(it) != null
            })
            inventoryRegions += InventoryRegion(1..1, filter = { false })
            inventoryRegions += InventoryRegion(2..2, filter = { false })
        }
        bindPlayerInventory(player.inventory)
    }
}

class ContainerSieve(val tile: TileSieve, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        tile.invModule.inventory.let { inv ->
            addSlotToContainer(SlotItemHandler(inv, 0, 96, 16))
            addSlotToContainer(SlotTakeOnly(inv, 1, 74, 48))
            addSlotToContainer(SlotTakeOnly(inv, 2, 96, 48))
            addSlotToContainer(SlotTakeOnly(inv, 3, 118, 48))

            inventoryRegions += InventoryRegion(0..0, filter = {
                MagneticraftApi.getSieveRecipeManager().findRecipe(it) != null
            })
            inventoryRegions += InventoryRegion(1..1, filter = { false })
            inventoryRegions += InventoryRegion(2..2, filter = { false })
        }
        bindPlayerInventory(player.inventory)
    }
}

class ContainerSolarTower(val tile: TileSolarTower, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        bindPlayerInventory(player.inventory)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClick(button: AbstractButton, mouse: Vec2d, mouseButton: Int): Boolean {
        sendUpdate(IBD().apply { this.setBoolean(button.id, true) })
        return true
    }

    override fun receiveDataFromClient(ibd: IBD) {
        ibd.getBoolean(0) {
            tile.solarTowerModule.searchMirrors = true
        }
    }
}

class ContainerContainer(val tile: TileContainer, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        val inv = tile.stackInventoryModule.getGuiInventory()
        addSlotToContainer(SlotUnmodifiableItemHandler(inv, 0, 85, 16))
        addSlotToContainer(SlotUnmodifiableItemHandlerTakeOnly(inv, 1, 85, 48))

        inventoryRegions += InventoryRegion(0..0, advFilter = { _, i -> i != 1 })
        inventoryRegions += InventoryRegion(1..1, advFilter = { _, _ -> false })

        bindPlayerInventory(player.inventory)
    }
}

class ContainerPumpjack(val tile: TilePumpjack, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        bindPlayerInventory(player.inventory)
    }
}

class ContainerHydraulicPress(val tile: TileHydraulicPress, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        tile.invModule.inventory.let { inv ->
            addSlotToContainer(SlotItemHandler(inv, 0, 97, 16))
            addSlotToContainer(SlotTakeOnly(inv, 1, 97, 48))

            inventoryRegions += InventoryRegion(0..0, filter = {
                MagneticraftApi.getHydraulicPressRecipeManager().findRecipe(it, tile.hydraulicPressModule.mode) != null
            })
            inventoryRegions += InventoryRegion(1..1, filter = { false })
        }
        bindPlayerInventory(player.inventory)
    }

    override fun receiveDataFromClient(ibd: IBD) {
        ibd.getInteger(DATA_ID_SELECTED_OPTION) {
            tile.hydraulicPressModule.mode = HydraulicPressMode.values()[it]
        }
    }
}

class ContainerOilHeater(val tile: TileOilHeater, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        bindPlayerInventory(player.inventory)
    }
}

class ContainerRefinery(val tile: TileRefinery, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        bindPlayerInventory(player.inventory)
    }
}

class ContainerSteamEngine(val tile: TileSteamEngine, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        bindPlayerInventory(player.inventory)
    }
}

class ContainerBigCombustionChamber(val tile: TileBigCombustionChamber, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        tile.invModule.inventory.let { inv ->
            addSlotToContainer(SlotItemHandler(inv, 0, 99, 33))
            inventoryRegions += InventoryRegion(0..0)
        }
        bindPlayerInventory(player.inventory)
    }
}