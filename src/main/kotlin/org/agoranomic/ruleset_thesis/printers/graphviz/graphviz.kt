package org.agoranomic.ruleset_thesis.printers.graphviz

import org.agoranomic.ruleset_thesis.dependency_map.RuleDependencyMap
import org.agoranomic.ruleset_thesis.model.Rule
import org.agoranomic.ruleset_thesis.model.RuleNumber

// graphviz is located at https://graphviz.org

private fun graphvizDependencies(dependencyMap: RuleDependencyMap): String {
    val ret = StringBuilder()

    dependencyMap.allDependents().forEach { dependent ->
        val dependencies = dependencyMap.directDependenciesOf(dependent)
        ret.append(
            "    r$dependent -> ${dependencies.map { "r$it" }.joinToString(
                separator = " ",
                prefix = "{ ",
                postfix = " }"
            )}\n"
        )
    }

    return ret.toString()
}

typealias GraphvizMultiAttrFunc = (RuleNumber, RuleDependencyMap) -> List<GraphvizAttribute>

fun toGraphviz(rules: List<Rule>, dependencyMap: RuleDependencyMap, attrs: GraphvizMultiAttrFunc = { _, _ -> emptyList() }): String {
    val ruleMap = rules.associateBy { it.number }

    fun formatRuleLabel(rule: RuleNumber): String {
        val title = (ruleMap[rule] ?: error("No such rule: $rule")).title
        return "Rule $rule: $title"
    }

    val ret = StringBuilder()
    ret.append("digraph Rules {\n")

    val involvedRules = dependencyMap.involvedRules()

    for (rule in involvedRules) {
        val labelAttr = GraphvizStringAttribute(
            "label",
            formatRuleLabel(rule)
        )
        val allAttrs = attrs(rule, dependencyMap) + labelAttr
        val attrString = allAttrs.joinToString(" ") { "[${it.name} = ${it.stringValue}]" }

        ret.append("    r$rule $attrString\n")
    }

    ret.append(graphvizDependencies(dependencyMap))

    ret.append("}")

    return ret.toString()
}