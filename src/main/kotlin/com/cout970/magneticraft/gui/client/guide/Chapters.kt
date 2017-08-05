package com.cout970.magneticraft.gui.client.guide

/**
 * Created by cout970 on 2017/08/02.
 */

class Book {
    val sections: Map<String, Section> = mapOf("test1" to Section("test1", MarkdownDocument(parseChildren(testPage))))
}

data class Section(val name: String, val document: MarkdownDocument)


private val testPage = """**Crushing Table**

The first step and the earliest form of ore duplication in Magneticraft; the Crushing Table allows you to crush ores into their corresponding Rocky Chunks.

Items and blocks are placed on to the Crushing Table by right-clicking on it with the item or block you wish to place on to it in your hand.

Ores placed on the Crushing Table will require a hammer of equal or greater material than the mining level of the ore. Ex. Gold Ore requires an iron level hammer. The level of the hammer also affects the amount of swings you will need to crush the ore, the better the material of the hammer, the faster the ore is crushed.

Ores must be manually placed on the Table via right-clicking with the ore in hand, and removed from the Table manually after completion. The Crushing Table does not support item importing or exporting by pipe, hopper, etc.

Any Item can be placed on the Table itself, making it a useful tool for decoration.

The Crushing Table is also used to make metal plates early game. It takes 6 metal nuggets to make one plate of the corresponding metal and takes 4 ingots to make a heavy plate of the corresponding material as ingots used.

**Hammers**

Hammers come in three tiers. Stone, iron, and steel. Each tier is able to be used to crush higher level of ores on the Crushing Table. Stone can crush anything a stone pickaxe can mine. Iron can crush anything and iron pickaxe can mine. And steel can crush all ores.

Along with being able to crush higher levels of ores on the Crushing table. Higher tier hammers have more durability and require fewer swings to crush a ore. Stone taking 10, iron taking 8, and steel taking only 6 swings to crush a ore."""