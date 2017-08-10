package com.cout970.magneticraft.gui.common

import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.misc.gui.SlotTakeOnly
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.tileentity.TileComputer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/08/10.
 */

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