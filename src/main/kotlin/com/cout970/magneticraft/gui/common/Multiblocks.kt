package com.cout970.magneticraft.gui.common

import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.gui.common.core.DATA_ID_SHELVING_UNIT_FILTER
import com.cout970.magneticraft.gui.common.core.DATA_ID_SHELVING_UNIT_LEVEL
import com.cout970.magneticraft.gui.common.core.DATA_ID_SHELVING_UNIT_SCROLL
import com.cout970.magneticraft.misc.gui.SlotShelvingUnit
import com.cout970.magneticraft.misc.inventory.InventoryRegion
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.tileentity.TileShelvingUnit
import com.cout970.magneticraft.tileentity.modules.ModuleShelvingUnitMb
import com.cout970.magneticraft.util.vector.vec2Of
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/08/10.
 */


class ContainerShelvingUnit(val tile: TileShelvingUnit, player: EntityPlayer, world: World, blockPos: BlockPos,
                            val level: ModuleShelvingUnitMb.Level) : ContainerBase(player, world, blockPos) {

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

    fun switchLevel(newLevel: ModuleShelvingUnitMb.Level) {
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
        if (filter.isEmpty() || filter.isBlank()) {
            switchLevel(currentLevel)
            return
        }
        updateCurrentSlots(allSlots.filter {
            it.stack.isNotEmpty && it.stack.displayName.contains(filter, ignoreCase = true)
        })
    }

    override fun receiveDataFromClient(ibd: IBD) {
        ibd.getFloat(DATA_ID_SHELVING_UNIT_SCROLL) { withScroll(it) }
        ibd.getString(DATA_ID_SHELVING_UNIT_FILTER) { filterSlots(it) }
        ibd.getInteger(DATA_ID_SHELVING_UNIT_LEVEL) { switchLevel(ModuleShelvingUnitMb.Level.values()[it]) }
        super.receiveDataFromClient(ibd)
    }

    fun setFilter(text: String) {
        filterSlots(text)
        sendUpdate(IBD().apply { setString(DATA_ID_SHELVING_UNIT_FILTER, text) })
    }
}