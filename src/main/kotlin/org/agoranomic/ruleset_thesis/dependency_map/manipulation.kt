package org.agoranomic.ruleset_thesis.dependency_map

import org.agoranomic.ruleset_thesis.model.RuleNumber

fun RuleDependencyMap.withoutRule(rule: RuleNumber): RuleDependencyMap {
    return mutableCopy().also { it.removeRule(rule) }
}

fun MutableRuleDependencyMap.removeAll(predicate: (RuleNumber) -> Boolean) {
    for (rule in involvedRules()) {
        if (predicate(rule)) removeRule(rule)
    }
}

fun MutableRuleDependencyMap.retainAll(predicate: (RuleNumber) -> Boolean) {
    removeAll { !predicate(it) }
}

fun RuleDependencyMap.removeIf(predicate: (RuleNumber) -> Boolean): RuleDependencyMap {
    val copy = mutableCopy()
    copy.removeAll(predicate)
    return copy
}

fun RuleDependencyMap.retainIf(predicate: (RuleNumber) -> Boolean): RuleDependencyMap {
    val copy = mutableCopy()
    copy.retainAll(predicate)
    return copy
}

fun MutableRuleDependencyMap.removeDirectDependentsOf(dependency: RuleNumber) {
    removeAll { dependent -> hasDirectDependencyOn(dependent, dependency) }
}

fun RuleDependencyMap.withoutDirectDependentsOf(dependency: RuleNumber): RuleDependencyMap {
    val copy = mutableCopy()
    copy.removeDirectDependentsOf(dependency)
    return copy
}

fun MutableRuleDependencyMap.removeIndirectDependentsOf(dependency: RuleNumber) {
    removeAll { dependent -> hasIndirectDependencyOn(dependent, dependency)  }
}

fun RuleDependencyMap.withoutIndirectDependentsOf(rule: RuleNumber): RuleDependencyMap {
    val copy = mutableCopy()
    copy.removeIndirectDependentsOf(rule)
    return copy
}