package com.cout970.magneticraft.guide

import coffee.cypher.mcextlib.delegates.loaderState
import coffee.cypher.mcextlib.extensions.strings.i18n
import com.cout970.magneticraft.gui.Coords
import com.cout970.magneticraft.gui.client.guide.PAGE_CENTER
import com.cout970.magneticraft.guide.builders.entry
import com.cout970.magneticraft.guide.builders.page
import com.cout970.magneticraft.guide.builders.text
import com.cout970.magneticraft.util.MODID
import net.minecraftforge.fml.common.LoaderState

const val GUIDE = "$MODID.guide"

val mainEntries = emptyList<String>()

val contentTable: Entry by loaderState(LoaderState.POSTINITIALIZATION) {
    entry("$GUIDE.contents") {
        +page {
            //logo here
        }

        +page {
            val shift = 5 + (7.5 * (mainEntries.size - 1)).toInt()
            val start = Coords(10, PAGE_CENTER.y - shift)

            mainEntries.forEachIndexed { i, name ->
                +text {
                    text = "[${name.i18n()}]($name:0)"
                    position = start + Coords(0, 15 * i)
                }
            }
        }
    }
}