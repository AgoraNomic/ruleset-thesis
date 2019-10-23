package org.agoranomic.ruleset_thesis

import org.agoranomic.ruleset_thesis.dependency_map.immutableCopy
import org.agoranomic.ruleset_thesis.dependency_map.mutableCopy
import org.agoranomic.ruleset_thesis.dependency_parsing.ManualAdjustment
import org.agoranomic.ruleset_thesis.dependency_parsing.parseDependencies
import org.agoranomic.ruleset_thesis.parsing.parseRuleList
import org.agoranomic.ruleset_thesis.printers.graphviz.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

private fun readSLR(path: Path) = parseRuleList(Files.readString(path)).map { it.rules }.flatten()

fun main() {
    val rules = ManualAdjustment.withoutIgnoredRules(readSLR(Path.of("rules.txt")))
    val ruleByNumber = rules.associateBy { it.number }

    val totalDependencyMap = ManualAdjustment.stripMissingStrongDependencies(
        ManualAdjustment.adjustDependencies(
            parseDependencies(rules)
        )
    )

    val shortDependencyMap = totalDependencyMap.mutableCopy().also {
        it.removeRule(2152)
        it.removeRule(869)
        it.removeRule(2141)
        it.removeRule(478)
        it.removeRule(101)
        it.removeRule(1023)
    }.immutableCopy()

    // Doing stuff with dependencyMap is left as an exercise to the reader
    // Check out the "printers" and dependency_map/analysis.kt for ideas.
}