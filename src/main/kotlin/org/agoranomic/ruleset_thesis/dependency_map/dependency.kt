package org.agoranomic.ruleset_thesis.dependency_map

import org.agoranomic.ruleset_thesis.model.RuleNumber

data class Dependency(val dependent: RuleNumber, val dependency: RuleNumber)

fun MutableRuleDependencyMap.addDependency(d: Dependency) {
    addDependencyOn(d.dependent, d.dependency)
}

fun MutableRuleDependencyMap.removeDependency(d: Dependency) {
    removeDependencyOn(d.dependent, d.dependency)
}

fun mutableRuleDependencyMapOf(vararg dependencies: Dependency): MutableRuleDependencyMap {
    val map = emptyMutableRuleDependencyMap()
    dependencies.forEach { map.addDependency(it) }

    return map
}

fun ruleDependencyMapOf(vararg dependencies: Dependency): RuleDependencyMap = mutableRuleDependencyMapOf(*dependencies).immutableCopy()