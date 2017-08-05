package com.cout970.magneticraft.guide

import com.cout970.magneticraft.util.roundTo

data class BookEntry(val name: String, val pages: List<BookPage>) {

    /**
     * Gets the pair of pages, one per side, to show on the book
     */
    fun getPagePair(index: Int): Pair<BookPage, BookPage?> {
        val firstPage = pages[index roundTo 2]
        val secondPage = if (index roundTo 2 == pages.lastIndex) null else pages[(index roundTo 2) + 1]

        return firstPage to secondPage
    }

    /**
     * Gets if there is more pages to show
     */
    fun hasNextPair(index: Int) = pages.lastIndex > (index roundTo 2) + 1
}