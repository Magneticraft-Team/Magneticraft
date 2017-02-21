package com.cout970.magneticraft.gui.common.blocks


import com.cout970.magneticraft.gui.common.*
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.multiblock.TileGrinder
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by cout970 on 11/07/2016.
 */
class ContainerGrinder(player: EntityPlayer, world: World, blockPos: BlockPos) : ContainerBase(player, world, blockPos) {

    val tile = world.getTile<TileGrinder>(blockPos)

    init {
        val inv = tile?.inventory
        inv?.let {
            for (i in 0 until 4) {
                addSlotToContainer(InSlotItemHandler(inv, i, 102 + 20 * i, 16))
            }
        }
        bindPlayerInventory(player.inventory)
    }

    override fun transferStackInSlot(playerIn: EntityPlayer?, index: Int): ItemStack? = null

    override fun sendDataToClient(): IBD? {
        val data = IBD()
        tile!!
        data.setDouble(DATA_ID_VOLTAGE, tile.node.voltage)
        data.setFloat(DATA_ID_BURNING_TIME, tile.craftingProcess.timer)
        data.setFloat(DATA_ID_MACHINE_PRODUCTION, tile.production.average)
        data.setDouble(DATA_ID_MACHINE_HEAT, tile.heatNode.heat)
        return data
    }

    override fun receiveDataFromServer(ibd: IBD) {
        tile!!
        ibd.getDouble(DATA_ID_VOLTAGE, { tile.node.voltage = it })
        ibd.getFloat(DATA_ID_BURNING_TIME, { tile.craftingProcess.timer = it })
        ibd.getFloat(DATA_ID_MACHINE_PRODUCTION, { tile.production.storage = it })
        ibd.getDouble(DATA_ID_MACHINE_HEAT, { tile.heatNode.heat = it })
    }


    class InSlotItemHandler(slot: IItemHandler, index: Int, xPosition: Int, yPosition: Int) : SlotItemHandler(slot, index, xPosition, yPosition) {
        override fun isItemValid(stack: ItemStack?): Boolean {
            return true
        }

        override fun canTakeStack(playerIn: EntityPlayer?): Boolean {
            return true
        }
    }
}