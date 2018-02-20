package com.cout970.magneticraft.gui.client.guide

import com.cout970.magneticraft.util.ResourceList
import net.minecraft.client.Minecraft

/**
 * Created by cout970 on 2017/08/02.
 */

data class Book(val sections: Map<String, Section>)

data class Section(val name: String, val document: MarkdownDocument)

fun loadBook(): Book {

    try {
        val langOptions = ResourceList.getGuideBookLanguages()
        val currentLang = Minecraft.getMinecraft().languageManager.currentLanguage.languageCode

        val lang = if (currentLang in langOptions) currentLang else "en_us"

        val locations = ResourceList.getGuideBookPages(lang)

        val sections = locations.map {
            it to Minecraft.getMinecraft().resourceManager.getResource(it)
        }.mapNotNull { (loc, res) ->
            val text = res.inputStream.reader().readText()
            if (text.isEmpty()) return@mapNotNull null
            val name = loc.resourcePath.removePrefix("guide/$lang/").removeSuffix(".md")
            Section(name, MarkdownDocument(parseChildren(text)))
        }

        return Book((sections + createIndexPage(sections)).map { it.name to it }.toMap())

    } catch (e: Exception) {
        e.printStackTrace()
        return Book(emptyMap())
    }
}

fun createIndexPage(sections: List<Section>): Section {

    val children = listOf(
            MdHeader(1, listOf(MdText("Index\n"))),
            MdText("\n")

    ) + sections.map {
        val name = it.name.split("-").joinToString(" ") { it.capitalize() }
        MdLink(it.name + "#0", listOf(
                MdText("* $name\n"))
        )
    }

    return Section("index", MarkdownDocument(children))
}

