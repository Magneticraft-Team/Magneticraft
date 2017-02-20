package com.cout970.magneticraft.guide

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.util.vector.Vec2d

const val GUIDE_LANG = "${MOD_ID}.guide"
val PAGE_SIZE = Vec2d(108, 141)

data class Book(val entries: List<BookEntry>)

val book: Book by lazy {
    JsonSerializer.read()
}
