package com.cout970.magneticraft.guide

import com.cout970.magneticraft.util.roundTo

class Entry(val name: String, pages: List<Page>) {
    val pages = pages

    fun getPagePair(page: Int): Pair<Page, Page?> {
        val firstPage = pages[page roundTo 2]
        val secondPage = if (page roundTo 2 == pages.lastIndex) null else pages[(page roundTo 2) + 1]

        return firstPage to secondPage
    }

    fun hasNextPair(page: Int) = pages.lastIndex > (page roundTo 2) + 1
}