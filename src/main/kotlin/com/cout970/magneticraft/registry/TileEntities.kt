package com.cout970.magneticraft.registry

import com.cout970.magneticraft.tileentity.*
import com.cout970.magneticraft.tileentity.electric.*
import com.cout970.magneticraft.tileentity.multiblock.TileHydraulicPress
import com.cout970.magneticraft.tileentity.multiblock.TileMultiblock
import com.cout970.magneticraft.tileentity.multiblock.TileSolarPanel
import net.minecraftforge.fml.common.registry.GameRegistry

//Map with all the TileEntities in the mod
//@formatter:off
val tiles = mapOf<Class<out TileBase>, String>(
        TileCrushingTable::class.java           to "crushing_table",
        TileTableSieve::class.java              to "table_sieve",
        TileFeedingTrough::class.java           to "feeding_trough",
        TileElectricConnector::class.java       to "electric_connector",
        TileElectricPole::class.java            to "electric_pole",
        TileIncendiaryGenerator::class.java     to "incendiary_generator",
        TileIncendiaryGenerator.TileIncendiaryGeneratorBottom::class.java to "incendiary_generator_bottom",
        TileElectricFurnace::class.java         to "electric_furnace",
        TileElectricPoleAdapter::class.java     to "electric_pole_adapter",
        TileBattery::class.java                 to "battery",
        TileInfiniteWater::class.java           to "infinite_water",
        TileInfiniteEnergy::class.java          to "infinite_energy",
        TileAirLock::class.java                 to "airlock",
        TileHydraulicPress::class.java          to "hydraulic_press",
        TileMultiblock::class.java              to "tile_multiblock",
        TileSolarPanel::class.java              to "solar_panel"
//        TileInserter::class.java                to "inserter"
)
//@formatter:on

/**
 * Called by CommonProxy to register all the TileEntities
 */
fun registerTileEntities() {
    tiles.forEach { GameRegistry.registerTileEntity(it.key, "magneticraft:${it.value}") }
}