package org.agoranomic.ruleset_thesis.dependency_parsing

import org.agoranomic.ruleset_thesis.dependency_map.*
import org.agoranomic.ruleset_thesis.model.Rule
import org.agoranomic.ruleset_thesis.model.RuleNumber

object ManualAdjustment {
    private val ignoredRules = listOf<RuleNumber>(2029, 1727, 2486)

    fun withoutIgnoredRules(rules: Collection<Rule>) = rules.filter { !ignoredRules.contains(it.number) }

    private val forceRemoveDependencies = listOf<Dependency>()

    private val forceAdditionDependencies = listOf<Dependency>()

    fun adjustDependencies(dependencyMap: RuleDependencyMap): RuleDependencyMap {
        val copy = dependencyMap.mutableCopy()

        forceRemoveDependencies.forEach { copy.removeDependency(it) }
        forceAdditionDependencies.forEach { copy.addDependency(it) }

        return copy
    }

    private data class StrongDependency(val checkedRule: RuleNumber, val requiredDependency: RuleNumber)

    // Strong dependencies: not considering any dependency on <from>, if a rule does not (in)directly depend on <to>, it does not actually depend on <from>
    private val strongDependencies = listOf(
        StrongDependency(2576, 2166),
        StrongDependency(2577, 2166),
        StrongDependency(2578, 2166),
        StrongDependency(2579, 2166),
        StrongDependency(2581, 649)
    )

    fun stripMissingStrongDependencies(dependencyMap: RuleDependencyMap): RuleDependencyMap {
        val result = dependencyMap.mutableCopy()

        for ((checkedRule, requiredDependency) in strongDependencies) {
            val workDependencyMap = dependencyMap.mutableCopy().also { it.removeRule(checkedRule) }

            for (dependent in dependencyMap.allDependents()) {
                if (dependencyMap.hasDirectDependencyOn(dependent, checkedRule) && !workDependencyMap.hasIndirectDependencyOn(dependent, requiredDependency)) {
                    result.removeDependencyOn(dependent, checkedRule)
                }
            }
        }

        return result
    }
}