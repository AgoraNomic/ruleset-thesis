package org.agoranomic.ruleset_thesis.dependency_parsing

import org.agoranomic.ruleset_thesis.dependency_map.RuleDependencyMap
import org.agoranomic.ruleset_thesis.dependency_map.emptyMutableRuleDependencyMap
import org.agoranomic.ruleset_thesis.dependency_map.mutableCopy
import org.agoranomic.ruleset_thesis.model.Rule
import org.agoranomic.ruleset_thesis.model.RuleNumber
import org.agoranomic.ruleset_thesis.util.getOrFail

private val punctuators = charArrayOf('(', ')', ' ', ',', '.', ':', '-', '*', '\n', '/', '"', ';', '<', '>', '\'', '+', '=')

private fun flattenWordListMap(wordListMap: Map<List<String>, RuleNumber?>, transformWord: (String) -> String): Map<Int, Map<List<String>, RuleNumber?>> {
    val wordMap = mutableMapOf<Int, MutableMap<List<String>, RuleNumber?>>()

    for ((wordList, rule) in wordListMap) {
        for (word in wordList) {
            val parts = word.split(*punctuators)

            wordMap.computeIfAbsent(parts.size) { mutableMapOf() }.put(parts.map(transformWord), rule)
        }
    }

    return wordMap
}

private val caseInsensitiveWordListMap = mutableMapOf<List<String>, RuleNumber?>(
    listOf("a", "an", "the") to null,
    listOf("s") to null, // Generated from some contractions,
    listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z") to null, // Single letters, probably in lists
    listOf("i", "ii", "iii", "iv", "v", "vi", "vii") to null, // Roman numerals, probably in lists
    listOf("M", "N", "X", "Y", "Z", "T") to null, // Used as placeholders
    listOf("i", "e") to null, // in "i.e."
    listOf("is", "be", "has", "have", "would", "are", "do", "does", "doing", "did", "was", "were") to null,
    listOf("e", "em", "emself", "eir", "they", "their", "them", "it", "its", "I", "me", "my", "his", "her", "him", "hers", "any", "other") to null,
    listOf("and", "but", "or", "via", "hereby", "wherein", "in", "where", "in order", "of", "outside", "met", "some", "thus", "what", "such", "ago", "yet", "each", "who", "before", "after", "at", "due", "containing", "also", "some", "than", "thereby", "till", "begin", "begins", "end", "ends", "least", "when", "over", "next", "to", "if", "in", "at", "so", "with", "without", "for", "from", "unless", "by", "within", "on", "how", "let", "as", "instead", "all", "while", "above", "under", "using") to null,
    listOf("allow", "allows", "allowing", "enable", "enables", "enabling", "permit", "permits", "permitting", "proscrbe", "proscribes", "proscribing", "succeed", "succee   ds", "succeeding", "fail", "fails", "failing", "modify", "modifies", "modifying", "apply", "applies", "applying", "purport", "purports", "purporting", "authorize", "authorizes", "authorizing", "prohibit", "prohibits", "prohibiting") to null,
    listOf("true", "false") to null,
    listOf("this", "these", "that", "those", "there") to null,
    listOf("game", "nomic") to null,
    listOf("may", "can", "cannot", "shall", "must", "forbidden", "should", "impossible") to null, // NOT R2152 words
    listOf("notwithstanding") to null,
    listOf("please", "right good") to null,
    listOf("cease") to null,
    listOf("no", "not", "non", "nor", "none", "a lot") to null,
    ((0..1000).toList().map { it.toString() }) to null,
    listOf("zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine") to null,
    listOf("zeroth", "first", "second", "third", "fourth", "fifth", "sixth", "seventh", "eighth", "nineth") to null,
    listOf("win-win") to null, // Conflict with "win" the game
    listOf("action", "actions") to 101,
    listOf("agora") to 101,
    listOf("ossified") to 1698,
    listOf("person", "persons", "people") to 869,
    listOf("player", "players") to 869,
    listOf("Citizenship", "Registered", "Unregistered", "register", "registers", "registering", "registered", "registration", "deregister", "deregisters", "deregistered", "deregistering", "deregistration") to 869,
    listOf("by announcement") to 478,
    listOf("forum", "fora", "Publicity", "message", "public message", "publicly", "Discussion", "Foreign") to 478,
    listOf("publish", "publishes", "published", "publishing", "announce", "announces", "announcing", "send", "sends", "sent", "sending") to 478,
    listOf("Registrar", "untracked") to 2139,
    listOf("Cantus Cygneus", "Cantus Cygnei", "Writ of Fugiendae Agorae Grandissima Exprobratione", "Writ of FAGE", "Writs of FAGE") to 1789,
    listOf("regulated", "unregulated") to 2125,
    listOf("in a timely fashion", "within a timely fashion") to 1023,
    listOf("day", "days", "Agoran day", "Agoran days", "week", "weeks", "Agoran week", "Agoran weeks", "month", "months", "Agoran week", "Agoran weeks", "quarter", "quarters", "Agoran quarter", "Agoran quarters", "year", "years", "Agoran year", "Agoran years") to 1023,
    listOf("object", "objects", "objection", "objections", "support", "supports", "Agoran consent", "notice") to 1728,
    listOf("supporter", "objector", "satisfied") to 2124,
    listOf("determinate", "indeterminate") to 2518,
    listOf("random") to 2505,
    listOf("switch", "switches", "flip", "flips", "flipping", "singleton", "boolean") to 2162,
    listOf("power", "instrument", "secured") to 1688,
    listOf("proposal", "proposals") to 2350,
    listOf("promotor", "distribute", "distributing", "proposal pool") to 1607,
    listOf("assessor") to 2137,
    listOf("comptrollor", "notice of veto") to 2597,
    listOf("rule", "rules") to 2141,
    listOf("rules to the contrary notwithstanding") to 2141,
    listOf("rule change", "rule changes", "enact", "repeal", "reenact", "re-enact", "amend", "retitle") to 105,
    listOf("regulation", "regulations") to 2493,
    listOf("rulekeepor") to 1051,
    listOf("logical ruleset", "logical rulesets", "full logical ruleset", "short logical ruleset") to 1681,
    listOf("editor", "editorial guideline", "editorial guidelines") to 2599,
    listOf("Agoran decision", "decision") to 693,
    listOf("AI-majority", "instant runoff"," first-past-the-post") to 2528,
    listOf("voting period", "valid option", "valid options") to 2528,
    listOf("vote collector") to 208,
    listOf("voting method", "voting methods") to 955,
    listOf("quorum") to 879,
    listOf("voting strength") to 2422,
    listOf("office", "offices", "vacant", "officer", "officers", "imposed office", "elected", "holder", "interim", "officeholder", "officeholders") to 1006,
    listOf("election", "elections", "contested", "uncontested", "nomination") to 2154,
    listOf("impeach") to 2573,
    listOf("deputise", "deputy", "deputies", "deputisation", "temporary deputisation") to 2160,
    listOf("associate director of personnel", "ADOP") to 2138,
    listOf("overpowered") to 2472,
    listOf("weekly duty", "weekly duties", "monthly duty", "monthly duties", "report", "weekly report", "monthly report") to 2143,
    listOf("convergence") to 2143,
    listOf("ratification", "ratify", "ratifies", "ratified", "public document") to 1551,
    listOf("ratification without objection") to 2202,
    listOf("self-ratification", "self-ratifying", "claim of error") to 2201,
    listOf("exile", "outlaw", "outlaws") to 2556,
    listOf("blot", "blots", "Impure", "Pure", "Fugitive", "levy a fine of", "levying fines", "expunge", "expunging") to 2555,
    listOf("Referee") to 2555,
    listOf("finger", "fingers", "pointed finger", "finger pointing", "point", "pointed", "investigator") to 2478,
    listOf("summary judgment", "summary judgement") to 2479,
    listOf("cold hand of justice", "crime", "crimes", "forgivable") to 2557,
    listOf("impartial arbitration restoration") to 2531,
    listOf("call for judgement", "call for judgment", "CFJ", "CFJs", "judicial case", "inquiry case", "judge", "judgment") to 991,
    listOf("Arbitor") to 991,
    listOf("moot", "motion to reconsider") to 911,
    listOf("excess case") to 2175,
    listOf("recuse") to 2492,
    listOf("lie", "lies", "lied") to 2471,
    listOf("pledge", "pledges", "pledging", "pledged", "oath", "oaths") to 2450,
    listOf("act on behalf", "acting on behalf", "principal") to 2466,
    listOf("consent", "consents", "consented", "consenting") to 2519,
    listOf("contract", "contracts") to 1742,
    listOf("asset", "assets", "mint authority", "backing document", "public classes of assets") to 2166,
    listOf("owner", "owns", "lost and found department") to 2576,
    listOf("own") to null, // cannot count, since "own" is too common a word, even in rules that depend on assets
    listOf("destroy", "destroyed", "destructible", "indestructible", "earn", "earned", "earns", "earning", "grant", "grants", "granted", "granting", "lose", "loses", "losing", "revoke", "revokes", "revoking", "revoked", "transfer", "transfers", "transferring", "transferred", "fixed", "liquid") to 2577,
    listOf("currency", "currencies") to 2578,
    listOf("fee", "fee-based", "fee-based action", "fee-based actions") to 2579,
    listOf("treasuror") to 2456,
    listOf("coin", "coins") to 2483,
    listOf("payday", "paydays") to 2559,
    listOf("welcome package") to 2499,
    listOf("agoran birthday") to 2585,
    listOf("auction", "auctions", "auctioneer", "auctioneers", "announcer", "announcers") to 2545,
    listOf("bid", "bids", "bidding", "bidder", "bidders") to 2550,
    listOf("Master", "zombie", "active", "inactive", "masterminding") to 2532,
    listOf("resale value") to 2574,
    listOf("zombie auction") to 1885,
    listOf("win", "won", "Champion") to 2449,
    listOf("Apathy") to 2449,
    listOf("Karma", "Notice of Honour", "notices of honour", "Shogun", "Honourless Worm") to 2510,
    listOf("Tailor", "Ribbon Ownership", "ribbon", "ribbons", "qualifies", "qualify", "Banner", "Raise a Banner") to 2438,
    listOf("Festivity", "festival", "Festive") to 2480,
    listOf("Patent Title", "patent titles", "Herald") to 649,
    listOf("Badge", "badges") to 2415,
    listOf("degrees") to 1367,
    listOf("Heroic titles") to 2231,
    listOf("Speaker", "Laureled") to 103,
    listOf("Prime Minister") to 2423,
    listOf("Motion of No Confidence") to 2463,
    listOf("Cabinet Order", "Certiorari", "Dive", "Manifesto") to 2451,
    listOf("Distributor") to 2575,
    listOf("Read the Ruleset Week") to 2327,
    listOf("Tournament", "tournaments", "gamemaster") to 2464,
    listOf("Free Tournament") to 2566,
    listOf("Birthday Tournament") to 2495,
    listOf("Holiday", "holidays") to 1769
)

private val caseInsensitiveWordMap = flattenWordListMap(caseInsensitiveWordListMap) { it.toLowerCase() }

private val caseSensitiveWordListMap = mapOf<List<String>, RuleNumber?>(
    // Terms that need all capitals
    listOf("CANNOT", "IMPOSSIBLE", "INEFFECTIVE", "INVALID", "MUST NOT", "MAY NOT", "SHALL NOT", "ILLEGAL", "PROHIBITED", "NEED NOT", "OPTIONAL", "SHOULD NOT", "DISCOURAGED", "DEPRECATED", "CAN", "MAY", "MUST", "SHALL", "REQUIRED", "MANDATORY", "SHOULD", "ENCOURAGED", "RECOMMENDED") to 2152,
    listOf("ADOPTED", "REJECTED", "FAILED QUORUM") to 955,
    listOf("FOR", "AGAINST", "PRESENT") to 2528,
    listOf("FALSE", "TRUE", "IRRELEVANT", "INSUFFICIENT", "DISMISS", "PARADOXICAL") to 591,
    listOf("AFFIRM", "REMAND", "REMIT") to 911,

    // Specific Patent Titles, also prevents conflict
    listOf("Grand Hero of Agora Nomic", "GHAN", "Hero of Agora Nomic", "HAN") to 2231,
    listOf("Tapecutter", "Scamster", "Hard Labor", "McGyver", "Bard", "Necromancer", "Tycoon", "Helping Hand", "Tiger Team") to 2581,
    listOf("Awards Month", "Silver Quill", "Wooden Gavel", "Golden Glove", "Employee of the Year") to 2582
)

private val caseSensitiveWordMap = flattenWordListMap(caseSensitiveWordListMap) { it }

private fun findFirstPhrase(allWords: List<String>, phraseMap: Map<Int, Map<List<String>, RuleNumber?>>, transformWords: (List<String>) -> List<String>): Pair<Int, RuleNumber?>? {
    val wordCount = allWords.size

    for (phraseLength in phraseMap.keys.sortedDescending()) {
        if (phraseLength > wordCount) continue

        val currentWords = transformWords(allWords.subList(0, phraseLength))
        val lengthPhraseMap = phraseMap.getOrFail(phraseLength)

        if (lengthPhraseMap.containsKey(currentWords)) {
            val result = lengthPhraseMap[currentWords]
            return phraseLength to result
        }
    }

    return null
}

fun parseDependencies(rules: List<Rule>): RuleDependencyMap {
    val dependencyMap = emptyMutableRuleDependencyMap()
    val unknownWords = mutableSetOf<String>()

    for (rule in rules) {
        val words = rule.text.split(*punctuators).filter { it.isNotBlank() && it.toIntOrNull() == null }
        val wordCount = words.size

        var currentIndex = 0

        while (currentIndex < wordCount) {
            val remainingWords = words.subList(currentIndex, wordCount)

            val lookups = listOf(
                findFirstPhrase(
                    remainingWords,
                    caseSensitiveWordMap
                ) { it },
                findFirstPhrase(
                    remainingWords,
                    caseInsensitiveWordMap
                ) { it.map { str -> str.toLowerCase() } })

            val phraseResult = lookups.firstOrNull { it != null }
            if (phraseResult != null) {
                val dependency = phraseResult.second
                if (dependency != null) dependencyMap.addDependencyOn(rule.number, dependency)
                currentIndex += phraseResult.first
            } else {
                unknownWords.add(words[currentIndex])
                ++currentIndex
            }
        }
    }

    System.err.println("Unknown words (${unknownWords.size}): $unknownWords")
    return dependencyMap
}
