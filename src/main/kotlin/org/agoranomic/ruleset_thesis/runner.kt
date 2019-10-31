package org.agoranomic.ruleset_thesis

import org.agoranomic.ruleset_thesis.dependency_map.*
import org.agoranomic.ruleset_thesis.dependency_parsing.ManualAdjustment
import org.agoranomic.ruleset_thesis.dependency_parsing.parseDependencies
import org.agoranomic.ruleset_thesis.parsing.parseRuleList
import org.agoranomic.ruleset_thesis.printers.graphviz.*
import org.agoranomic.ruleset_thesis.printers.toCircularDependencyRoutes
import org.agoranomic.ruleset_thesis.printers.toDependencyCounts
import org.agoranomic.ruleset_thesis.printers.toDependentCounts
import org.agoranomic.ruleset_thesis.printers.toUnderpoweredDependencies
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

    outputGraph("total", toGraphvizSimple(rules, totalDependencyMap))
    outputGraph("simplified", toGraphvizSimple(rules, shortDependencyMap))
    outputGraph("cycles", toGraphvizColoredOnCircularDependencies(rules, shortDependencyMap))
    outputGraph("leaves", toGraphvizColoredOnLeaves(rules, shortDependencyMap))
    outputGraph("without_circular", toGraphvizSimple(rules, totalDependencyMap.withoutCircularDependencies()))

    outputText("dependent_counts", toDependentCounts(rules, totalDependencyMap))
    outputText("dependency_counts", toDependencyCounts(rules, totalDependencyMap))
    outputText("circular_dependencies", toCircularDependencyRoutes(totalDependencyMap))
    outputText("underpowered_dependencies", toUnderpoweredDependencies(rules, totalDependencyMap))

    for (rule in listOf(2152, 869, 1023, 478)) {
        outputText("dependents_$rule", totalDependencyMap.directDependentsOf(rule).joinToString("\n"))
    }
}