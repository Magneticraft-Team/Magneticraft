package com.cout970.magneticraft.gui.common.blocks


import com.cout970.magneticraft.gui.common.ContainerBase
import com.cout970.magneticraft.gui.common.DATA_ID_BURNING_TIME
import com.cout970.magneticraft.gui.common.DATA_ID_MACHINE_HEAT
import com.cout970.magneticraft.gui.common.DATA_ID_MAX_BURNING_TIME
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.tileentity.heat.TileFirebox
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by cout970 on 08/07/2016.
 */
class ContainerFirebox(player: EntityPlayer, world: World, blockPos: BlockPos) : ContainerBase(player, world, blockPos) {

    val tile = world.getTile<TileFirebox>(blockPos)

    init {
        val inv = tile?.inventory
        inv?.let { addSlotToContainer(SlotItemHandler(inv, 0, 129, 30)) }
        bindPlayerInventory(player.inventory)
    }

    override fun sendDataToClient(): IBD? {
        tile!!
        val data = IBD()
        data.setFloat(DATA_ID_BURNING_TIME, tile.burningTime)
        data.setFloat(DATA_ID_MAX_BURNING_TIME, tile.maxBurningTime)
        data.setDouble(DATA_ID_MACHINE_HEAT, tile.heat.heat)
        return data
    }

    override fun receiveDataFromServer(ibd: IBD) {
        tile!!
        ibd.getFloat(DATA_ID_BURNING_TIME, { tile.burningTime = it })
        ibd.getFloat(DATA_ID_MAX_BURNING_TIME, { tile.maxBurningTime = it })
        ibd.getDouble(DATA_ID_MACHINE_HEAT, { tile.heat.heat = it })
    }
}