package org.agoranomic.ruleset_thesis.dependency_map

import org.agoranomic.ruleset_thesis.model.RuleNumber

interface RuleDependencyMap {
    /**
     * Returns a set containing all rules on which `rule` has a direct dependency.
     * The returned set is disjoint from this instance - changes in this instance will not be reflected in the set.
     *
     * @param rule the rule whose dependencies will be returned.
     * @returns a set containing all rules on which `rule` has a direct dependency
     */
    fun directDependenciesOf(rule: RuleNumber): Set<RuleNumber>

    /**
     * Returns whether or not `from` has a direct dependency on `to`.
     *
     * @param from the rule to test for having a direct dependency on `to`.
     * @param to the rule to test for `from` having a direct dependency on it.
     * @return whether or not `from` has a direct dependency on `to`
     */
    fun hasDirectDependencyOn(from: RuleNumber, to: RuleNumber): Boolean = directDependenciesOf(from).contains(to)

    /**
     * Returns whether or not `from` has an indirect dependency on `to`.
     *
     * `A` has an indirect dependency on `B` if and only if `A` has a direct dependency on `B` or `A` has a
     * direct dependency on a rule that has an indirect dependency on `B`.
     *
     * @param from the rule to test for having an indirect dependency on `to`.
     * @param to the rule to test for `from` having an indirect dependency on it.
     * @return whether or not `from` has an indirect dependency on `to`
     */
    fun hasIndirectDependencyOn(from: RuleNumber, to: RuleNumber): Boolean

    /**
     * Returns the set of all rules that have a direct dependency on `rule`.
     * The returned set is disjoint from this instance - changes in this instance will not be reflected in the set.
     *
     * @param rule the rule whose dependents will be returned.
     * @return the set of all rules that have a direct dependency on `rule`
     */
    fun directDependentsOf(rule: RuleNumber): Set<RuleNumber>

    /**
     * Returns a Set containing all rules that have dependencies.
     * The returned set is disjoint from this instance - changes in this instance will not be reflected in the set.
     *
     * @return a Set containing all rules that have dependencies
     */
    fun allDependents(): Set<RuleNumber>

    /**
     * Returns a Set containing all rules that are depended upon.
     * The returned set is disjoint from this instance - changes in this instance will not be reflected in the set.
     *
     * @return a Set containing all rules that are depended upon
     */
    fun allDependencies(): Set<RuleNumber>

    /**
     * Returns a Set containing all rules that have dependencies or have which are depended upon.
     * The returned set is disjoint from this instance - changes in this instance will not be reflected in the set.
     *
     * @return a Set containing all rules that have dependencies or have which are depended upon
     */
    fun involvedRules(): Set<RuleNumber> = allDependents() + allDependencies()

    /**
     * Returns a List containing a path from the rule `from` to the rule `to`, or null if there is no such route.
     *
     * If the return value is not null, the first element is `from` and the last element is `to`.
     * If `from == to`, the return value is a list containing the single element `to`, even if this instance has no knowledge of `to`.
     *
     * @return a List containing a path from the rule `from` to the rule `to`, or null if there is no such route.
     */
    fun route(from: RuleNumber, to: RuleNumber): List<RuleNumber>?
}

interface MutableRuleDependencyMap : RuleDependencyMap {
    /**
     * Adds a dependency from `from` to `to`. Has no effect if `from == to`.
     *
     * @param from the dependent
     * @param to the dependency
     */
    fun addDependencyOn(from: RuleNumber, to: RuleNumber)

    /**
     * Removes a dependency from `from` to `to`. Has no effect if there is no dependency.
     *
     * @param from the dependent
     * @param to the dependency
     */
    fun removeDependencyOn(from: RuleNumber, to: RuleNumber)

    /**
     * Removes all dependency relationships that involve `rule`.
     *
     * @param rule the rule to remove
     */
    fun removeRule(rule: RuleNumber)
}