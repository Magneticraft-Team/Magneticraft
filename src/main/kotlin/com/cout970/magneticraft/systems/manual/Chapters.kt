package com.cout970.magneticraft.systems.manual

import com.cout970.magneticraft.misc.ResourceList
import com.cout970.magneticraft.misc.logError
import net.minecraft.client.Minecraft
import java.util.stream.Collectors
import kotlin.streams.toList

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

        val texts = resources.mapNotNull { (loc, res) ->

            val text = res.inputStream.reader().readText()

            if (text.isEmpty()) return@mapNotNull null
            val name = loc.resourcePath.removePrefix("guide/$lang/").removeSuffix(".md")

            val prefix = name.substringBeforeLast('/', "")

            Triple(text, prefix, name)
        }

        val parsed = texts.parallelStream().map { (text, prefix, name) ->
            parseMarkdownDocument(text, prefix) to name
        }.collect(Collectors.toList())

        val sections = parsed.map { (md, name) -> Section(name, md) }

        checkInvalidLinks(sections)

        val pages = sections.map { it.name to it }.toMap()
        return Book(pages["index"] ?: error("No index page"), pages)

    } catch (e: Exception) {
        e.printStackTrace()
        return Book(Section("Error", errorDocument()), emptyMap())
    }
}

private fun checkInvalidLinks(sections: List<Section>) {

    val names = sections.map { it.name }.toSet()

    sections.forEach { sec ->
        val links = getLinks(sec.document.root)
        val loc = sec.document.location

        links.forEach {
            val url = MdRenderer.parseUrl(loc, it).first
            if (url !in names) {
                logError("Invalid link url: {} at {}", url, sec.name)
            }
        }
    }
}

private fun getLinks(tags: List<MdTag>): List<String> {
    val chinkLinks = tags.flatMap { getLinks(it.childs) }

    return chinkLinks + tags.filterIsInstance<MdLink>().map { it.url }
}

fun errorDocument(): MarkdownDocument {

    val tags = listOf(
        MdText("An Error occurred loading the book, please report to the mod author")
    )

    return MarkdownDocument(tags, "")
}
