package com.cout970.magneticraft.features.items

import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.RegisterItems
import com.cout970.magneticraft.systems.items.IItemMaker
import com.cout970.magneticraft.systems.items.ItemBase
import com.cout970.magneticraft.systems.items.ItemBuilder
import net.minecraft.item.Item

/**
 * Created by cout970 on 2017/06/11.
 */
@RegisterItems
object MetallicItems : IItemMaker {

    // Ingots
    lateinit var copperIngot: ItemBase private set
    lateinit var leadIngot: ItemBase private set
    lateinit var cobaltIngot: ItemBase private set
    lateinit var tungstenIngot: ItemBase private set
    lateinit var steelIngot: ItemBase private set
    lateinit var aluminiumIngot: ItemBase private set
    lateinit var mithrilIngot: ItemBase private set
    lateinit var nickelIngot: ItemBase private set
    lateinit var osmiumIngot: ItemBase private set
    lateinit var silverIngot: ItemBase private set
    lateinit var tinIngot: ItemBase private set
    lateinit var zincIngot: ItemBase private set

    // Light Plates
    lateinit var ironLightPlate: ItemBase private set
    lateinit var goldLightPlate: ItemBase private set
    lateinit var copperLightPlate: ItemBase private set
    lateinit var leadLightPlate: ItemBase private set
    lateinit var tungstenLightPlate: ItemBase private set
    lateinit var steelLightPlate: ItemBase private set

    // Heavy Plates
    lateinit var ironHeavyPlate: ItemBase private set
    lateinit var goldHeavyPlate: ItemBase private set
    lateinit var copperHeavyPlate: ItemBase private set
    lateinit var leadHeavyPlate: ItemBase private set
    lateinit var tungstenHeavyPlate: ItemBase private set
    lateinit var steelHeavyPlate: ItemBase private set

    // Nuggets
    lateinit var copperNugget: ItemBase private set
    lateinit var leadNugget: ItemBase private set
    lateinit var cobaltNugget: ItemBase private set
    lateinit var tungstenNugget: ItemBase private set
    lateinit var steelNugget: ItemBase private set
    lateinit var aluminiumNugget: ItemBase private set
    lateinit var mithrilNugget: ItemBase private set
    lateinit var nickelNugget: ItemBase private set
    lateinit var osmiumNugget: ItemBase private set
    lateinit var silverNugget: ItemBase private set
    lateinit var tinNugget: ItemBase private set
    lateinit var zincNugget: ItemBase private set

    // Chunks
    lateinit var ironChunk: ItemBase private set
    lateinit var goldChunk: ItemBase private set
    lateinit var copperChunk: ItemBase private set
    lateinit var leadChunk: ItemBase private set
    lateinit var cobaltChunk: ItemBase private set
    lateinit var tungstenChunk: ItemBase private set
    lateinit var aluminiumChunk: ItemBase private set
    lateinit var mithrilChunk: ItemBase private set
    lateinit var nickelChunk: ItemBase private set
    lateinit var osmiumChunk: ItemBase private set
    lateinit var silverChunk: ItemBase private set
    lateinit var tinChunk: ItemBase private set
    lateinit var zincChunk: ItemBase private set

    // Rocky Chunks
    lateinit var ironRockyChunk: ItemBase private set
    lateinit var goldRockyChunk: ItemBase private set
    lateinit var copperRockyChunk: ItemBase private set
    lateinit var leadRockyChunk: ItemBase private set
    lateinit var cobaltRockyChunk: ItemBase private set
    lateinit var tungstenRockyChunk: ItemBase private set
    lateinit var aluminiumRockyChunk: ItemBase private set
    lateinit var mithrilRockyChunk: ItemBase private set
    lateinit var nickelRockyChunk: ItemBase private set
    lateinit var osmiumRockyChunk: ItemBase private set
    lateinit var silverRockyChunk: ItemBase private set
    lateinit var tinRockyChunk: ItemBase private set
    lateinit var zincRockyChunk: ItemBase private set

    // Dusts
    lateinit var ironDust: ItemBase private set
    lateinit var goldDust: ItemBase private set
    lateinit var copperDust: ItemBase private set
    lateinit var leadDust: ItemBase private set
    lateinit var cobaltDust: ItemBase private set
    lateinit var tungstenDust: ItemBase private set
    lateinit var steelDust: ItemBase private set
    lateinit var aluminiumDust: ItemBase private set
    lateinit var mithrilDust: ItemBase private set
    lateinit var nickelDust: ItemBase private set
    lateinit var osmiumDust: ItemBase private set
    lateinit var silverDust: ItemBase private set
    lateinit var tinDust: ItemBase private set
    lateinit var zincDust: ItemBase private set

    override fun initItems(): List<Item> {
        val builder = ItemBuilder().apply {
            creativeTab = CreativeTabMg
        }

        copperIngot = builder.withName("copper_ingot").build()
        leadIngot = builder.withName("lead_ingot").build()
        cobaltIngot = builder.withName("cobalt_ingot").build()
        tungstenIngot = builder.withName("tungsten_ingot").build()
        steelIngot = builder.withName("steel_ingot").build()
        aluminiumIngot = builder.withName("aluminium_ingot").build()
        mithrilIngot = builder.withName("mithril_ingot").build()
        nickelIngot = builder.withName("nickel_ingot").build()
        osmiumIngot = builder.withName("osmium_ingot").build()
        silverIngot = builder.withName("silver_ingot").build()
        tinIngot = builder.withName("tin_ingot").build()
        zincIngot = builder.withName("zinc_ingot").build()

        ironLightPlate = builder.withName("iron_light_plate").build()
        goldLightPlate = builder.withName("gold_light_plate").build()
        copperLightPlate = builder.withName("copper_light_plate").build()
        leadLightPlate = builder.withName("lead_light_plate").build()
        tungstenLightPlate = builder.withName("tungsten_light_plate").build()
        steelLightPlate = builder.withName("steel_light_plate").build()

        ironHeavyPlate = builder.withName("iron_heavy_plate").build()
        goldHeavyPlate = builder.withName("gold_heavy_plate").build()
        copperHeavyPlate = builder.withName("copper_heavy_plate").build()
        leadHeavyPlate = builder.withName("lead_heavy_plate").build()
        tungstenHeavyPlate = builder.withName("tungsten_heavy_plate").build()
        steelHeavyPlate = builder.withName("steel_heavy_plate").build()

        copperNugget = builder.withName("copper_nugget").build()
        leadNugget = builder.withName("lead_nugget").build()
        cobaltNugget = builder.withName("cobalt_nugget").build()
        tungstenNugget = builder.withName("tungsten_nugget").build()
        steelNugget = builder.withName("steel_nugget").build()
        aluminiumNugget = builder.withName("aluminium_nugget").build()
        mithrilNugget = builder.withName("mithril_nugget").build()
        nickelNugget = builder.withName("nickel_nugget").build()
        osmiumNugget = builder.withName("osmium_nugget").build()
        silverNugget = builder.withName("silver_nugget").build()
        tinNugget = builder.withName("tin_nugget").build()
        zincNugget = builder.withName("zinc_nugget").build()

        ironChunk = builder.withName("iron_chunk").build()
        goldChunk = builder.withName("gold_chunk").build()
        copperChunk = builder.withName("copper_chunk").build()
        leadChunk = builder.withName("lead_chunk").build()
        cobaltChunk = builder.withName("cobalt_chunk").build()
        tungstenChunk = builder.withName("tungsten_chunk").build()
        aluminiumChunk = builder.withName("aluminium_chunk").build()
        mithrilChunk = builder.withName("mithril_chunk").build()
        nickelChunk = builder.withName("nickel_chunk").build()
        osmiumChunk = builder.withName("osmium_chunk").build()
        silverChunk = builder.withName("silver_chunk").build()
        tinChunk = builder.withName("tin_chunk").build()
        zincChunk = builder.withName("zinc_chunk").build()

        ironDust = builder.withName("iron_dust").build()
        goldDust = builder.withName("gold_dust").build()
        copperDust = builder.withName("copper_dust").build()
        leadDust = builder.withName("lead_dust").build()
        cobaltDust = builder.withName("cobalt_dust").build()
        tungstenDust = builder.withName("tungsten_dust").build()
        steelDust = builder.withName("steel_dust").build()
        aluminiumDust = builder.withName("aluminium_dust").build()
        mithrilDust = builder.withName("mithril_dust").build()
        nickelDust = builder.withName("nickel_dust").build()
        osmiumDust = builder.withName("osmium_dust").build()
        silverDust = builder.withName("silver_dust").build()
        tinDust = builder.withName("tin_dust").build()
        zincDust = builder.withName("zinc_dust").build()

        ironRockyChunk = builder.withName("iron_rocky_chunk").build()
        goldRockyChunk = builder.withName("gold_rocky_chunk").build()
        copperRockyChunk = builder.withName("copper_rocky_chunk").build()
        leadRockyChunk = builder.withName("lead_rocky_chunk").build()
        cobaltRockyChunk = builder.withName("cobalt_rocky_chunk").build()
        tungstenRockyChunk = builder.withName("tungsten_rocky_chunk").build()
        aluminiumRockyChunk = builder.withName("aluminium_rocky_chunk").build()
        mithrilRockyChunk = builder.withName("mithril_rocky_chunk").build()
        nickelRockyChunk = builder.withName("nickel_rocky_chunk").build()
        osmiumRockyChunk = builder.withName("osmium_rocky_chunk").build()
        silverRockyChunk = builder.withName("silver_rocky_chunk").build()
        tinRockyChunk = builder.withName("tin_rocky_chunk").build()
        zincRockyChunk = builder.withName("zinc_rocky_chunk").build()

        return listOf(
            // Ingots
            copperIngot, leadIngot, cobaltIngot, tungstenIngot, steelIngot, aluminiumIngot, mithrilIngot, nickelIngot,
            osmiumIngot, silverIngot, tinIngot, zincIngot,
            // Light Plates
            goldLightPlate, goldLightPlate, copperLightPlate, leadLightPlate, tungstenLightPlate, steelLightPlate,
            // Heavy Plates
            goldHeavyPlate, goldHeavyPlate, copperHeavyPlate, leadHeavyPlate, tungstenHeavyPlate, steelHeavyPlate,
            // Nuggets
            cobaltNugget, leadNugget, cobaltNugget, tungstenNugget, steelNugget, aluminiumNugget, mithrilNugget,
            nickelNugget, osmiumNugget, silverNugget, tinNugget, zincNugget,
            // Chunks
            goldChunk, goldChunk, copperChunk, leadChunk, cobaltChunk, tungstenChunk, aluminiumChunk, mithrilChunk,
            nickelChunk, osmiumChunk, silverChunk, tinChunk, zincChunk,
            // Dusts
            ironDust, goldDust, copperDust, leadDust, cobaltDust, tungstenDust, steelDust, aluminiumDust, mithrilDust,
            nickelDust, osmiumDust, silverDust, tinDust, zincDust,
            // Rocky chunks
            ironRockyChunk, goldRockyChunk, copperRockyChunk, leadRockyChunk, cobaltRockyChunk, tungstenRockyChunk,
            aluminiumRockyChunk, mithrilRockyChunk, nickelRockyChunk, osmiumRockyChunk, silverRockyChunk, tinRockyChunk,
            zincRockyChunk
        )
    }
}