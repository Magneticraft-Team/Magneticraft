package com.cout970.magneticraft.guide

data class LinkInfo(val entry: String, val page: Int) {

    fun getEntryTarget() = EntryRegistry.findEntry(entry) to page
}