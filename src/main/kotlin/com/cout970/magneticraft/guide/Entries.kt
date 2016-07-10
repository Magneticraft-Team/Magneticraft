package com.cout970.magneticraft.guide

import coffee.cypher.mcextlib.delegates.postInit
import com.cout970.magneticraft.gui.client.guide.PAGE_CENTER
import com.cout970.magneticraft.guide.builders.GUIDE_FOLDER
import com.cout970.magneticraft.guide.builders.entry
import com.cout970.magneticraft.guide.builders.image
import com.cout970.magneticraft.guide.builders.page
import com.cout970.magneticraft.util.MODID
import com.cout970.magneticraft.util.vector.Vec2d

const val GUIDE_LANG = "$MODID.guide"

val contentTable: Entry by postInit {
    entry("$GUIDE_LANG.contents") {
        +page {
            +image {
                location = "$GUIDE_FOLDER/logo.png"
                size = Vec2d(150, 100)
                position = size centeredAt PAGE_CENTER - Vec2d(0, 10)
            }
        }

//        +page {
//            val shift = 5 + (7.5 * (mainEntries.size - 1)).toInt()
//            val start = Vec2d(10, PAGE_CENTER.y - shift)
//
//            mainEntries.forEachIndexed { i, name ->
//                +text {
//                    text = "[${name.i18n()}]($name:0)"
//                    position = start + Vec2d(0, 15 * i)
//                }
//            }
//        }
    }
}