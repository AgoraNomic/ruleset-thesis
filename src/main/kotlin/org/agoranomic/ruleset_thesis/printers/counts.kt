package org.agoranomic.ruleset_thesis.printers

import org.agoranomic.ruleset_thesis.dependency_map.RuleDependencyMap
import org.agoranomic.ruleset_thesis.model.Rule

fun toDependentCounts(rules: List<Rule>, dependencyMap: RuleDependencyMap): String {
    val dependentCounts = rules.associateWith { dependencyMap.directDependentsOf(it.number).size }

    return dependentCounts.toList().sortedByDescending { it.second }.joinToString("\n") { (rule, dependentCount) ->
        "Rule ${rule.number} (\"${rule.title}\") has $dependentCount dependents"
    }
}

fun toDependencyCounts(rules: List<Rule>, dependencyMap: RuleDependencyMap): String {
    val dependencyCounts = rules.associateWith { dependencyMap.directDependenciesOf(it.number).size }

    return dependencyCounts.toList().sortedByDescending { it.second }.joinToString("\n") { (rule, dependentCount) ->
        "Rule ${rule.number} (\"${rule.title}\") has $dependentCount dependencies"
    }
}