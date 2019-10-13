package org.agoranomic.ruleset_thesis.printers.graphviz

sealed class GraphvizAttribute(val name: String) {
    abstract val stringValue: String
}

class GraphvizStringAttribute(name: String, rawValue: String) : GraphvizAttribute(name) {
    override val stringValue: String = "\"${rawValue.replace("\"", "\\\"")}\""
}