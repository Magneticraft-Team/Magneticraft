package com.cout970.magneticraft.features.electric_machines

import com.cout970.magneticraft.features.heat_machines.TileElectricHeater
import com.cout970.magneticraft.features.heat_machines.TileRfHeater
import com.cout970.magneticraft.systems.gui.containers.ContainerBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/08/10.
 */


class ContainerWindTurbine(val tile: TileWindTurbine, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        bindPlayerInventory(player.inventory)
    }
}

class ContainerElectricHeater(val tile: TileElectricHeater, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        bindPlayerInventory(player.inventory)
    }
}

class ContainerRfHeater(val tile: TileRfHeater, player: EntityPlayer, world: World, blockPos: BlockPos)
    : ContainerBase(player, world, blockPos) {

    init {
        bindPlayerInventory(player.inventory)
    }
}