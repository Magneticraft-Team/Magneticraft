package com.cout970.magneticraft

import com.cout970.magneticraft.tileentity.TileCrushingTable
import net.minecraftforge.fml.common.registry.GameRegistry

val tiles = mapOf(
        TileCrushingTable::class.java to "crushing_table"
)

fun registerTileEntities() {
    tiles.forEach { GameRegistry.registerTileEntity(it.key, it.value) }
}