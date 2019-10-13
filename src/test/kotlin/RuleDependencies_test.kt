import org.agoranomic.ruleset_thesis.dependency_map.*
import org.agoranomic.ruleset_thesis.model.RuleNumber
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
sealed class RuleDependenciesTestEngine {
    private var dummyCounter = 0

    private fun nextDummyValue() = dummyCounter++

    fun nextDummyRuleNumber(): RuleNumber = nextDummyValue()
}

class `RuleDependencies tests` : RuleDependenciesTestEngine() {
    @Nested
    inner class `after default creation` {
        @Test
        fun `has no rules`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            assertEmpty(dependencyMap.involvedRules())
        }

        @Test
        fun `rule has no dependencies`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            assertEmpty(dependencyMap.directDependenciesOf(nextDummyRuleNumber()))
        }

        @Test
        fun `rule has no dependents`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            assertEmpty(dependencyMap.directDependentsOf(nextDummyRuleNumber()))
        }

        @Test
        fun `rule has no indirect dependencies`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()

            assertFalse(dependencyMap.hasIndirectDependencyOn(firstRule, secondRule))
        }
    }

    @Nested
    inner class `after adding non-self dependency` {
        @Test
        fun `has two known rules`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(firstRule, secondRule)
            assertEquals(dependencyMap.involvedRules(), setOf(firstRule, secondRule))
            assertEquals(dependencyMap.allDependents(), setOf(firstRule))
            assertEquals(dependencyMap.allDependencies(), setOf(secondRule))
        }

        @Test
        fun `rule has dependency on other`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(firstRule, secondRule)
            assertEquals(dependencyMap.directDependenciesOf(firstRule), setOf(secondRule))
            assertEquals(dependencyMap.directDependentsOf(secondRule), setOf(firstRule))
        }

        @Test
        fun `no random dependencies`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(firstRule, secondRule)

            val thirdRule = nextDummyRuleNumber()
            assertEmpty(dependencyMap.directDependenciesOf(thirdRule))
            assertEmpty(dependencyMap.directDependentsOf(thirdRule))
        }
    }

    @Nested
    inner class `after adding self dependency` {
        @Test
        fun `has no rules`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val rule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(rule, rule)

            assertEmpty(dependencyMap.allDependents())
            assertEmpty(dependencyMap.allDependencies())
            assertEmpty(dependencyMap.involvedRules())
        }

        @Test
        fun `rule has no dependencies`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val rule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(rule, rule)

            assertFalse(dependencyMap.hasDirectDependencyOn(rule, rule))
            assertFalse(dependencyMap.hasIndirectDependencyOn(rule, rule))
            assertEmpty(dependencyMap.directDependenciesOf(rule))
            assertEmpty(dependencyMap.directDependentsOf(rule))
        }

        @Test
        fun `rule has no circular dependency`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val rule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(rule, rule)

            assertFalse(dependencyMap.hasCircularDependency(rule))
        }
    }

    @Nested
    inner class `equality tests` {
        @Test
        fun `empty instances are equal`() {
            assertEquals(
                emptyMutableRuleDependencyMap(),
                emptyMutableRuleDependencyMap()
            )
        }

        @Test
        fun `instances are equal with same single dependency`() {
            val firstDependencyMap = emptyMutableRuleDependencyMap()
            val secondDependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()

            firstDependencyMap.addDependencyOn(firstRule, secondRule)
            secondDependencyMap.addDependencyOn(firstRule, secondRule)

            assertEquals(firstDependencyMap, secondDependencyMap)
        }

        @Test
        fun `instance with dependency not equal to empty instance`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(firstRule, secondRule)

            assertNotEquals(emptyMutableRuleDependencyMap(), dependencyMap)
        }

        @Test
        fun `instances with same dependencies in different order are equal`() {
            val firstDependencyMap = emptyMutableRuleDependencyMap()
            val secondDependencyMap = emptyMutableRuleDependencyMap()

            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()

            val thirdRule = nextDummyRuleNumber()
            val fourthRule = nextDummyRuleNumber()

            firstDependencyMap.addDependencyOn(firstRule, secondRule)
            secondDependencyMap.addDependencyOn(thirdRule, fourthRule)

            firstDependencyMap.addDependencyOn(thirdRule, fourthRule)
            secondDependencyMap.addDependencyOn(firstRule, secondRule)

            assertEquals(firstDependencyMap, secondDependencyMap)
        }
    }

    @Nested
    inner class `copy tests` {
        @Test
        fun `instance equal with mutable copy`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(firstRule, secondRule)

            assertEquals(dependencyMap, dependencyMap.mutableCopy())
        }

        @Test
        fun `instance equal with immutable copy`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(firstRule, secondRule)

            assertEquals(dependencyMap, dependencyMap.immutableCopy())
        }

        @Test
        fun `instance disjoint with mutable copy`() {
            val original = emptyMutableRuleDependencyMap()
            val copy = original.mutableCopy()

            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()
            original.addDependencyOn(firstRule, secondRule)

            assertNotEquals(original, copy)
        }

        @Test
        fun `instance disjoint with immutable copy`() {
            val original = emptyMutableRuleDependencyMap()
            val copy = original.immutableCopy()

            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()
            original.addDependencyOn(firstRule, secondRule)

            assertNotEquals(original, copy)
        }
    }

    @Nested
    inner class `indirect dependencies tests` {
        @Test
        fun `has indirect dependency on direct dependency`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(firstRule, secondRule)
            assertTrue(dependencyMap.hasIndirectDependencyOn(firstRule, secondRule))
        }
    }

    @Nested
    inner class `after rule is removed` {
        @Test
        fun `no dependencies`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(firstRule, secondRule)
            dependencyMap.removeRule(firstRule)

            assertFalse(dependencyMap.hasDirectDependencyOn(firstRule, secondRule))
            assertFalse(dependencyMap.hasIndirectDependencyOn(firstRule, secondRule))
            assertEmpty(dependencyMap.directDependenciesOf(firstRule))
            assertEmpty(dependencyMap.directDependentsOf(secondRule))
            assertEmpty(dependencyMap.involvedRules())
        }

        @Test
        fun `no dependents`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(firstRule, secondRule)
            dependencyMap.removeRule(secondRule)

            assertFalse(dependencyMap.hasDirectDependencyOn(firstRule, secondRule))
            assertFalse(dependencyMap.hasIndirectDependencyOn(firstRule, secondRule))
            assertEmpty(dependencyMap.directDependentsOf(firstRule))
            assertEmpty(dependencyMap.directDependentsOf(secondRule))
            assertEmpty(dependencyMap.involvedRules())
        }
    }

    @Nested
    inner class `with circular dependency` {
        @Test
        fun `rule has no indirect dependency on self`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(firstRule, secondRule)
            dependencyMap.addDependencyOn(secondRule, firstRule)

            assertFalse(dependencyMap.hasIndirectDependencyOn(firstRule, firstRule))
            assertFalse(dependencyMap.hasIndirectDependencyOn(secondRule, secondRule))
        }

        @Test
        fun `rule has circular dependency`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(firstRule, secondRule)
            dependencyMap.addDependencyOn(secondRule, firstRule)

            assertTrue(dependencyMap.hasCircularDependency(firstRule))
            assertTrue(dependencyMap.hasCircularDependency(secondRule))
        }
    }

    @Nested
    inner class `route tests` {
        @Test
        fun `route to self is singleton list`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val rule = nextDummyRuleNumber()

            assertEquals(dependencyMap.route(rule, rule), listOf(rule))
        }

        @Test
        fun `basic route`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()
            val thirdRule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(firstRule, secondRule)
            dependencyMap.addDependencyOn(secondRule, thirdRule)

            assertEquals(dependencyMap.route(firstRule, thirdRule), listOf(firstRule, secondRule, thirdRule))
        }

        @Test
        fun `no route test`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()
            val thirdRule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(firstRule, secondRule)
            dependencyMap.addDependencyOn(thirdRule, secondRule)

            assertNull(dependencyMap.route(firstRule, thirdRule))
        }

        @Test
        fun `circular dependency route works`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(firstRule, secondRule)
            dependencyMap.addDependencyOn(secondRule, firstRule)

            assertEquals(dependencyMap.circularDependencyRoute(firstRule), listOf(firstRule, secondRule, firstRule))
            assertEquals(dependencyMap.circularDependencyRoute(secondRule), listOf(secondRule, firstRule, secondRule))
        }

        @Test
        fun `circular dependency route return null when none exist`() {
            val dependencyMap = emptyMutableRuleDependencyMap()
            val firstRule = nextDummyRuleNumber()
            val secondRule = nextDummyRuleNumber()
            val thirdRule = nextDummyRuleNumber()

            dependencyMap.addDependencyOn(firstRule, secondRule)
            dependencyMap.addDependencyOn(thirdRule, secondRule)
            dependencyMap.addDependencyOn(secondRule, thirdRule)

            assertNull(dependencyMap.circularDependencyRoute(firstRule))
        }
    }
}