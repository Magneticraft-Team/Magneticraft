package com.cout970.magneticraft.guide

import coffee.cypher.mcextlib.delegates.postInit
import coffee.cypher.mcextlib.extensions.strings.i18n
import com.cout970.magneticraft.api.registries.entries.mainEntries
import com.cout970.magneticraft.gui.Coords
import com.cout970.magneticraft.gui.centeredAt
import com.cout970.magneticraft.gui.client.guide.PAGE_CENTER
import com.cout970.magneticraft.guide.builders.*
import com.cout970.magneticraft.util.MODID

const val GUIDE_LANG = "$MODID.guide"

val contentTable: Entry by postInit {
    entry("$GUIDE_LANG.contents") {
        +page {
            +image {
                location = "$GUIDE_FOLDER/logo.png"
                size = Coords(150, 100)
                position = size centeredAt PAGE_CENTER - Coords(0, 10)
            }
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