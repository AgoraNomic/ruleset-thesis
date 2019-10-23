package org.agoranomic.ruleset_thesis.printers.graphviz

import org.agoranomic.ruleset_thesis.dependency_map.RuleDependencyMap
import org.agoranomic.ruleset_thesis.model.Rule

fun toGraphvizSimple(rules: List<Rule>, dependencyMap: RuleDependencyMap): String {
    return toGraphviz(
        rules,
        dependencyMap,
        multiAttrFunc(boxShapedAttrFunc(), filledAttrFunc())
    )
}

fun toGraphvizColoredOnCircularDependencies(rules: List<Rule>, dependencyMap: RuleDependencyMap): String {
    return toGraphviz(
        rules,
        dependencyMap,
        multiAttrFunc(boxShapedAttrFunc(), filledAttrFunc(), circularDependenciesColoredAttrFunc())
    )
}

fun toGraphvizColoredOnLeaves(rules: List<Rule>, dependencyMap: RuleDependencyMap): String {
    return toGraphviz(
        rules,
        dependencyMap,
        multiAttrFunc(boxShapedAttrFunc(), filledAttrFunc(), leavesColoredAttrFunc())
    )
}
