package com.cout970.magneticraft.gui.common

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.network.MessageContainerUpdate
import com.cout970.magneticraft.network.MessageGuiUpdate
import com.cout970.magneticraft.util.misc.IBD
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

    //this makes sure that the subclass handles shift click to avoid crashes
    abstract override fun transferStackInSlot(playerIn: EntityPlayer?, index: Int): ItemStack?

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