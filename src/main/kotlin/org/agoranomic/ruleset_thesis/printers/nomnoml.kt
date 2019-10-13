package org.agoranomic.ruleset_thesis.printers

import org.agoranomic.ruleset_thesis.dependency_map.RuleDependencyMap
import org.agoranomic.ruleset_thesis.model.Rule
import org.agoranomic.ruleset_thesis.model.RuleNumber

// nomnoml is located at: http://nomnoml.com

fun toNomnoml(rules: List<Rule>, dependencyMap: RuleDependencyMap): String {
    val ruleMap = rules.associateBy { it.number }

    fun formatRule(rule: RuleNumber): String {
        val title = (ruleMap[rule] ?: error("No such rule: $rule")).title
        return "[$rule: \"$title\"]"
    }

    return dependencyMap.allDependents().map { dependent ->
        dependencyMap.directDependenciesOf(dependent).joinToString("\n") { dependency ->
            "${formatRule(dependency)}<-${formatRule(dependent)}"
        }
    }.filter { it.isNotBlank() }.joinToString("\n")
}
