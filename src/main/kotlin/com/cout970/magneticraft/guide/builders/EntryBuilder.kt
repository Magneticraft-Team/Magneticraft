package com.cout970.magneticraft.guide.builders

import com.cout970.magneticraft.guide.BookEntry
import com.cout970.magneticraft.guide.BookPage

/**
 * Created by cypheraj on 5/8/16.
 */
class EntryBuilder(val title: String) {
    val pages = mutableListOf<BookPage>()
    operator fun BookPage.unaryPlus() {
        pages += this
    }

    fun build() = BookEntry(title, pages)
}