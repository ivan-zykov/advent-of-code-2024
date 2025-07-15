fun main() {
    fun part1(input: List<String>): Int {

        // Parse rules
        // Parse updates
        // Build map of rules (before and after): a page to list of pages after it
        // For each page in the update:
            // None of the prev pages are in the after rules
            // None of the after pages are in the before rules

        val rules = input.subList(0, input.indexOf(""))
            .map { it.substringBefore('|') to it.substringAfter('|') }
        val updates = input.subList(input.indexOf("") + 1, input.lastIndex + 1)
            .map { it.split(',') }

        val rulesAfter = rules.groupBy(keySelector = { it.first }, valueTransform = { it.second })
        val rulesBefore = rules.groupBy(keySelector = { it.second }, valueTransform = { it.first })

        return updates.filter { update ->
            update.all { page ->
                val pagesBefore = update.subList(0, update.indexOf(page))
                val pagesAfter = update.subList(update.indexOf(page), update.lastIndex + 1)

                pagesBefore.all { rulesAfter[page]?.contains(it) != true } &&
                        pagesAfter.all { rulesBefore[page]?.contains(it) != true }
            }
        }.sumOf { update ->
            update[update.lastIndex / 2].toInt()
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
