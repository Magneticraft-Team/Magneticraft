package com.cout970.magneticraft.guide

import com.cout970.magneticraft.api.registries.entries.EntryRegistry

data class LinkInfo(val entry: String, val page: Int) {

    fun getEntryTarget() = EntryRegistry.findEntry(entry) to page
}