package com.cout970.magneticraft.guide

import com.cout970.magneticraft.api.registries.entries.findEntry

data class LinkInfo(val entry: String, val page: Int) {
    fun getEntryTarget() = findEntry(entry) to page
}