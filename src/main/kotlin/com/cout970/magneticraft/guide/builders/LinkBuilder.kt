package com.cout970.magneticraft.guide.builders

import com.cout970.magneticraft.guide.LinkInfo
import com.cout970.magneticraft.guide.components.Link
import com.cout970.magneticraft.guide.components.PageComponent

class LinkBuilder {
    lateinit var entry: String
    var page: Int = 0
    lateinit var base: PageComponent

    fun build() = Link(LinkInfo(entry, page), base)
}