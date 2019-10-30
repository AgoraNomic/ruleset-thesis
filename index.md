# Rulset Thesis

## 0. Introduction
At the time of writing, Agora has a large number of rules - 122 rules to be exact. Many of these rules build off of other rules. Eventually, the concepts in the rules are built up enough to allow actual gameplay. Thoughout the ruleset, the Rules build up a complex dependency network.

This analysis is based off of the ruleset that is dated 18 Sep 2019.


## 1. Implementation

### 1.1 Failed attempt: manual reading

In order to analyze this network, we must traverse the entirety of the ruleset. I originally began by doing this manually - reading through the text of rules and looking at what other rules they referenced, eventually putting that into a text file, that I would then machine parse. It quickly became clear that this approach was unsustainable; there was simply too much text to read.

In addition to being slow, this approach was error prone - even reviewing my work after ~10 rules, I had already seen rules that I had missed. Clearly, I needed to find another solution.

### 1.2 Machine-Parsing the ruleset

The clear solution to these problems was to machine-parse the ruleset. There are two obvious ways to parse the ruleset - clone the ruleset repository and parse the individual files, and parse the single-file SLR. I went with parsing the SLR. I'd love to be able to say that this was because of some heavily thought-out technical consideration, but I just didn't even think about parsing the individual files until writing this up, and parsing the SLR was pretty easy, since it follows a pretty simple format. However, for any future endeavors, parsing the individual rule files is likely to be a much better idea, especially if annotations or history must be handled.

However, simply having the ruleset in memory is not enough - the dependencies of each rule must still be extracted. This was done by creating a map of certain phrases to the rule that defines them. For example, the phrase "in a timely fashion" maps to Rule 1023. This map was created by manually reading through the entire ruleset and writing down what phrases each rule defines. I note that this was significantly easier than trying to manually determine the dependencies of each rule - definitions within each rule are pretty easy to spot, but the individual words that created dependencies were not easy to spot. Having created this map, it is easy enough to search through the text of all rules to find where the phrases are used and then create a list of the dependencies of each rule. In reality this is implemented with two maps - one that has case-insensitive phrases and one that has case-sensitive phrases. This was implemented to support things like "Mother, May I?"'s all-capital words, and found several uses.

One failed attempt in creating this was assigning specific words to rules, instead of phrases. This completely failed. Take for instance, the word "notice" - this could just mean "notice" by itself, but it could be part of the phrase "notice of honour". It was clear that searching for complete phrases was necessary.

#### 1.2.1 Strong dependencies

I implemented what I called "Strong Dependencies" in order to solve the issue of terms being overloaded. A Strong Dependency from RX to RY means that a rule does not depend on RX unless it also (possibly indirectly) depends on RY.

For example, Rule 2577 ("Asset Actions") has a strong dependency on Rule 2166 ("Assets"). This prevents Rule 2438 ("Ribbons"), which uses the term "earns" from depending on Rule 2577, which defines the term "earns" for assets, because Rule 2438 does not separately depend on Rule 2577.

#### 1.2.2 Problems

Despite my (maybe not quite) best efforts, there are some quirks with this machine-parsing.

One issue was that, in an earlier version of the program, Rule 2582 was found to depend on Rule 2581, because Rule 2581 defined the Patent Title "Hard Labor", while Rule 2582 happened to use the term "hard labor" - this was fixed by making these patent titles require case-sensitive usages, rather than permitting case-insensitive usages.

Another issue is that it is impossible to mark the word "own" as being defined by Rule 2576 ("Ownership"). "Own" simply has too many meanings besides asset ownership, even in rules that depend on assets. I'm not quite sure how to solve this problem, short of manually looking for all instances of it (which I'm not going to do).

Something I found while writing this thesis was that in Rule 2581 "win-win" was being counted as if it was to "win" the game - this was fixed, but shows just how subtle these errors are to find. This could have gone completely unnoticed, but I just so happened to find it.

Finally, I can't be absolutely sure that the dependency graphs are actually correct. They look reasonable to me, and I've examined some things that I've found surprising, and found the program to be correct, but I still can't be certain of the analysis in general.

### 1.3 Manual overrides

Some manual adjusting to the analysis is required to correctly model the rules. Some joke, or otherwise meaningless, rules are completely ignored (and thus not included in any further analysis) - Rule 2029 ("Town Fountain"), Rule 1727 ("Happy Birthday"), and Rule 2486 ("The Royal Parade").

## 2. Analysis

### 2.1 Rules not in the hierarchy

There is a single rule that has neither dependencies nor dependents - Rule 2429 ("Bleach"). Clearly nothing directly depends on this rule, and it happens to not use terms defined by any other rule, so it gets the special distinction of not being in the hierarchy.

### 2.2 Dependents

In general, rules with more dependents can be said to have more important definitions in them, and are generally of higher power (there is only 1 power-2 rule in the rules with the top 10 most dependents; the rest are power-3 or power-4).

The rule with the most dependents is Rule 2152 ("Mother, May I?"), with 87 direct dependents, or ~71% of all rules. I find this to be unsurprising - the terms defined there are used in a lot of places.

Next up is Rule 869 ("How to Join and Leave Agora"), with 81 direct dependents. Again, I find this unsurprising - this is the rule that defines both "player" and "person", which show up a lot in the Rules.

Third place is Rule 2141 ("Role and Attributes of Rules"), with 72 direct dependents. I found this surprising at first, and I believe this can be partially attributed to the common usage of the phrase "rules to the contrary notwithstanding", which I have counted as being defined by Rule 2141.

Finally, there are 55 leaves (rules with no dependents). These could be rules that don't define any terms (i.e. Rule 1030, which defines no terms, but is still implicitly referenced by other rules), or rules that only implement actual game mechanics.

### 2.3 Dependencies

Another way to sort the rules is by how many dependencies they have. Rules with more dependencies pull together lots of different aspects of the game, (hopefully) providing something useful. 

There is a 3-way tie for having the most direct dependencies: Rule 2532 ("Zombies"), Rule 2438 ("Ribbons") and Rule 2581 ("Official Patent Titles"), each with 19 dependencies. Rule 2438 is a great example of pulling together rules from many different areas of the game into something unified - it takes a bunch of things that one can do from all over the rules, and creates a single unified win condition. Rule 2581 is a less good example - it gets a lot of its dependencies from rules that define offices, and it doesn't do anything particularly unifying. Finally, Rule 2532 is an even worse example - while it does create something novel (zombies), it gets its dependencies mostly from /prohibiting/ people from causing their zombies to do certain things; it certainly does not bring in a bunch of different aspects to create something unified.

There is a single rule with zero dependencies: Rule 2429, as discussed above.

Other than that, rules with fewer dependencies are generally either very short (i.e. Rule 2137 ("The Assessor")), or somewhat niche (i.e. Rule 2240 ("No Cretans Need Apply")).

### 2.4 Circular dependencies

In most area, circular dependencies are not idea. I know this to be a fact in software - where circular dependencies can cause all kinds of issues, particularly when not all of the components of the cycle are not controlled by the same organization. However, this is not the case with the Agoran ruleset - the Players can completely control the Ruleset and, as far as we know, nothing else depends on it. Circular dependencies in the ruleset also don't cause any problems simply by existing, even if they might in some other areas, like software.

In the ruleset, a whopping 55 of the 122 rules in the ruleset are involved in a dependency cycle. To be fair - some of the cycles are ridiculous; three of the cycles are of length 11.

One of the length 11 cycles is as follows:
- Rule 2545 ("Auctions") depends on Rule 2578 ("Currencies")
- R2578 depends on Rule 2166 ("Assets")
- R2166 depends on Rule 2141 ("Role and Attributes of the Rules") [text: "granted Mint Authority by the Rules"]
- R2141 depends on Rule 1051 ("The Rulekeepor")
- R1051 depends on Rule 1006 ("Offices")
- R1006 depends on Rule 2154 ("Election Procedure")
- R2154 depends on Rule 2472 ("Office Incompatibilities")
- R2472 depends on Rule 991 ("Calls for Judgement") [because it mentions the Arbitor]
- R991 depends on Rule 2532 ("Zombies") [text: "all active players"]
- R2532 depends on Rule 2550 ("Bidding")
- R2550 depends on Rule 2545 ("Auctions"), completing the cycle.

I hope nobody feels bad about that being a cycle - it's a bit ridiculous.

There are certain dependency chains that appear in a large number of cycles; R2141 -> R1051 is one, and R2141 -> R1688 -> R101 is another. This perhaps points out that these Rules are attempting to do to much, or that they could be split into multiple rules to remove the cycles (if the Agoran people wished to).

In addition, there are some cycles that are short and easy to remove. One example is R2139 <-> R1789; all that would need to be done is to remove Rule 2139's (basically useless) reptition of the Registrar's responsibility to publish Cantus Cygnei.

### 2.5 Power

Under Rule 217, rules with lower power cannot necessarily define terms that affect rules with higher power. So, ideally, Rules should only depend on other Rules of equal or higher power, in order to ensure that the definitions they use are given definite legal force. However, it is clear from my analysis that this is not what actually happens in the ruleset.

By my analysis, 43 rules depend on lower-powered rules. 3 of these rules depend on six lower-power rules (the maximum):
- R1698 ("Agora Is A Nomic"), depending on R2152, R869, R105, R2350, R1023, and R2141
- R2574 ("Zombie Life Cycle"), depending on R2532, R1023, R2139, R1885, R2545, and R2143
- R2438 ("Ribbons"), depending on R1006, R2143, R1023, R991, and R649.

However, these are outliers - most of the rules that have any underpowered dependencies at all only have one or two.

Looking at this from the opposite direction, there are also certain rules that are _used_ as underpowered dependencies quite often.

There are 31 rules used as underpowered dependencies. The rule with the most overpowered dependents is Rule 1023 ("Agoran Time"), with 17. Second place goes to Rule 2143 ("Official Reports and Duties"), with 12. Perhaps the fact that these rules are needed so often by higher power rules indicates that they should be made of a higher power.

### 2.6 Specific rules

There are a few specific rules that I think would be interesting to analyze in further detail. I will only go into this briefly, although each of these subjects could probably be a thesis unto itself.

#### 2.6.1 Rule 101

Rule 101 ("The Game of Agora") is a special rule. Rule 101 attempts to define what "the game of Agora" is:
{
      Agora is a game of Nomic, wherein Persons, acting in accordance
      with the Rules, communicate their game Actions and/or results of
      these actions via Fora in order to play the game.
}

Surprisingly, this Rule has a large number of direct dependents. This is because it defines the term "Agora", which is mentioned a large number of times in the ruleset [note: this excludes "Agoran" in "Agoran consent", which is defined by Rule 1728]. I also counted this rule as defining the word "action(s)", since there is no rule with an explicit definition of the term, and Rule 101 comes the closest to doing so. With these terms, Rule 101 gathers a large number of dependents - 48.

In a previous version of this thesis, I used a manual override to mark Rules 2449, 869, 2141, and 468 as depending on Rule 101 and to mark Rule 101 as not depending on those Rules. I did this because I thought that Rule 101 was really setting up what Rules, Fora, and Players were, and that the other rules were simply giving precise definitions to the terms. During peer review, I received a comment from the H. G. that this was not the function of Rule 101, that it was more like an "object of the game". This interpretation makes sense to me, so there is no such override in this version of the analysis. However, neither choice is necessarily correct, and this presents an interesting question - what exactly is the role of Rule 101 in the rulset (regardless of what the intent was), and to what degree should it affect how other rules are interepreted?

#### 2.6.2 Rule 2152

Rule 2152 ("Mother, May I?") is the rule that defines that pertain to entities attempting to do things, and (generally people) being punished for or required to do something. Unsurprisingly, these concepts are used widely throughout the ruleset - games generally have things that people can do and obligations that people must fulfill.

In fact, as mentioned above, Rule 2152 has the most direct dependents of any rule: 87. Even a power-4 rule depends on it: Rule 1698.

#### 2.6.3 Players

Rule 869 ("How to Join and Leave Agora") is the rule that actually defines what a "player" and a "person' are. Thus, rules that depend on this rule recognize that people exist, as opposed to just dealing with the more vague term of "entity" or just stating that things happen.

Unsurprisingly, this rule has many dependents - 81. This means that almost 2/3 of the rules deal with people and players.

#### 2.6.4 Time

Rule 1023 ("Agoran Time") fixes the timescale of Agora to UTC, but it also provides a central dependency of all of the rules that depend on time.

It should be noted that text-based analysis cannot be perfect here - Rule 1023 says that its definitions only apply when referring to absolute time points, and not relative time frames, but there's no easy way to determine what is meant by simple phrase-based searching. So, Rule 1023 simply collects every rule that contains a mention of time as a dependent.

Rule 1023 has 48 dependents, which comprise about 40% of the ruleset.

#### 2.6.5 Fora

Rule 478 defines both what a Forum is and many terms related to it - including the ever popular phrase "by announcement". I thus expected Rule 478 to have many, many dependents, which it does. Rules that depend on Rule 478 deal with the reality that the players generally do things by sending public messages, so rules that don't depend on Rule 478 likely operate more in the abstract - something simply happens, rather than a player causing it to happen.

Rule 478 has 66 dependents, which is a little more than half of the ruleset.


## 3. Final thoughts

This was a fun project. It only took me a few days to write the code and a few days to write and revise this thesis. Hopefully this research, or the code that I've provided, can be useful to someone else someday.

### 3.1 Further work

As I've mentioned before, there is certainly more analysis that could be done on the structure of the ruleset and how individual rules can have such great impacts on the ruleset; this is left as an exercise to future Agorans.

The machine-parsing system could certainly be improved; possibly by allowing more context to be used to determine the meaning of phrases. However, this would likely significantly complicate the code that is used to analyze phrases, and is getting closer to full-blown language processing.


## A1. Appendex 1: Graphs

I've attached some graphs that may be of interest to the community. This thesis does not strictly depend on these graphs, but they may be useful to visualise the dependency network.

### A1.1 Graph Generation

All graphs were generated with graphviz, available at https://graphviz.org/ . This was done by having my program iterate through the dependency map and emit the source for graphviz.

The following command was used to generate all graphs: [dot -Tsvg -start=1000 "$graphFile" > "$outFile"], where "$graphFile" and "$outFile" are replaced by the graphviz source file and the output file, respectively. The start parameter is necessary because, if it is not provided, graphviz uses the time to seed a random number generator, while if it is provided, the provided number is used as the seed, thus giving deterministic graphs.

### A1.2 Non-graphed rules

Unless otherwise noted, the following rules are excluded from all graphs:
- Rule 2152
- Rule 869
- Rule 2141
- Rule 478
- Rule 101
- Rule 1023

These are the rules that have the most dependents, and including them in most graphs makes the graphs (even more) unreadable and take a good bit longer to render, so they are not included.

### A1.3 Attached graphs

The following graphs are attached, all as svg files:

\[total\]: includes ALL rules that participate in the hierarchy, even those left out of other graphs.

\[simplified\]: includes the rules that are not excluded.

\[cycles\]: rules that participate in a dependency cycle are colored red, rules that do not are colored green.

\[leaves\]: rules that have no dependencies (leaves) are colored.

\[without_circular\]: ALL rules, except that any rule in a dependency cycle is simply removed; I find this interesting because it contains actual layers, rather than the insanity that there is right now.


## A2. Appendix: Other attachments

I've also attached some plain text files that provide more information on the topics that I've gone into above.

The following non-graph files are attached:
\[dependent_counts\]: contains a list of rules and the number of dependents that each has.

\[dependency_counts\]: contains a list of rules and the number of dependencies that each has.

\[circular_dependencies\]: contains a list of dependency cycles in the rules; each line is a separate cycle, and each rule depends on the one that follows it, with the final rule depending on the first rule.

\[underpowered_dependencies\]: contains a list of the underpowered dependencies of each rule.

\[dependents_(2152|869|1023|478)\]: contains a list of the direct dependents of Rule N, with one on each line.


## A3. Appendix: Code

All code used is attached. In addition, it is published at https://github.com/agoraNomic/ruleset-thesis. Feel free to look at it and play with it.

