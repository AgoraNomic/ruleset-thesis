package org.agoranomic.ruleset_thesis.dependency_map

import org.agoranomic.ruleset_thesis.model.RuleNumber

fun RuleDependencyMap.dependentCounts(): Map<RuleNumber, Int> {
    return involvedRules().associateWith { directDependentsOf(it).size }
}

fun RuleDependencyMap.dependencyCounts(): Map<RuleNumber, Int> {
    return involvedRules().associateWith { directDependenciesOf(it).size }
}

fun RuleDependencyMap.hasCircularDependency(rule: RuleNumber): Boolean {
    return directDependenciesOf(rule).any { hasIndirectDependencyOn(it, rule) }
}

fun RuleDependencyMap.circularDependencyRoute(rule: RuleNumber): List<RuleNumber>? {
    val dependencies = directDependenciesOf(rule)
    val routes = dependencies.map { route(it, rule) }

    if (routes.any { it != null }) {
        val firstRoute = routes.first { it != null }!!
        return listOf(rule) + firstRoute
    }

    return null
}

fun RuleDependencyMap.circularDependencyRoutes(): List<List<RuleNumber>> {
    val ret = mutableListOf<List<RuleNumber>>()

    for (rule in allDependents()) {
        if (ret.any { it.contains(rule) }) continue

        val route = circularDependencyRoute(rule)
        if (route != null) ret += route.dropLast(1)
    }

    return ret
}

fun RuleDependencyMap.rulesWithCircularDependencies(): Set<RuleNumber> {
    return involvedRules().filter { hasCircularDependency(it) }.toSet()
}

fun RuleDependencyMap.leaves(): Set<RuleNumber> {
    return involvedRules().filter { directDependentsOf(it).isEmpty() }.toSet()
}