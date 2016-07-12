package com.cout970.magneticraft.registry

import com.cout970.magneticraft.tileentity.TileCrushingTable
import com.cout970.magneticraft.tileentity.TileFeedingTrough
import com.cout970.magneticraft.tileentity.TileTableSieve
import com.cout970.magneticraft.tileentity.electric.*
import net.minecraftforge.fml.common.registry.GameRegistry

//@formatter:off
val tiles = mapOf(
        TileCrushingTable::class.java           to "crushing_table",
        TileTableSieve::class.java              to "table_sieve",
        TileFeedingTrough::class.java           to "feeding_trough",
        TileElectricConnector::class.java       to "electric_connector",
        TileElectricPole::class.java            to "electric_pole",
        TileIncendiaryGenerator::class.java     to "incendiary_generator",
        TileElectricFurnace::class.java         to "electric_furnace",
        TileElectricPoleAdapter::class.java     to "electric_pole_adapter",
        TileBattery::class.java                 to "battery"
)
//@formatter:on

fun registerTileEntities() {
    tiles.forEach { GameRegistry.registerTileEntity(it.key, "magneticraft:${it.value}") }
}