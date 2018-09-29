package com.cout970.magneticraft.systems.manual

import java.util.*

fun main(args: Array<String>) {
    val parser = MarkdownParser("""# World Generation

**Copper Ore**

A common ore that is used in lots of recipes, it's found in the form of Chalcopyrite
Default config values:
- Max veins per chunk: 11
- Max blocks per vein: 8
- Min Y level: 10
- Max Y level: 70

**Lead Ore**

A common ore that is used in lots of recipes, it's found in the form of Galena, a compound of lead and silver.
Default config values:
- Max veins per chunk: 10
- Max blocks per vein: 8
- Min Y level: 2
- Max Y level: 80

**Tungsten Ore**

A strong, rarer ore that is used in large amounts of industrial machinery, it's found in the form of Wolframite
Default config values:
- Max veins per chunk: 8
- Max blocks per vein: 8
- Min Y level: 20
- Max Y level: 60


**Pyrite Ore**

A compound of iron and sulfur (iron sulfide) also known as fool's gold, looks exactly like gold, but it's used to get sulfur.
Default config values:
- Max veins per chunk: 9
- Max blocks per vein: 9
- Min Y level: 30
- Max Y level: 100

**Limestone**

A common rock type that generates in large veins
- Max veins per chunk: 2
- Max blocks per vein: 50
- Min Y level: 16
- Max Y level: 50""".trimIndent())

    println(parser.parse())

}

private const val EOF = -1
private const val H1 = 255
private const val H2 = 256
private const val H3 = 257
private const val H4 = 258
private const val H5 = 259
private const val H6 = 260
private const val SPACE = ' '.toInt()
private const val ASTERISK1 = '*'.toInt()
private const val ASTERISK2 = 263
private const val ASTERISK3 = 264
private const val LINE_END = '\n'.toInt()
private const val PARAGRAPH_END = 265
private const val WORD = 266
private const val LEFT_BRACE = '['.toInt()
private const val RIGHT_BRACE = ']'.toInt()
private const val LEFT_PAREN = '('.toInt()
private const val RIGHT_PAREN = ')'.toInt()

private typealias Token = Pair<Int, String>

data class Document(val paragraphs: List<Paragraph>) {
    override fun toString(): String {
        return paragraphs.joinToString("\n\n")
    }
}

data class Paragraph(val lines: List<Line>) {
    override fun toString(): String {
        return lines.joinToString("\n") { "Line('$it')" }
    }
}

data class Header(val level: Int, val content: List<LineContent>) : LineContent() {
    override fun toString(): String {
        return "Header($level, '${content.joinToString("")}')"
    }
}

data class Line(val content: List<LineContent>) {
    override fun toString(): String {
        return content.joinToString("")
    }
}

sealed class LineContent

data class Text(val txt: String) : LineContent() {
    override fun toString(): String {
        return txt
    }
}

data class Bold(val txt: String) : LineContent() {
    override fun toString(): String {
        return "Bold('$txt')"
    }
}

data class Italic(val txt: String) : LineContent() {
    override fun toString(): String {
        return "Italic('$txt')"
    }
}

data class BoldItalic(val txt: String) : LineContent() {
    override fun toString(): String {
        return "BoldItalic('$txt')"
    }
}

data class Link(val txt: String, val url: String) : LineContent() {
    override fun toString(): String {
        return "Link('$txt', '$url')"
    }
}

data class ListItem(val content: List<LineContent>) : LineContent() {
    override fun toString(): String {
        return "ListItem('${content.joinToString("")}')"
    }
}

data class ListGroup(val items: List<ListItem>) : LineContent() {
    override fun toString(): String {
        return "ListGroup(${items.joinToString()})"
    }
}

class MarkdownParser(str: String) {

    val buff = TokenBuffer(StringBuilder(str))

    fun parse() = readDocument()

    private val Token.id: Int get() = first
    private val Token.content: String get() = second

    private fun readDocument(): Document {
        val list = mutableListOf<Paragraph>()

        do {
            val p = readParagraph() ?: break
            list.add(p)
        } while (true)

        return Document(list)
    }

    private fun readParagraph(): Paragraph? {
        var tk: Token

        // ignore initial spaces
        do {
            tk = buff.read()
        } while (tk.first == SPACE)
        buff.returnToken(tk)

        val tokens = mutableListOf<Token>()
        var token: Token

        do {
            token = buff.read()
            tokens.add(token)
        } while (token.id != PARAGRAPH_END && token.id != EOF)

        if (tokens.size == 1 && tokens[0].id == EOF) return null
        return processParagraph(tokens.dropLast(1))
    }

    private fun processParagraph(tokens: List<Token>): Paragraph {
        if (tokens.isEmpty()) return Paragraph(emptyList())

        val lines = readLines(tokens)

        if (lines.any { it.content.size == 1 && it.content[0] is ListItem }) {
            val newLines = mutableListOf<Line>()
            val items = mutableListOf<ListItem>()
            var inList = false

            lines.forEach { line ->
                val isItem = line.content.size == 1 && line.content[0] is ListItem
                val item = if (isItem) line.content[0] as ListItem else null

                if (isItem) {
                    inList = true
                    items.add(item!!)
                } else {
                    if (inList) {
                        inList = false
                        newLines.add(Line(listOf(
                            ListGroup(items.toList())
                        )))
                        items.clear()
                    }
                    newLines.add(line)
                }
            }

            if (inList) {
                newLines.add(Line(listOf(
                    ListGroup(items)
                )))
            }

            return Paragraph(newLines)
        }

        return Paragraph(lines)
    }

    private fun readLines(tokens: List<Token>): List<Line> {
        val lines = mutableListOf<Line>()
        var tokenList = tokens

        while (true) {
            val index = tokenList.indexOfFirst { it.id == LINE_END }

            if (index == -1) {
                lines += processLine(tokenList)
                break
            } else {
                val line = tokenList.subList(0, index)
                lines += processLine(line)
                tokenList = tokenList.subList(index + 1, tokenList.size)
            }
        }

        return lines
    }

    private fun processLine(tokens: List<Token>): Line {
        if (tokens.size > 2 && isHeader(tokens[0]) && tokens[1].id == SPACE) {
            return Line(listOf(
                readHeader(tokens)
            ))
        }

        if (tokens.size > 2 && isList(tokens[0]) && tokens[1].id == SPACE) {
            val rest = tokens.drop(1).dropWhile { it.id == SPACE }
            return Line(listOf(
                ListItem(processText(rest))
            ))
        }

        // TODO check for ---, etc
        val content = processText(tokens)

        return Line(content)
    }

    private fun isList(tk: Token) = when (tk.id) {
        '-'.toInt() -> true
        ASTERISK1 -> true
        WORD -> tk.second == "+"
        else -> false
    }

    private fun isHeader(tk: Token) = when (tk.id) {
        H1, H2, H3, H4, H5, H6 -> true
        else -> false
    }

    private fun readHeader(tokens: List<Token>): Header {
        val level = tokens[0].id - H1 + 1
        val content = tokens.drop(1).dropWhile { it.id == SPACE }

        return Header(level, processText(content))
    }

    private fun processText(tokens: List<Token>): List<LineContent> {
        val content = mutableListOf<LineContent>()
        val tokensLeft = ArrayDeque(tokens)

        while (tokensLeft.isNotEmpty()) {
            val tk = tokensLeft.first

            when (tk.id) {
                ASTERISK1, ASTERISK2, ASTERISK3 -> {
                    tokensLeft.removeFirst()
                    val res = readTag(tk.id, tokensLeft.toList())

                    if (res == null) {
                        content.add(Text(tk.second))
                    } else {
                        content += res.first
                        repeat(res.second) {
                            tokensLeft.removeFirst()
                        }
                    }
                }

                LEFT_BRACE -> {
                    tokensLeft.removeFirst()
                    val res = readLink(tokensLeft.toList())

                    if (res == null) {
                        content.add(Text(tk.second))
                    } else {
                        content += res.first
                        repeat(res.second) {
                            tokensLeft.removeFirst()
                        }
                    }
                }

                else -> {
                    content.add(Text(tk.second))
                    tokensLeft.removeFirst()
                }
            }
        }


        return content
    }

    private fun readTag(tag: Int, tokens: List<Token>): Pair<LineContent, Int>? {
        val index = tokens.indexOfFirst { it.id == tag }
        if (index == -1) return null

        repeat(index) {
            when (tokens[it].id) {
                SPACE, WORD -> Unit
                else -> return null
            }
        }

        val content = tokens.subList(0, index).joinToString("") { it.content }

        val lineContent = when (tag) {
            ASTERISK1 -> Bold(content)
            ASTERISK2 -> Italic(content)
            ASTERISK3 -> BoldItalic(content)
            else -> Bold(content)
        }
        return lineContent to index + 1
    }

    private fun readLink(tokens: List<Token>): Pair<LineContent, Int>? {
        val txtIndex = tokens.indexOfFirst { it.id == RIGHT_BRACE }
        if (txtIndex == -1) return null

        repeat(txtIndex) {
            when (tokens[it].id) {
                SPACE, WORD -> Unit
                else -> return null
            }
        }

        if (tokens[txtIndex + 1].id != LEFT_PAREN) return null

        val rest = tokens.subList(txtIndex + 2, tokens.size)

        val urlIndex = rest.indexOfFirst { it.id == RIGHT_PAREN }
        if (urlIndex == -1) return null

        val text = tokens.subList(0, txtIndex).joinToString("") { it.content }
        val url = rest.subList(0, urlIndex).joinToString("") { it.content }

        return Link(text, url) to txtIndex + urlIndex + 3
    }
}

object MarkdownLexer {

    fun nextToken(buff: StringBuilder): Token {
        if (buff.isEmpty()) return EOF to "EOF"

        val char = buff.pop()

        return when (char) {
            ' ' -> trimSpaces(buff)
            '#' -> readHeader(buff)
            '-' -> '-'.toInt() to "-"
            '=' -> '='.toInt() to "="
            '[' -> '['.toInt() to "["
            ']' -> ']'.toInt() to "]"
            '(' -> '('.toInt() to "("
            ')' -> ')'.toInt() to ")"
            '*' -> readAsterisk(buff)
            '\n' -> readEndLine(buff)
            else -> readWord(char, buff)
        }
    }

    private fun readWord(firstChar: Char, buff: StringBuilder): Token {
        val acc = StringBuilder()
        var shouldBreak = false

        acc.append(firstChar)

        while (!shouldBreak && buff.isNotEmpty()) {
            val char = buff[0]

            when (char) {
                ' ', '#', '-', '=', '*', '\n', '[', ']', '(', ')' -> shouldBreak = true
                else -> {
                    buff.pop()
                    acc.append(char)
                }
            }
        }
        return WORD to acc.toString()
    }

    private fun readAsterisk(buff: StringBuilder): Token {
        if (buff[0] == '*' && buff[1] == '*') {
            buff.pop(2)
            return ASTERISK3 to "***"
        }

        if (buff[0] == '*') {
            buff.pop()
            return ASTERISK2 to "**"
        }

        return ASTERISK1 to "*"
    }

    private fun readEndLine(buff: StringBuilder): Token {

        if (buff.isNotEmpty() && buff[0] == '\n') {
            while (buff.isNotEmpty() && buff[0] == '\n') {
                buff.pop()
            }

            return PARAGRAPH_END to "\n"
        }
        return LINE_END to "\n"
    }

    private fun trimSpaces(buff: StringBuilder): Token {
        while (buff.isNotEmpty() && buff[0] == ' ') {
            buff.pop()
        }
        return ' '.toInt() to " "
    }

    private fun readHeader(buff: StringBuilder): Token {
        var level = 1
        for (i in 0..4) {
            if (buff[i] != '#') {
                break
            } else {
                level++
            }
        }
        buff.pop(level - 1)
        return H1 + (level - 1) to "#" * level
    }
}

class TokenBuffer(startBuff: StringBuilder) {
    val buff: StringBuilder = startBuff
    val buffered = ArrayDeque<Token>()

    fun read(): Token {
        return if (buffered.isEmpty()) {
            MarkdownLexer.nextToken(buff)
        } else {
            buffered.removeFirst()
        }
    }

    fun returnToken(tk: Token) {
        buffered.addFirst(tk)
    }
}

private fun StringBuilder.pop(amount: Int = 1): Char {
    val char = this[0]
    delete(0, amount)
    return char
}

operator fun String.times(count: Int): String {
    return (1..count).joinToString("") { this }
}