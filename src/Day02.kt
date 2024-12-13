import kotlin.math.absoluteValue

fun main() {
    fun part1(input: List<String>): Int {
        // Parse to Int
        val inputOfInt = input.map { report ->
            report.split(" ")
                .map { value -> value.toInt() }
        }
//            .also { println("Input parsed: $it") }

        // Map to deltas
        val allDeltas = inputOfInt.map { report ->
            val reportDeltas = mutableListOf<Int>()
            report.forEachIndexed() { idx, value ->
                if (idx != report.lastIndex) reportDeltas.add(value - report[idx + 1])
            }
            reportDeltas.toList()
        }
//            .also { println("Deltas: $it") }

        // Filter by requirements
        return allDeltas
            .filter { report ->
                report.all { value -> value < 0 }.or(report.all { value -> 0 < value })
            }.count { report ->
                report.all { value -> value.absoluteValue in (1..3) }
            }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 2)

    val input = readInput("Day02")
    check(part1(input) == 257)
//    part2(input).println()
}
