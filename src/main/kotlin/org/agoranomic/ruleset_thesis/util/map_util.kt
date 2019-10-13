package org.agoranomic.ruleset_thesis.util

fun <K, V> Map<K, V>.getOrFail(key: K): V {
    if (containsKey(key)) return (get(key) as V)

    error("Missing expected key: $key")
}