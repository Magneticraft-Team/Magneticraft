package com.cout970.magneticraft.gui.common

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.network.MessageIBD
import com.cout970.magneticraft.util.misc.IBD
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 08/07/2016.
 */

val DATA_ID_VOLTAGE = 1
val DATA_ID_BURNING_TIME = 2
val DATA_ID_MAX_BURNING_TIME = 3
val DATA_ID_MACHINE_HEAT = 4
val DATA_ID_MACHINE_PRODUCTION = 5
val DATA_ID_FLUID_AMOUNT = 6
val DATA_ID_FLUID_NAME = 7
val DATA_ID_STORAGE = 8
val DATA_ID_CHAGE_RATE = 9

abstract class ContainerBase(val player: EntityPlayer, val world: World, val pos: BlockPos) : Container() {

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
                Magneticraft.network.sendTo(MessageIBD(ibd), player)
            }
        }
    }

    //to avoid crashed with shift click
    abstract override fun transferStackInSlot(playerIn: EntityPlayer?, index: Int): ItemStack?

    abstract fun sendDataToClient(): IBD?

    abstract fun receiveDataFromServer(ibd: IBD)
}