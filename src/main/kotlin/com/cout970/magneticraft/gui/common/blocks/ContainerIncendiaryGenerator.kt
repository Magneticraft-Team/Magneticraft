package com.cout970.magneticraft.gui.common.blocks

import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.gui.common.*
import com.cout970.magneticraft.tileentity.electric.TileIncendiaryGenerator
import com.cout970.magneticraft.util.IBD
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.items.SlotItemHandler

/**
 * Created by cout970 on 08/07/2016.
 */
class ContainerIncendiaryGenerator(player: EntityPlayer, world: World, blockPos: BlockPos) : ContainerBase(player, world, blockPos) {

    val tile = world.getTile<TileIncendiaryGenerator>(blockPos)

    init {
        val inv = tile?.inventory
        inv?.let { addSlotToContainer(SlotItemHandler(inv, 0, 129, 30)) }
        bindPlayerInventory(player.inventory)
    }

    override fun sendDataToClient(): IBD? {
        val data = IBD()
        tile!!
        data.setDouble(DATA_ID_VOLTAGE, tile.node.voltage)
        data.setFloat(DATA_ID_BURNING_TIME, tile.burningTime)
        data.setFloat(DATA_ID_MAX_BURNING_TIME, tile.maxBurningTime)
        data.setFloat(DATA_ID_MACHINE_HEAT, tile.heat)
        data.setFloat(DATA_ID_MACHINE_PRODUCTION, tile.production.average)
        return data
    }

    override fun receiveDataFromServer(ibd: IBD) {
        tile!!
        ibd.getDouble(DATA_ID_VOLTAGE, { tile.mainNode.voltage = it })
        ibd.getFloat(DATA_ID_BURNING_TIME, { tile.burningTime = it })
        ibd.getFloat(DATA_ID_MAX_BURNING_TIME, { tile.maxBurningTime = it })
        ibd.getFloat(DATA_ID_MACHINE_HEAT, { tile.heat = it })
        ibd.getFloat(DATA_ID_MACHINE_PRODUCTION, { tile.production.storage = it })
    }
}