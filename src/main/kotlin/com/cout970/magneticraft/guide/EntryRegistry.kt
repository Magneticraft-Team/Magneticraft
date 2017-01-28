package com.cout970.magneticraft.guide

import java.util.*

object EntryRegistry {

    var entries = mapOf<String, BookEntry>()
        private set

    var mainEntries = emptyList<String>()
        private set

    fun registerEntries(entryList: List<BookEntry>) {
        entries += entryList.associateBy(BookEntry::name)
    }

    fun registerMainEntry(entry: BookEntry) {
        registerEntries(listOf(entry))
        mainEntries += entry.name
    }

    fun findEntry(name: String) =
            entries[name] ?: throw NoSuchElementException("Requested entry $name does not exist")
}
