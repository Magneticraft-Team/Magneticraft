package com.cout970.magneticraft.features.multiblocks

import com.cout970.magneticraft.features.multiblocks.tileentities.TileShelvingUnit
import com.cout970.magneticraft.misc.gui.SlotShelvingUnit
import com.cout970.magneticraft.misc.inventory.InventoryRegion
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.*
import com.cout970.magneticraft.systems.integration.IntegrationHandler
import com.cout970.magneticraft.systems.integration.jei.MagneticraftPlugin
import com.cout970.magneticraft.systems.tilemodules.ModuleShelvingUnitMb
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/08/10.
 */


class ContainerShelvingUnit(builder: GuiBuilder, configFunc: (AutoContainer) -> Unit, player: EntityPlayer, world: World, pos: BlockPos)
    : AutoContainer(builder, configFunc, player, world, pos) {

    val tile: TileShelvingUnit = tileEntity as TileShelvingUnit

    val allSlots: List<SlotShelvingUnit>
    var currentSlots = emptyList<SlotShelvingUnit>()
    var currentLevel = tile.shelvingUnitModule.guiLevel

    init {
        val inv = tile.invModule.inventory

        allSlots = (0 until inv.slots).map {
            val x = it % 9 * 18 + 8
            val y = it / 9 * 18 + 21
            SlotShelvingUnit(inv, it, x, y)
        }

        allSlots.forEach { addSlotToContainer(it) }
        bindPlayerInventory(player.inventory, vec2Of(0, 41))
    }

    override fun postInit() {
        switchLevel(currentLevel)
    }

    fun updateCurrentSlots(list: List<SlotShelvingUnit>) {
        allSlots.forEach { it.hide(); it.lock() }
        currentSlots = list
        withScroll(0f)
    }

    fun levelButton(id: Int) {
        val ibd = IBD().apply {
            setInteger(DATA_ID_SHELVING_UNIT_LEVEL, id)
            setString(DATA_ID_SHELVING_UNIT_FILTER, "")
        }
        sendUpdate(ibd)
        filterSlots("")
        switchLevel(ModuleShelvingUnitMb.Level.values()[id])
    }

    fun switchLevel(newLevel: ModuleShelvingUnitMb.Level) {
        currentLevel = newLevel
        val available = tile.shelvingUnitModule.getAvailableSlots(currentLevel)
        updateCurrentSlots(allSlots.filterIndexed { index, _ -> index in available })
    }

    fun withScroll(scrollLevel: Float) {
        currentSlots.forEach { it.hide(); it.lock() }
        val column = Math.max(0, Math.round(scrollLevel * ((currentSlots.size / 9f) - 5)))
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

        fun matches(stack: ItemStack, filter: String): Boolean {
            if (stack.isEmpty) return false

            if (filter.startsWith('@')) {
                val mod = filter.substring(1)
                return stack.item.registryName!!.resourceDomain.contains(mod, ignoreCase = true)
            }

            return stack.displayName.contains(filter, ignoreCase = true)
        }

        updateCurrentSlots(allSlots.filter { matches(it.stack, filter) })
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

    fun setFilter(text: String) {
        if (IntegrationHandler.jei && Config.syncShelvingUnitSearchWithJei) {
            setJeiFilter(text)
        }
        filterSlots(text)
        sendUpdate(IBD().apply {
            setIntArray(DATA_ID_SHELVING_UNIT_FILTER, currentSlots.map { it.slotNumber }.toIntArray())
        })
    }

    fun setJeiFilter(text: String) {
        MagneticraftPlugin.INSTANCE.getSearchBar().filterText = text
    }

    override fun receiveDataFromClient(ibd: IBD) {
        ibd.getBoolean(DATA_ID_SHELVING_UNIT_SORT) { tile.shelvingUnitModule.sortStacks() }
        ibd.getInteger(DATA_ID_SHELVING_UNIT_LEVEL) { switchLevel(ModuleShelvingUnitMb.Level.values()[it]) }
        ibd.getFloat(DATA_ID_SHELVING_UNIT_SCROLL) { withScroll(it) }
        ibd.getIntArray(DATA_ID_SHELVING_UNIT_FILTER) { serverFilterSlots(it) }
    }
}
