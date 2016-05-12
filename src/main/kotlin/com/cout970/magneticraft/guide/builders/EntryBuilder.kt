package com.cout970.magneticraft.guide.builders

import com.cout970.magneticraft.guide.Entry
import com.cout970.magneticraft.guide.Page

/**
 * Created by cypheraj on 5/8/16.
 */
class EntryBuilder(val title: String) {
    val pages = mutableListOf<Page>()
    operator fun Page.unaryPlus() {
        pages += this
    }

    fun build() = Entry(title, pages)
}