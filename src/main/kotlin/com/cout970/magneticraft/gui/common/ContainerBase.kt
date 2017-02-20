package com.cout970.magneticraft.gui.common

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.misc.inventory.getNonPlayerSlotRanges
import com.cout970.magneticraft.misc.inventory.getPlayerSlotRanges
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.network.MessageContainerUpdate
import com.cout970.magneticraft.network.MessageGuiUpdate
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

abstract class ContainerBase(val player: EntityPlayer, val world: World, val pos: BlockPos) : Container() {

    //the TileEntity that has the gui, can be null
    val tileEntity = world.getTileEntity(pos)

    override fun canInteractWith(playerIn: EntityPlayer?): Boolean = true

    fun bindPlayerInventory(playerInventory: InventoryPlayer) {

        for (i in 0..2) {
            for (j in 0..8) {
                this.addSlotToContainer(Slot(playerInventory, j + i * 9 + 9,
                        8 + j * 18,
                        84 + i * 18))
            }
        }

        for (i in 0..8) {
            this.addSlotToContainer(Slot(playerInventory, i,
                    8 + i * 18,
                    142))
        }
    }

    override fun detectAndSendChanges() {
        super.detectAndSendChanges()
        if (player is EntityPlayerMP) {
            val ibd = sendDataToClient()
            if (ibd != null) {
                Magneticraft.network.sendTo(MessageContainerUpdate(ibd), player)
            }
        } else if (player is EntityPlayerSP) {
            val ibd = sendDataToServer()
            if (ibd != null) {
                Magneticraft.network.sendToServer(MessageGuiUpdate(ibd, player.persistentID))
            }
        }
    }

    /**
     * Try to merge the [stack] in any slot range specified in [ranges].
     */
    protected fun mergeItemStack(stack: ItemStack, ranges: List<IntRange>, reverseDirection: Boolean): Boolean {
        ranges.forEach {
            if (this.mergeItemStack(stack, it.start, it.endInclusive, reverseDirection))
                return true
        }

        return false
    }

    override fun transferStackInSlot(playerIn: EntityPlayer?, index: Int): ItemStack? {
        if (index < this.inventorySlots.size) {
            val slot = this.inventorySlots[index]

            return if (slot.inventory is InventoryPlayer) {
                tryToMerge(playerIn, slot, false)
            } else {
                tryToMerge(playerIn, slot, true)
            }

        }

        return null
    }

    private fun tryToMerge(playerIn: EntityPlayer?, slot: Slot, playerSlot: Boolean): ItemStack? {

        val stack = slot.stack

        if (!slot.hasStack || stack == null || playerIn == null)
            return null

        val copy = stack.copy()

        val slotRanges = (if (playerSlot) this.getPlayerSlotRanges(playerIn) else this.getNonPlayerSlotRanges())

        slotRanges.let {
            if (!this.mergeItemStack(stack, slotRanges, playerSlot))
                return null

            if (stack.stackSize == 0) {
                slot.putStack(null)
            } else {
                slot.onSlotChanged()
            }

        }

        // Avoid crash, Minecraft call the method 'transferSlot' recursively if 'returnedStack == inputStack'
        if (copy.item == stack.item)
            return null

        return copy
    }

    //Called every tick to get the changes in the server that need to be sent to the client
    open fun sendDataToClient(): IBD? = null

    //Called every tick to get the changes in the client that need to be sent to the server
    //for example buttons
    open fun sendDataToServer(): IBD? = null

    //called when server data is received
    open fun receiveDataFromServer(ibd: IBD) = Unit

    //called when client data is received
    open fun receiveDataFromClient(ibd: IBD) = Unit
}