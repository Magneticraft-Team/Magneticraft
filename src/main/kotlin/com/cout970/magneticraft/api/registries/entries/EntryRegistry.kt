package com.cout970.magneticraft.api.registries.entries

import com.cout970.magneticraft.guide.Entry
import java.util.*

var entries = mapOf<String, Entry>()
    private set

var mainEntries = emptyList<String>()
    private set

fun registerEntries(entryList: List<Entry>) {
    entries += entryList.associateBy(Entry::name)
}

fun registerMainEntry(entry: Entry) {
    registerEntries(listOf(entry))
    mainEntries += entry.name
}

fun findEntry(name: String) =
    entries[name] ?: throw NoSuchElementException("Requested entry $name does not exist")
