package com.cout970.magneticraft.guide

import java.util.*

internal object EntryRegistry {
    private var entries = mapOf<String, Entry>()

    fun registerEntries(entryList: List<Entry>) {
        entries += entryList.associateBy(Entry::name)
    }

    fun findEntry(name: String) =
        entries[name] ?: throw NoSuchElementException("Requested entry $name does not exist")
}