package org.agoranomic.ruleset_thesis.printers.graphviz

import org.agoranomic.ruleset_thesis.dependency_map.RuleDependencyMap
import org.agoranomic.ruleset_thesis.dependency_map.hasCircularDependency
import org.agoranomic.ruleset_thesis.model.RuleNumber

typealias GraphvizAttrFunc = (RuleNumber, RuleDependencyMap) -> GraphvizAttribute?

private fun valueColorAttrFunc(extractValue: (RuleNumber, RuleDependencyMap) -> Int): GraphvizAttrFunc {

    return { rule, dependencyMap ->
        val max = dependencyMap.involvedRules().map { extractValue(it, dependencyMap) }.max() ?: 1

        val value = extractValue(rule, dependencyMap)
        val green = ((value.toFloat() / max.toFloat()) * 255).toInt()
        val red = 255 - green

        GraphvizStringAttribute(
            "fillcolor",
            "#%02x%02x%02x".format(red, green, 0)
        )
    }
}

fun dependentsColorAttrFunc() = valueColorAttrFunc { r, d -> d.directDependentsOf(r).size }

fun boxShapedAttr() = GraphvizStringAttribute("shape", "box")
fun boxShapedAttrFunc() = simpleAttrFunc(boxShapedAttr())

fun filledAttr() = GraphvizStringAttribute("style", "filled")
fun filledAttrFunc() = simpleAttrFunc(filledAttr())

fun redColoredAttr() = GraphvizStringAttribute("color", "#FF0000")
fun redColoredAttrFunc() = simpleAttrFunc(redColoredAttr())

fun greenColoredAttr() = GraphvizStringAttribute("color", "#00FF00")
fun greenColoredAttrFunc() = simpleAttrFunc(greenColoredAttr())

fun leavesColoredAttrFunc(): GraphvizAttrFunc = { r, d ->
    if (d.directDependentsOf(r).isEmpty()) GraphvizStringAttribute(
        "fillcolor",
        "#29e6f0"
    ) else null
}

fun circularDependenciesColoredAttrFunc(): GraphvizAttrFunc {
    return { rule, dependencyMap ->
        if (dependencyMap.hasCircularDependency(rule)) redColoredAttr() else greenColoredAttr()
    }
}

fun simpleAttrFunc(attr: GraphvizAttribute): GraphvizAttrFunc = { _, _ -> attr }

@JvmName("joinAttrsFunc")
fun multiAttrFunc(vararg attrs: GraphvizAttribute): GraphvizMultiAttrFunc {
    return { _, _ -> attrs.toList() }
}

@JvmName("joinAttrFuncsFunc")
fun multiAttrFunc(vararg funcs: GraphvizAttrFunc): GraphvizMultiAttrFunc {
    return { r, d -> funcs.mapNotNull { it(r, d) } }
}

@JvmName("joinMultiAttrFuncsFunc")
fun multiAttrFunc(vararg funcs: GraphvizMultiAttrFunc): GraphvizMultiAttrFunc {
    return { r, d -> funcs.flatMap { it(r, d) } }
}