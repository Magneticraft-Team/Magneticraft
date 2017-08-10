package com.cout970.magneticraft.gui.client.guide

import net.minecraft.client.Minecraft

/**
 * Created by cout970 on 2017/08/02.
 */

data class Book(val sections: Map<String, Section>)

data class Section(val name: String, val document: MarkdownDocument)

fun loadBook(): Book {
    val loader = Thread.currentThread().contextClassLoader
    val stream = loader.getResourceAsStream("assets/magneticraft/guide")
    val langOptions = stream.reader().readLines()
    val currentLang = Minecraft.getMinecraft().languageManager.currentLanguage.languageCode

    val sections = if (currentLang in langOptions) {
        loadFromLang(currentLang)
    } else {
        loadFromLang("en_us")
    }
    return Book((sections + createIndexPage(sections)).map { it.name to it }.toMap())
}

fun loadFromLang(lang: String): List<Section> {
    val loader = Thread.currentThread().contextClassLoader
    val stream = loader.getResourceAsStream("assets/magneticraft/guide/$lang")
    val files = stream.reader().readLines()
    return files.map {
        val sectionStream = loader.getResourceAsStream("assets/magneticraft/guide/$lang/$it")
        val text = sectionStream.reader().readText()
        Section(it.removeSuffix(".md"), MarkdownDocument(parseChildren(text)))
    }
}

fun createIndexPage(sections: List<Section>): Section {

    val children = (1..22).map {
        MarkdownNewLine()
    } + listOf(
            MarkdownHeader(1, listOf(MarkdownText("Index\n"))),
            MarkdownText("\n")

    ) + sections.map {
        MarkdownLink(it.name + "#0", listOf(
                MarkdownText("* " +it.name.replace("-", " ") + "\n"))
        )
    }

    return Section("index", MarkdownDocument(children))
}