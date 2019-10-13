package org.agoranomic.ruleset_thesis.model

import java.math.BigDecimal

typealias RuleNumber = Int
typealias RulePower = BigDecimal
typealias RuleTitle = String
typealias RuleText = String

data class Rule(val number: RuleNumber, val power: RulePower, val title: RuleTitle, val text: RuleText)