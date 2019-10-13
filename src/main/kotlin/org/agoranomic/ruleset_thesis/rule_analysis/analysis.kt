package org.agoranomic.ruleset_thesis.rule_analysis

import org.agoranomic.ruleset_thesis.dependency_map.RuleDependencyMap
import org.agoranomic.ruleset_thesis.model.Rule
import org.agoranomic.ruleset_thesis.model.RuleNumber
import org.agoranomic.ruleset_thesis.util.getOrFail

fun underpoweredDependencies(rules: List<Rule>, dependencyMap: RuleDependencyMap): Map<RuleNumber, Set<RuleNumber>> {
    val ruleMap = rules.associateBy { it.number }

    return dependencyMap.involvedRules().map { dependent ->
        val dependentPower = ruleMap.getOrFail(dependent).power
        dependent to dependencyMap.directDependenciesOf(dependent).filter { dependency ->
            ruleMap.getOrFail(dependency).power < dependentPower
        }.toSet()
    }.toMap()
}
