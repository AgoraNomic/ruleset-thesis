package org.agoranomic.ruleset_thesis.printers

import org.agoranomic.ruleset_thesis.dependency_map.RuleDependencyMap
import org.agoranomic.ruleset_thesis.dependency_map.circularDependencyRoutes
import org.agoranomic.ruleset_thesis.model.Rule
import org.agoranomic.ruleset_thesis.rule_analysis.underpoweredDependencies

fun toCircularDependencyRoutes(dependencyMap: RuleDependencyMap): String {
    return dependencyMap.circularDependencyRoutes().map { it.joinToString(", ") }.joinToString("\n")
}

fun toUnderpoweredDependencies(rules: List<Rule>, dependencyMap: RuleDependencyMap): String {
    val underpoweredDependenciesMap = underpoweredDependencies(
        rules,
        dependencyMap
    ).filter { (_, dep) -> dep.isNotEmpty() }.toList().sortedByDescending { (_, dependencies) -> dependencies.size }

    return underpoweredDependenciesMap.map { (r, dep) -> "Underpowered dependencies of Rule $r: $dep" }.joinToString("\n")
}