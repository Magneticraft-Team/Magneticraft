package com.cout970.magneticraft

import com.cout970.magneticraft.tileentity.TileCrushingTable
import com.cout970.magneticraft.tileentity.TileFeedingTrough
import com.cout970.magneticraft.tileentity.TileTableSieve
import net.minecraftforge.fml.common.registry.GameRegistry

val tiles = mapOf(
    TileCrushingTable::class.java to "crushing_table",
    TileTableSieve::class.java to "table_sieve",
    TileFeedingTrough::class.java to "feeding_trough"
)

fun registerTileEntities() {
    tiles.forEach { GameRegistry.registerTileEntity(it.key, it.value) }
}