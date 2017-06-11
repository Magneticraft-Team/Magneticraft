package com.cout970.magneticraft.gui.common.blocks

import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.gui.common.ContainerBase
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.network.MessageGuiUpdate
import com.cout970.magneticraft.tileentity.computer.TileComputer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by cout970 on 2016/09/30.
 */
class ContainerMonitor(val tile: TileComputer, player: EntityPlayer, world: World, blockPos: BlockPos) : ContainerBase(player, world, blockPos) {

    init {
        addSlotToContainer(object : SlotItemHandler(tile.inv, 0, 64, 233) {
            override fun canTakeStack(playerIn: EntityPlayer?): Boolean = false
        })
    }

    override fun transferStackInSlot(playerIn: EntityPlayer?, index: Int): ItemStack? = null

    override fun sendDataToClient(): IBD {
        val ibd = IBD()
        tile.monitor.saveToClient(ibd)
        ibd.setBoolean(3, tile.motherboard.isOnline())

        return ibd
    }

    override fun sendDataToServer(): IBD {
        val ibd = IBD()
        tile.monitor.saveToServer(ibd)
        return ibd
    }

    override fun receiveDataFromServer(ibd: IBD) {
        tile.monitor.loadFromServer(ibd)
        ibd.getBoolean(3) { if (it) tile.motherboard.start() else tile.motherboard.halt() }
    }

    override fun receiveDataFromClient(ibd: IBD) {
        ibd.getInteger(50) {
            when (it) {
                0 -> tile.motherboard.start()
                1 -> tile.motherboard.halt()
                2 -> tile.motherboard.reset()
            }
        }
        tile.monitor.loadFromClient(ibd)
    }

    fun onPress(id: Int) {
        Magneticraft.network.sendToServer(MessageGuiUpdate(IBD().apply { setInteger(50, id) }, player.persistentID))
    }
}