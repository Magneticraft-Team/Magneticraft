package com.cout970.magneticraft.gui.common.blocks


import com.cout970.magneticraft.gui.common.*
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.heat.TileIcebox
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by cout970 on 08/07/2016.
 */
class ContainerIcebox(player: EntityPlayer, world: World, blockPos: BlockPos) : ContainerBase(player, world, blockPos) {

    val tile = world.getTile<TileIcebox>(blockPos)

    init {
        val inv = tile?.inventory
        inv?.let { addSlotToContainer(SlotItemHandler(inv, 0, 129, 30)) }
        bindPlayerInventory(player.inventory)
    }

    override fun sendDataToClient(): IBD? {
        tile!!
        val data = IBD()
        data.setFloat(DATA_ID_BURNING_TIME, tile.meltingTime)
        data.setFloat(DATA_ID_MAX_BURNING_TIME, tile.maxMeltingTime)
        data.setFloat(DATA_ID_MAX_FREEZING_TIME, tile.maxFreezingTime)
        data.setFloat(DATA_ID_FREEZING_TIME, tile.freezingTime)
        data.setDouble(DATA_ID_MACHINE_HEAT, tile.heat.heat)
        data.merge(tile.tank.getData())
        return data
    }

    override fun receiveDataFromServer(ibd: IBD) {
        tile!!
        ibd.getFloat(DATA_ID_BURNING_TIME, { tile.meltingTime = it })
        ibd.getFloat(DATA_ID_MAX_BURNING_TIME, { tile.maxMeltingTime = it })
        ibd.getFloat(DATA_ID_FREEZING_TIME, { tile.freezingTime = it })
        ibd.getFloat(DATA_ID_MAX_FREEZING_TIME, { tile.maxFreezingTime = it })
        ibd.getDouble(DATA_ID_MACHINE_HEAT, { tile.heat.heat = it })
        tile.tank.setData(ibd)
    }
}