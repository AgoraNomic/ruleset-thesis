package org.agoranomic.ruleset_thesis

import org.agoranomic.ruleset_thesis.dependency_map.immutableCopy
import org.agoranomic.ruleset_thesis.dependency_map.mutableCopy
import org.agoranomic.ruleset_thesis.dependency_parsing.ManualAdjustment
import org.agoranomic.ruleset_thesis.dependency_parsing.parseDependencies
import org.agoranomic.ruleset_thesis.parsing.parseRuleList
import java.io.File
import java.nio.file.Files

private const val REMOVE_HIGH_FREQ_RULES = true

fun main() {
    val text = Files.readString(File("rules.txt").toPath())
    val rules = ManualAdjustment.withoutIgnoredRules(
        parseRuleList(
            text
        ).map { it.rules }.flatten()
    )
    val ruleByNumber = rules.associateBy { it.number }

    val dependencyMap = ManualAdjustment.stripMissingStrongDependencies(
        ManualAdjustment.adjustDependencies(
            parseDependencies(rules)
        )
    ).mutableCopy().also {
        if (REMOVE_HIGH_FREQ_RULES) {
            it.removeRule(2152)
            it.removeRule(869)
            it.removeRule(2141)
            it.removeRule(478)
            it.removeRule(101)
            it.removeRule(1023)
        }
    }.immutableCopy()

    // Doing stuff with dependencyMap is left as an exercise to the reader
    // Check out the "printers" and dependency_map/analysis.kt for ideas.
}