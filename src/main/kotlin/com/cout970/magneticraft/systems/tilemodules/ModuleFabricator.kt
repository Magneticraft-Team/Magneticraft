package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.misc.inventory.*
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.set
import com.cout970.magneticraft.misc.tileentity.getCap
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.dropItem
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.item.crafting.IRecipe
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.IItemHandler

class ModuleFabricator(
    val inventory: Inventory,
    override val name: String = "module_fabricator"
) : IModule {

    override lateinit var container: IModuleContainer
    val recipeGrid = InventoryCrafting(object : Container() {
        override fun canInteractWith(playerIn: EntityPlayer): Boolean = true

        override fun onCraftMatrixChanged(inventoryIn: IInventory) {
            if (!ignoreGridUpdate) {
                resetMatches()
                craftRecipe = null
            }
        }
    }, 3, 3)
    val craftingResult = Inventory(1)

    var ignoreGridUpdate = false
    var craftRecipe: IRecipe? = null
    var itemMatches: BooleanArray? = null
    var toCraft = false
    val craftingResources = mutableListOf<InventorySlotLocation>()

    init {
        inventory.onContentsChanges = { _, _ -> resetMatches() }
    }

    fun resetMatches() {
        if (world.isClient) return
        itemMatches = null
    }

    override fun update() {
        if (world.isClient) return

        if (itemMatches == null || (craftRecipe != null && container.shouldTick(10))) {
            calculateItemMatches()
            container.sendUpdateToNearPlayers()
        }

        if (toCraft) {
            toCraft = false
            craftItem()
        }
    }

    fun clearGrid() {
        repeat(9) {
            recipeGrid.setInventorySlotContents(it, ItemStack.EMPTY)
        }
    }

    fun requestItemCraft() {
        if (craftRecipe == null || itemMatches == null || itemMatches!!.any { !it }) return
        toCraft = true
    }

    fun craftItem(): Boolean {
        val recipe = craftRecipe
        val matches = itemMatches
        if (matches == null || recipe == null || matches.any { !it }) return false

        for (slot in craftingResources) {
            val extracted = slot.inventory.extractItem(slot.index, slot.amount, true)
            if (extracted.isEmpty || extracted.count != slot.amount) {
                return false
            }
        }

        for (slot in craftingResources) {
            val stack = slot.inventory.extractItem(slot.index, slot.amount, false)
            val container = stack.item.getContainerItem(stack)

            if (container.isNotEmpty && slot.inventory[slot.index].isEmpty) {
                slot.inventory.insertItem(slot.index, container, false)
            }
        }

        val result = craftingResult[0].copy()
        val excess = inventory.insertItem(result, false)
        if (excess.isNotEmpty) {
            world.dropItem(excess, pos.up())
        }

        resetMatches()
        return true
    }

    fun calculateItemMatches() {
        itemMatches = booleanArrayOf(false, false, false, false, false, false, false, false, false)
        if (craftRecipe == null) {
            refreshRecipe()
            if (craftRecipe == null) return
        }

        val matches = itemMatches ?: return
        craftingResources.clear()

        for (i in 0..8) {
            val item = recipeGrid.getStackInSlot(i)
            if (item.isEmpty) {
                matches[i] = true
                continue
            }

            if (searchItemInInventory(i, inventory, craftingResources)) {
                matches[i] = true
                continue
            }

            for (side in EnumFacing.VALUES) {
                val inv = world.getCap(ITEM_HANDLER, pos.offset(side), side.opposite) ?: continue

                if (searchItemInInventory(i, inv, craftingResources)) {
                    matches[i] = true
                    continue
                }
            }
        }
    }

    fun searchItemInInventory(currentSlot: Int, inv: IItemHandler, slots: MutableList<InventorySlotLocation>): Boolean {
        for (slot in 0 until inv.slots) {
            val content = inv[slot]
            if (content.isEmpty) continue

            if (checkInGrid(content, currentSlot)) {
                val oldSlot = slots.find { invSlot ->
                    invSlot.index == slot && invSlot.inventory == inv && ApiUtils.equalsIgnoreSize(invSlot.content, content)
                }

                if (oldSlot == null) {
                    slots.add(InventorySlotLocation(content, slot, 1, inv))
                    return true

                } else if (content.count >= oldSlot.amount + 1) {
                    oldSlot.amount++
                    return true
                }
            }
        }

        return false
    }

    fun checkInGrid(newItem: ItemStack, currentSlot: Int): Boolean {
        val recipe = craftRecipe ?: return false
        val oldItem = recipeGrid.getStackInSlot(currentSlot)
        ignoreGridUpdate = true
        recipeGrid.setInventorySlotContents(currentSlot, newItem)

        var result = false

        if (recipe.matches(recipeGrid, world)) {
            val newOutput = recipe.getCraftingResult(recipeGrid)
            result = if (newItem.isEmpty) {
                false
            } else {
                ApiUtils.equalsIgnoreSize(craftingResult[0], newOutput)
            }
        }

        recipeGrid.setInventorySlotContents(currentSlot, oldItem)
        ignoreGridUpdate = false
        return result
    }

    fun refreshRecipe() {
        craftRecipe = CraftingManager.findMatchingRecipe(recipeGrid, world)
        craftRecipe?.let {
            craftingResult[0] = it.getCraftingResult(recipeGrid)
            if (craftingResult[0].isEmpty) {
                craftRecipe = null
            }
        }

        if (craftRecipe == null) {
            craftingResult[0] = ItemStack.EMPTY
        }
    }

    override fun serializeNBT(): NBTTagCompound {
        return newNbt {
            this["grid"] = newNbt {
                repeat(9) {
                    this[it.toString()] = recipeGrid.getStackInSlot(it).serializeNBT()
                }
            }
            this["matches"] = itemMatches?.map { if (it) 1 else 0 }?.toIntArray() ?: IntArray(0)
            this["result"] = craftingResult.serializeNBT()
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        repeat(9) {
            val itemNBT = nbt.getCompoundTag("grid")
                .getCompoundTag(it.toString())

            recipeGrid.setInventorySlotContents(it, ItemStack(itemNBT))
        }

        craftingResult.deserializeNBT(nbt.getCompoundTag("result"))

        @Suppress("SENSELESS_COMPARISON")
        if (container.tile.world != null && world.isServer) {
            resetMatches()
        } else {
            val matches = nbt.getIntArray("matches")
            if (matches.size == 9) {
                itemMatches = matches.map { it != 0 }.toBooleanArray()
            } else {
                itemMatches = null
            }
        }
    }

    data class InventorySlotLocation(
        val content: ItemStack,
        val index: Int,
        var amount: Int,
        val inventory: IItemHandler
    )
}