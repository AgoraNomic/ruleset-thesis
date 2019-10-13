package org.agoranomic.ruleset_thesis.dependency_map

import org.agoranomic.ruleset_thesis.model.RuleNumber
import org.agoranomic.ruleset_thesis.util.getOrFail

private sealed class RuleDependencyMapImplBase : RuleDependencyMap {
    internal abstract fun rawMap(): Map<RuleNumber, Set<RuleNumber>>

    override fun toString(): String {
        return rawMap().map { (rule, dependencies) ->
            "$rule :: " + dependencies.joinToString(", ") { it.toString() }
        }.joinToString("\n")
    }

    final override fun directDependenciesOf(rule: RuleNumber): Set<RuleNumber> {
        return rawMap()[rule]?.toSet() ?: emptySet()
    }

    final override fun directDependentsOf(rule: RuleNumber): Set<RuleNumber> {
        return rawMap().filterValues { it.contains(rule) }.keys.toSet()
    }

    final override fun hasDirectDependencyOn(from: RuleNumber, to: RuleNumber): Boolean {
        return rawMap()[from]?.contains(to) ?: false
    }

    private val indirectDependenciesCache: MutableMap<RuleNumber, MutableMap<RuleNumber, Boolean>> = mutableMapOf()
    private val routesCache: MutableMap<RuleNumber, MutableMap<RuleNumber, List<RuleNumber>?>> = mutableMapOf()

    private fun routeToImpl(from: RuleNumber, to: RuleNumber, seen: List<RuleNumber>): List<RuleNumber>? {
        if (from == to) return listOf(to)

        if (routesCache.containsKey(from)) {
            val routeTo = routesCache.getOrFail(from)
            if (routeTo.containsKey(to)) {
                return routeTo.getOrFail(to)
            }
        }

        val route = run {
            val dependencies = directDependenciesOf(from)
            if (dependencies.contains(to)) return@run listOf(from, to)

            val unseenDependencies = dependencies.filter { !seen.contains(it) }
            if (unseenDependencies.isEmpty()) return@run null

            val updatedSeen = seen + unseenDependencies

            val dependencyRoutes = unseenDependencies.map { routeToImpl(it, to, updatedSeen) }
            val nonnullRoutes = dependencyRoutes.filterNotNull()
            if (nonnullRoutes.isNotEmpty()) {
                return@run listOf(from) + nonnullRoutes.first()
            }

            return@run null
        }

        routesCache.computeIfAbsent(from) { mutableMapOf() }[to] = route

        return route
    }

    override fun route(from: RuleNumber, to: RuleNumber): List<RuleNumber>? {
        return routeToImpl(from, to, emptyList())
    }

    private fun hasIndirectDependencyOnImpl(from: RuleNumber, to: RuleNumber, seen: List<RuleNumber>): Boolean {
        if (indirectDependenciesCache.containsKey(from)) {
            val subMap = indirectDependenciesCache.getOrFail(from)
            if (subMap.containsKey(to)) {
                return subMap.getOrFail(to)
            }
        }

        val result = run {
            if (from == to) return@run false

            val dependencies = directDependenciesOf(from)
            if (dependencies.contains(to)) return@run true

            val pendingDependencies = dependencies.filter { !seen.contains(it) }

            val updatedSeen = seen.toMutableList()

            for (dependency in pendingDependencies) {
                updatedSeen += dependency
                if (hasIndirectDependencyOnImpl(dependency, to, updatedSeen)) return@run true
            }

            return@run false
        }

        indirectDependenciesCache.computeIfAbsent(from) { mutableMapOf() }[to] = result

        return result
    }

    final override fun hasIndirectDependencyOn(from: RuleNumber, to: RuleNumber): Boolean {
        return hasIndirectDependencyOnImpl(from, to, emptyList())
    }

    override fun allDependents(): Set<RuleNumber> {
        return rawMap().keys.toSet()
    }

    override fun allDependencies(): Set<RuleNumber> {
        return rawMap().values.flatten().toSet()
    }

    protected fun invalidate() {
        indirectDependenciesCache.clear()
        routesCache.clear()
    }

    override fun equals(other: Any?): Boolean {
        if (other is RuleDependencyMapImplBase) {
            return this.rawMap() == other.rawMap()
        }

        if (other is RuleDependencyMap) {
            val thisDependents = this.allDependents()
            if (other.allDependents() != thisDependents) return false

            for (rule in thisDependents) {
                if (this.directDependenciesOf(rule) != other.directDependenciesOf(rule)) return false
            }
        }

        return false
    }
}

private class MutableRuleDependencyMapImpl : RuleDependencyMapImplBase(), MutableRuleDependencyMap {
    private val dependencies: MutableMap<RuleNumber, MutableSet<RuleNumber>> = mutableMapOf()

    private fun removeEmptyDependents() {
        invalidate()
        val dependents = allDependents() // Must get a copy because we are going to mutate the set

        for (dependent in dependents) {
            if (dependencies.getOrFail(dependent).isEmpty()) {
                dependencies.remove(dependent)
            }
        }
    }

    override fun addDependencyOn(from: RuleNumber, to: RuleNumber) {
        invalidate()

        if (from == to) return
        dependencies.computeIfAbsent(from) { mutableSetOf() }.add(to)
    }

    override fun removeDependencyOn(from: RuleNumber, to: RuleNumber) {
        invalidate()

        dependencies[from]?.remove(to)

        removeEmptyDependents()
    }

    override fun removeRule(rule: RuleNumber) {
        invalidate()

        dependencies.remove(rule)
        dependencies.values.forEach { it.remove(rule) }

        removeEmptyDependents()
    }

    override fun rawMap(): Map<RuleNumber, Set<RuleNumber>> {
        return dependencies
    }
}

fun emptyMutableRuleDependencyMap(): MutableRuleDependencyMap {
    return MutableRuleDependencyMapImpl()
}

fun mutableRuleDependencyMapOf(vararg pairs: Pair<RuleNumber, RuleNumber>): MutableRuleDependencyMap {
    val map = emptyMutableRuleDependencyMap()
    pairs.forEach { map.addDependencyOn(it.first, it.second) }

    return map
}

fun ruleDependencyMapOf(vararg pairs: Pair<RuleNumber, RuleNumber>): RuleDependencyMap = mutableRuleDependencyMapOf(*pairs).immutableCopy()

fun RuleDependencyMap.mutableCopy(): MutableRuleDependencyMap {
    val result = emptyMutableRuleDependencyMap()

    allDependents().forEach { dependent ->
        directDependenciesOf(dependent).forEach { dependency ->
            result.addDependencyOn(dependent, dependency)
        }
    }

    return result
}

private class ImmutableRuleDependencyMapImpl(toCopy: RuleDependencyMap) : RuleDependencyMapImplBase(), RuleDependencyMap {
    // This is safe, since the copy always copies all internal state, so we can just grab a reference to the new (copied) state
    private val dependencies: Map<RuleNumber, Set<RuleNumber>> = (toCopy.mutableCopy() as RuleDependencyMapImplBase).rawMap()

    override fun rawMap(): Map<RuleNumber, Set<RuleNumber>> {
        return dependencies
    }
}

fun RuleDependencyMap.immutableCopy(): RuleDependencyMap {
    if (this is ImmutableRuleDependencyMapImpl) return this

    return ImmutableRuleDependencyMapImpl(this)
}