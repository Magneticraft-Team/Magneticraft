package com.cout970.magneticraft.guide

import java.util.*

object EntryRegistry {

    var entries = mapOf<String, BookEntry>()
        private set

    fun findEntry(name: String) =
            entries[name] ?: throw NoSuchElementException("Requested entry $name does not exist")
}
