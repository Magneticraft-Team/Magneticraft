package com.cout970.magneticraft.gui.client.guide

import java.util.*

fun main(args: Array<String>) {
    MarkdownParser("""
        # Title
        ## Header 2
        ### Header 3
        #### Header 4
        ##### Header 5
        ###### Header 6
        ####### Header 7

        First       line

        - list
        - list2
        + also list

        *bold*
        **italics**
        ***bold_italics***
        _underline_

        Title and line
        ---

        title and line
        ===

        [Link](url)




        Text
        in
        only
        one
        paragraph

        Another paragraph

        """.trimIndent()).parse()
}

const val EOF = -1
const val H1 = 255
const val H2 = 256
const val H3 = 257
const val H4 = 258
const val H5 = 259
const val H6 = 260
const val SPACE = ' '.toInt()
const val UNUSED2 = 262
const val ASTERISK1 = '*'.toInt()
const val ASTERISK2 = 263
const val ASTERISK3 = 264
const val LINE_END = '\n'.toInt()
const val PARAGRAPH_END = 265
const val WORD = 266

typealias Token = Pair<Int, String>

data class Document(val paragraphs: List<Paragraph>) {
    override fun toString(): String {
        return paragraphs.joinToString("\n")
    }
}

sealed class Paragraph

data class Header(val level: Int, val line: Line) : Paragraph() {
    override fun toString(): String {
        return "${"#" * level} $line"
    }
}

data class LargeText(val lines: List<Line>) : Paragraph() {
    override fun toString(): String {
        return lines.joinToString("\n")
    }
}

object EmptyParagraph : Paragraph()

data class Line(val lineContent: List<LineContent>) {
    override fun toString(): String {
        return lineContent.joinToString("")
    }
}

sealed class LineContent

data class Text(val str: String) : LineContent() {
    override fun toString(): String {
        return str
    }
}

data class Bold(val txt: Text) : LineContent() {
    override fun toString(): String {
        return "Bold($txt)"
    }
}

data class Italic(val txt: Text) : LineContent() {
    override fun toString(): String {
        return "Italic($txt)"
    }
}

data class BoldItalic(val txt: Text) : LineContent() {
    override fun toString(): String {
        return "BoldItalic($txt)"
    }
}

data class Link(val txt: Text, val url: String) : LineContent() {
    override fun toString(): String {
        return "[$txt]($url)"
    }
}

class MarkdownParser(str: String) {

    val buff = TokenBuffer(StringBuilder(str))

    fun parse() {
        println(readDocument())
    }

    /* Basic Grammar

    document = paragraph*

    paragraph = header
              | large_text
              | list
              | dash
              | SPACE*
              ;

    header_mark = H1 | H2 | H3 | H4 | H5 | H6

    header = header_mark SPACE* line

    large_text = line* (PARAGRAPH_END | EOF)

    list = list_line* (PARAGRAPH_END | EOF)

    list_line = ('-' | '+' | '*') SPACE line

    line = line_content* END_LINE

    line_content = text | italic | bold | bold_italic | link

    text = header_mark | WORD | SPACE*

    phrase = WORD (SPACE* WORD)*

    bold = ASTERISK1 phrase ASTERISK1
    italic = ASTERISK2 phrase ASTERISK2
    bold_italic = ASTERISK3 phraseASTERISK3

    link = '[' phrase ']' '(' WORD ')'

     */

    fun readDocument(): Document {
        val list = readList(this::readParagraph)
        return Document(list)
    }

    fun readParagraph(): Paragraph? {
        readAll(SPACE)

        val header = readHeader()
        if (header != null) return header

        val largeText = readLargeText()
        if (largeText != null) return largeText

        val tk = buff.read()

        if (tk.first == EOF) return null

        return EmptyParagraph
    }

    fun readHeader(): Header? {
        val tk1 = buff.read()

        when (tk1.first) {
            H1, H2, H3, H4, H5, H6 -> Unit
            else -> {
                buff.returnToken(tk1)
                return null
            }
        }

        val level = tk1.first - H1 + 1

        val tk2 = buff.read()

        if (tk2.first != SPACE) {
            buff.returnToken(tk2)
            buff.returnToken(tk1)
            return null
        }

        readAll(SPACE)

        val line = readLine() ?: error("Expected line: $buff")

        return Header(level, line)
    }

    fun readLargeText(): LargeText? {
        val list = readList(this::readLine)
        if (list.isEmpty()) return null
        return LargeText(list)
    }

    fun readLine(): Line? {

        val content = readLineContent()

        val list =
                if (content != null)
                    listOf(content) + readList(this::readLineContent)
                else
                    emptyList()

        val tk = buff.read()

        if (tk.first != LINE_END && tk.first != PARAGRAPH_END) {
            if (content == null) {
                return null
            } else {
                error("Unfinished line: tk: $tk, buff: \n${buff.buff}")
            }
        }

        return Line(list)
    }

    fun readLineContent(): LineContent? {
        return readBold()
                ?: readItalic()
                ?: readBoldItalic()
                ?: readLink()
                ?: readText()
    }

    fun readBold(): Bold? {
        val tk = buff.read()

        if (tk.first != ASTERISK1) {
            buff.returnToken(tk)
            return null
        }

        val phrase = readPhrase()

        if (phrase == null) {
            buff.returnToken(tk)
            return null
        }
        buff.expect(ASTERISK1)
        return Bold(phrase)
    }

    fun readItalic(): Italic? {
        val tk = buff.read()

        if (tk.first != ASTERISK2) {
            buff.returnToken(tk)
            return null
        }

        val phrase = readPhrase()

        if (phrase == null) {
            buff.returnToken(tk)
            return null
        }
        buff.expect(ASTERISK2)
        return Italic(phrase)
    }

    fun readBoldItalic(): BoldItalic? {
        val tk = buff.read()

        if (tk.first != ASTERISK3) {
            buff.returnToken(tk)
            return null
        }

        val phrase = readPhrase()

        if (phrase == null) {
            buff.returnToken(tk)
            return null
        }
        buff.expect(ASTERISK3)
        return BoldItalic(phrase)
    }

    fun readPhrase(): Text? {
        var str = ""
        buff.save()
        val tk = buff.read()

        if (tk.first != WORD) {
            buff.load()
            return null
        }

        str += tk.second

        while (true) {
            val tk1 = buff.read()

            if (tk1.first != SPACE) {
                buff.returnToken(tk1)
                break
            }

            val spaces = readAll(SPACE)

            val tk2 = buff.read()

            if (tk2.first != WORD) {
                TODO()
//                buff.load()
//                return
                break
            }

            str += tk1.second
            str += tk2.second
        }

        return Text(str)
    }

    fun readLink(): LineContent? {
        val tk1 = buff.read()

        if (tk1.second != "[") {
            buff.returnToken(tk1)
            return null
        }

        val text = readPhrase()

        if (text == null) {
            buff.returnToken(tk1)
            return null
        }

        val tk2 = buff.expect(WORD)

        if (tk2.second != "]") {
            buff.returnToken(tk2)
            return Text("[${text.str}")
        }

        val tk3 = buff.read()

        if (tk3.second != "(") {
            buff.returnToken(tk3)
            return Text("[${text.str}]")
        }

        val tk4 = buff.expect(WORD)

        val tk5 = buff.read()

        if (tk5.second != ")") {
            buff.returnToken(tk5)
            return Text("[${text.str}](${tk4.second}")
        }

        return Link(text, tk4.second)
    }

    fun readText(): Text? {
        val tk = buff.read()

        return when (tk.first) {
            H1, H2, H3, H4, H5, H6, WORD, ASTERISK1, ASTERISK2, ASTERISK3 -> Text(tk.second)
            SPACE -> {
                readAll(SPACE)
                Text(" ")
            }
            else -> {
                buff.returnToken(tk)
                null
            }
        }
    }

    fun readAll(kind: Int) {
        var tk: Token

        do {
            tk = buff.read()
        } while (tk.first == kind)

        buff.returnToken(tk)
    }

    inline fun <T> readList(func: () -> T?): List<T> {
        val list = mutableListOf<T>()

        do {
            val p = func() ?: break
            list.add(p)
        } while (true)

        return list
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
            '*' -> readAsterisk(buff)
            '\n' -> readEndLine(buff)
            else -> readWord(char, buff)
        }
    }

    fun readWord(firstChar: Char, buff: StringBuilder): Token {
        val acc = StringBuilder()
        var shouldBreak = false

        acc.append(firstChar)

        while (!shouldBreak && buff.isNotEmpty()) {
            val char = buff[0]

            when (char) {
                ' ', '#', '-', '=', '*', '\n' -> shouldBreak = true
                else -> {
                    buff.pop()
                    acc.append(char)
                }
            }
        }
        return WORD to acc.toString()
    }

    fun readAsterisk(buff: StringBuilder): Token {
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

    fun readEndLine(buff: StringBuilder): Token {

        if (buff.isNotEmpty() && buff[0] == '\n') {
            while (buff.isNotEmpty() && buff[0] == '\n') {
                buff.pop()
            }

            return PARAGRAPH_END to "\n"
        }
        return LINE_END to "\n"
    }

    fun trimSpaces(buff: StringBuilder): Token {
        while (buff.isNotEmpty() && buff[0] == ' ') {
            buff.pop()
        }
        return ' '.toInt() to " "
    }

    fun readHeader(buff: StringBuilder): Token {
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
    private val stack = ArrayDeque<Pair<StringBuilder, ArrayDeque<Token>>>()
    var buff: StringBuilder = startBuff
    var buffered = ArrayDeque<Token>()

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

    fun expect(expected: Int): Token {
        val tk = read()
        if (tk.first != expected) {
            error("Expected: $expected but found: $tk\nRemaining Buffer:\n$buff")
        }
        return tk
    }

    fun save(){
        val newBuilder = StringBuilder(buff.toString())
        val newTokenBuffer = ArrayDeque(buffered)

        stack.push(newBuilder to newTokenBuffer)
    }

    fun load(){
        val (builder, buffer) = stack.pop()

        buff = builder
        buffered = buffer
    }
}

fun StringBuilder.pop(amount: Int = 1): Char {
    val char = this[0]
    delete(0, amount)
    return char
}

operator fun String.times(count: Int): String {
    return (1..count).joinToString("") { this }
}