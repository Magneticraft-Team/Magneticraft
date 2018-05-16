package com.cout970.magneticraft.gui.client.guide

import com.cout970.magneticraft.util.ResourceList
import net.minecraft.client.Minecraft

/**
 * Created by cout970 on 2017/08/02.
 */

data class Book(val index: Section, val sections: Map<String, Section>)

data class Section(val name: String, val document: MarkdownDocument)

fun loadBook(): Book {

    try {
        val langOptions = ResourceList.getGuideBookLanguages()
        val currentLang = Minecraft.getMinecraft().languageManager.currentLanguage.languageCode

        val lang = if (currentLang in langOptions) currentLang else "en_us"

        val locations = ResourceList.getGuideBookPages(lang)
        val resources = locations.map { it to Minecraft.getMinecraft().resourceManager.getResource(it) }

        val sections = resources.mapNotNull { (loc, res) ->

            val text = res.inputStream.reader().readText()

            if (text.isEmpty()) return@mapNotNull null
            val name = loc.resourcePath.removePrefix("guide/$lang/").removeSuffix(".md")

            Section(name, MarkdownDocument(parseChildren(text)))
        }

        val pages = sections.map { it.name to it }.toMap()
        return Book(pages["index"]!!, pages)

    } catch (e: Exception) {
        e.printStackTrace()
        return Book(Section("Error", errorDocument()), emptyMap())
    }
}

private fun errorDocument(): MarkdownDocument {

    val tags = listOf(
            MdText("An Error occurred loading the book, please report to the mod author")
    )

    return MarkdownDocument(tags)
}
