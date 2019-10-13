package org.agoranomic.ruleset_thesis.parsing

import org.agoranomic.ruleset_thesis.model.*
import java.math.BigDecimal

private data class SectionHeader(val title: String, val description: String)

private fun Section(header: SectionHeader, rules: List<Rule>) = Section(
    header.title,
    header.description,
    rules
)

private data class RuleHeader(val number: RuleNumber, val power: RulePower, val title: RuleTitle)

private fun Rule(header: RuleHeader, text: String) = Rule(
    header.number,
    header.power,
    header.title,
    text
)

private fun firstDecimalLength(string: String): Int {
    val numberString = string.takeWhile { listOf('-', '+', '.').contains(it) || it.isDigit() }
    return numberString.length
}

private fun parseFirstDecimal(string: String): BigDecimal {
    val length = firstDecimalLength(string)
    require(length > 0)
    return BigDecimal(string.substring(0, length))
}

private fun firstIntLength(string: String): Int {
    val numberString = string.takeWhile { listOf('-', '+').contains(it) || it.isDigit() }
    return numberString.length
}

private fun parseFirstInt(string: String): Int {
    val length = firstIntLength(string)
    require(length > 0) { "String has no first int" }
    return string.substring(0, length).toInt()
}

private fun parseSectionHeader(rawText: String): SectionHeader {
    val text = rawText.trim()
    val lines = text.lines()

    val title = lines.first()
    val description = lines.drop(1).map { it.trimIndent() }.joinToString(" ")

    return SectionHeader(title, description)
}

private fun parseRuleHeader(rawFirstLine: String, rawSecondLine: String): RuleHeader {
    val firstLine = rawFirstLine.trim()
    val secondLine = rawSecondLine.trim()

    require(firstLine.startsWith("Rule "))
    val firstLineInfo = firstLine.substringAfter("Rule ")

    val idLength = firstIntLength(firstLineInfo)
    val id = parseFirstInt(firstLineInfo)
    val textAfterID = firstLineInfo.substring(idLength)

    require(textAfterID.startsWith("/"))
    val textAfterSlash = textAfterID.substringAfter("/")

    val revisionNumberLength = firstIntLength(textAfterSlash)
    val revisionNumber = parseFirstInt(textAfterSlash)
    val textAfterRevision = textAfterSlash.substring(revisionNumberLength)

    require(textAfterRevision.startsWith(" (Power="))
    val textAfterEqual = textAfterRevision.substringAfter(" (Power=")

    val powerLength = firstDecimalLength(textAfterEqual)
    val power = parseFirstDecimal(textAfterEqual)
    val textAfterPower = textAfterEqual.substring(powerLength)

    require(textAfterPower == ")")

    val title = secondLine

    return RuleHeader(id, power, title)
}

private fun parseRule(rawText: String): Rule {
    val text = rawText.trim()
    val lines = text.lines()

    val headerFirst = lines.first()
    val headerSecond = lines[1]

    val header = parseRuleHeader(headerFirst, headerSecond)

    val bodyLines = lines.drop(2).map { it.trimIndent().trim() }
    //    val paragraphs = mutableListOf<String>()
    //
    //    var currentParagraph = ""
    //
    //    for (textLine in textLines) {
    //        if (textLine.isNotBlank()) {
    //            currentParagraph += " " + textLine
    //        } else {
    //            paragraphs += (currentParagraph.trim())
    //            currentParagraph = ""
    //        }
    //    }

    val body = bodyLines.joinToString("\n")
    return Rule(header, body)
}

private fun parseSection(rawText: String): Section {
    val text = rawText.trim()
    val parts = text.split("------------------------------------------------------------------------").map { it.trim() }
        .filter { it.isNotBlank() }

    val header = parseSectionHeader(parts.first())
    val ruleTexts = parts.drop(1)

    val rules = ruleTexts.map { parseRule(it) }
    return Section(header, rules)
}

fun parseRuleList(rawText: String): List<Section> {
    val text = rawText.trim()

    val sectionTexts = text.split("========================================================================")
        .map { it.trim() }.filter { it.isNotBlank() }
    return sectionTexts.map { parseSection(it) }
}