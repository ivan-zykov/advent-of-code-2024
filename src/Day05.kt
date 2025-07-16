import kotlin.collections.forEach

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

        /*
        Get rules and updates: reuse part1
        Get incorrect updates: reuse part1
        Sort updates with a var of bubble sort:
            Page iterator
            For each page, check after rules
                If rules not satisfied, swap with next
         */

        val rules = getRulesFrom(input)
        val updates = getUpdatesFrom(input)
        val rulesGrouped = rules.rulesAfter()

        val wrongUpdates = updates.filterNot { update ->
            update.all { page ->
                val pagesBefore = update.pagesBefore(page)
                pagesBefore.follow(rulesGrouped[page])
            }
        }

        val fixedUpdates: List<List<String>> = buildList {
            wrongUpdates.forEach { update ->
                val temp = update.toMutableList()

                for (i in temp.indices) {
                    var wereSwapped = false
                    for (j in 0..temp.lastIndex - i - 1) {
                        val pagesAfter = temp.pagesAfter(j)
                        val pagesAfterAllNextPages: Set<String> = buildSet {
                            pagesAfter.forEach { pageAfter ->
                                rulesGrouped[pageAfter]?.let { addAll(it) }
                            }
                        }
                        if (temp[j] in pagesAfterAllNextPages) {
                            temp.swapWithNextAt(j)
                            wereSwapped = true
                        }
                    }
                    if (!wereSwapped) {
                        break
                    }
                }

                add(temp.toList())
            }
        }

        return fixedUpdates.sumOf {
            it.middlePage()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 143)
    check(part2(testInput) == 123)

    val input = readInput("Day05")
    check(part1(input) == 3608)
    part2(input).println() // 5570 and 5055 both too high
}

private fun List<String>.middlePage(): Int = this[lastIndex / 2].toInt()

private fun List<String>.pagesBefore(page: String) = subList(0, indexOf(page))

private fun List<String>.pagesAfter(idx: Int): List<String> = subList(idx + 1, lastIndex + 1)

private fun List<String>.follow(rules: List<String>?) =
    all { rules?.contains(it) != true }

private fun List<Pair<String, String>>.rulesBefore() =
    groupBy(keySelector = { it.second }, valueTransform = { it.first })

private fun List<Pair<String, String>>.rulesAfter() =
    groupBy(keySelector = { it.first }, valueTransform = { it.second })

private fun getUpdatesFrom(input: List<String>) = input.subList(input.indexOf("") + 1, input.lastIndex + 1)
    .map { it.split(',') }

private fun getRulesFrom(input: List<String>) = input.subList(0, input.indexOf(""))
    .map { it.substringBefore('|') to it.substringAfter('|') }

private fun MutableList<String>.swapWithPrev(idx: Int) {
    val temp = this[idx - 1]
    this[idx - 1] = this[idx]
    this[idx] = temp
}

private fun MutableList<String>.swapWithNextAt(idx: Int) {
    val temp = this[idx + 1]
    this[idx + 1] = this[idx]
    this[idx] = temp
}
