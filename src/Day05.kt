fun main() {
    fun part1(input: List<String>): Int {

        // Parse rules
        // Parse updates
        // Build map of rules (before and after): a page to list of pages after it
        // For each page in the update:
            // None of the prev pages are in the after rules
            // None of the after pages are in the before rules

        val rules = getRulesFrom(input)
        val updates = getUpdatesFrom(input)

        val rulesAfter = rules.rulesAfter()
        val rulesBefore = rules.rulesBefore()

        return updates.filter { update ->
            update.all { page ->
                val pagesBefore = update.pagesBefore(page)
                val pagesAfter = update.pagesAfter(page)

                pagesBefore.follow(rulesAfter[page]) &&
                        pagesAfter.follow(rulesBefore[page])
            }
        }.sumOf { update ->
            update.middlePage()
        }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 143)

    val input = readInput("Day05")
    check(part1(input) == 3608)
//    part2(input).println()
}

private fun List<String>.middlePage(): Int = this[lastIndex / 2].toInt()

private fun List<String>.pagesBefore(page: String) = subList(0, indexOf(page))

private fun List<String>.pagesAfter(page: String): List<String> = subList(indexOf(page), lastIndex + 1)

private fun List<String>.follow(rulesAfterForPage: List<String>?) =
    all { rulesAfterForPage?.contains(it) != true }

private fun List<Pair<String, String>>.rulesBefore() =
    groupBy(keySelector = { it.second }, valueTransform = { it.first })

private fun List<Pair<String, String>>.rulesAfter() =
    groupBy(keySelector = { it.first }, valueTransform = { it.second })

private fun getUpdatesFrom(input: List<String>) = input.subList(input.indexOf("") + 1, input.lastIndex + 1)
    .map { it.split(',') }

private fun getRulesFrom(input: List<String>) = input.subList(0, input.indexOf(""))
    .map { it.substringBefore('|') to it.substringAfter('|') }
