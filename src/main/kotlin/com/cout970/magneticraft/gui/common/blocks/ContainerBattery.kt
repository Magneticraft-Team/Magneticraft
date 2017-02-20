package com.cout970.magneticraft.gui.common.blocks


import com.cout970.magneticraft.gui.common.*
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.electric.TileBattery
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by cout970 on 11/07/2016.
 */
class ContainerBattery(player: EntityPlayer, world: World, blockPos: BlockPos) : ContainerBase(player, world, blockPos) {

    val tile = world.getTile<TileBattery>(blockPos)

    init {
        val inv = tile?.inventory
        inv?.let {
            addSlotToContainer(SlotItemHandler(inv, 0, 102, 16))
            addSlotToContainer(SlotItemHandler(inv, 1, 102, 48))
        }
        bindPlayerInventory(player.inventory)
    }

    override fun sendDataToClient(): IBD? {
        val data = IBD()
        tile!!
        data.setDouble(DATA_ID_VOLTAGE, tile.mainNode.voltage)
        data.setInteger(DATA_ID_STORAGE, tile.storage)
        data.setFloat(DATA_ID_CHARGE_RATE, tile.chargeRate.average)
        data.setFloat(DATA_ID_ITEM_CHARGE_RATE, tile.itemChargeRate.average)
        return data
    }

    override fun receiveDataFromServer(ibd: IBD) {
        tile!!
        ibd.getDouble(DATA_ID_VOLTAGE, { tile.mainNode.voltage = it })
        ibd.getInteger(DATA_ID_STORAGE, { tile.storage = it })
        ibd.getFloat(DATA_ID_CHARGE_RATE, { tile.chargeRate.storage = it })
        ibd.getFloat(DATA_ID_ITEM_CHARGE_RATE, { tile.itemChargeRate.storage = it })
    }
}