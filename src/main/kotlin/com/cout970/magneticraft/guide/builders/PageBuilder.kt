package com.cout970.magneticraft.guide.builders

import com.cout970.magneticraft.guide.BookPage
import com.cout970.magneticraft.guide.components.PageComponent

/**
 * Created by cypheraj on 5/8/16.
 */
class PageBuilder {
    private val components = mutableListOf<PageComponent>()

    operator fun PageComponent.unaryPlus() {
        components += this
    }

    fun build() = BookPage(components)
}