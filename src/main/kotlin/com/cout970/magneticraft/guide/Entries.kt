package com.cout970.magneticraft.guide

import coffee.cypher.mcextlib.extensions.strings.i18n
import com.cout970.magneticraft.guide.builders.*
import com.cout970.magneticraft.util.MOD_ID
import com.cout970.magneticraft.util.vector.Vec2d

const val GUIDE_LANG = "$MOD_ID.guide"
val PAGE_SIZE = Vec2d(108, 141)
val PAGE_CENTER = PAGE_SIZE.center()

data class Book(val entries: List<BookEntry>)

val book: Book by lazy {
    JsonSerializer.read()
}

fun createBook(): Book {
    return Book(listOf(
            entry("$GUIDE_LANG.contents") {
                +page {
                    +image {
                        location = "$GUIDE_FOLDER/logo.png"
                        size = Vec2d(150, 100) * 0.95
                        position = size centeredAt PAGE_CENTER - Vec2d(0, 10)
                    }
                }

                +page {
                    val shift = 5 + (7.5 * (EntryRegistry.mainEntries.size - 1)).toInt()
                    val start = Vec2d(10, PAGE_CENTER.y - shift)

                    EntryRegistry.mainEntries.forEachIndexed { i, name ->
                        +text {
                            text = "[${name.i18n()}]($name:0)"
                            position = start + Vec2d(0, 15 * i)
                        }
                    }
                }
            }
    ))
}