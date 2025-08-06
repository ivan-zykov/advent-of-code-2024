import kotlin.collections.forEach

fun main() {
    fun part1(input: List<String>): Int {
        val rules = getRulesFrom(input)
        val updates = getUpdatesFrom(input)
        val rulesGrouped = rules.rulesAfter()

        return updates.filter { update ->
            update.all { page ->
                val pagesAfter = update.pagesAfter(page)
                pagesAfter.follow(rulesGrouped[page])
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
            For each page, check rules
                If rules not satisfied, swap with next
         */

        val rules = getRulesFrom(input)
        val updates = getUpdatesFrom(input)
        val rulesGrouped = rules.rulesAfter().toSortedMap()

        val wrongUpdates = updates.filterNot { update ->
            update.all { page ->
                val pagesAfter = update.pagesAfter(page)
                pagesAfter.follow(rulesGrouped[page])
            }
        }

        val fixedUpdates: List<List<String>> = buildList {
            wrongUpdates.forEach { update ->
                val temp = update.toMutableList()

                for (i in temp.indices) {
                    var wereSwapped = false
                    for (j in 0..temp.lastIndex - i - 1) {
                        val pagesAfter = temp.pagesAfter(temp[j])
                        if (!pagesAfter.follow(rulesGrouped[temp[j]])) {
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

        val wrongUpdatesPost = fixedUpdates.filterNot { update ->
            update.all { page ->
                val pagesAfter = update.pagesAfter(page)
                pagesAfter.follow(rulesGrouped[page])
            }
        }

        return fixedUpdates.sumOf {
            it.middlePage()
        }
    }

    fun part2Alt(input: List<String>): Int {

        /*
        Hacky solution just taking the page with average number of other pages after it
         */

        val rules = getRulesFrom(input)
        val updates = getUpdatesFrom(input)
        val rulesGrouped = rules.rulesAfter().toSortedMap()

        val wrongUpdates = updates.filterNot { update ->
            update.all { page ->
                val pagesAfter = update.pagesAfter(page)
                pagesAfter.follow(rulesGrouped[page])
            }
        }

        return wrongUpdates.sumOf { update ->
            val rulesReducedForUpdate = rulesGrouped.filterKeys { key -> key in update }
                .mapValues { (_, value) ->
                    value.filter { element -> element in update }
                }
            val midSize = rulesReducedForUpdate.maxBy { it.value.size }.value.size / 2
            rulesReducedForUpdate.filter { it.value.size == midSize }
                .keys.first().toInt()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 143)
    check(part2(testInput) == 123)

    val input = readInput("Day05")
    check(part1(input) == 3608)
//    part2(input).println() // 5570 and 5055 both too high
    check(part2Alt(input) == 4922)
}

private fun List<String>.middlePage(): Int = this[lastIndex / 2].toInt()

private fun List<String>.pagesAfter(page: String): List<String> = subList(indexOf(page) + 1, lastIndex + 1)

private fun List<String>.follow(rules: Set<String>?) = all { rules?.contains(it) == true }

private fun List<Pair<String, String>>.rulesAfter() =
    groupBy(keySelector = { it.first }, valueTransform = { it.second })
        .mapValues { entry -> entry.value.toSet() }

private fun getUpdatesFrom(input: List<String>) = input.subList(input.indexOf("") + 1, input.lastIndex + 1)
    .map { it.split(',') }

private fun getRulesFrom(input: List<String>) = input.subList(0, input.indexOf(""))
    .map { it.substringBefore('|') to it.substringAfter('|') }

private fun MutableList<String>.swapWithNextAt(idx: Int) {
    val temp = this[idx + 1]
    this[idx + 1] = this[idx]
    this[idx] = temp
}
