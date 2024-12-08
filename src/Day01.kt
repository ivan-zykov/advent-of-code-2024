import kotlin.math.absoluteValue
import kotlin.time.measureTimedValue

fun List<String>.toOneList(selector: List<String>.() -> String) = asSequence()
    .map {
        it.split("\\s+".toRegex())
            .selector()
            .toInt()
    }
    .sorted()
    .toList()

fun main() {

    fun part1(input: List<String>): Int {

        val first = input.toOneList { first() }
        val second = input.toOneList { last() }

        return first.asSequence()
            .zip(second.asSequence())
            .sumOf { (it.first - it.second).absoluteValue }
    }

    fun part2(input: List<String>): Int {
        val (value, timeTaken) = measureTimedValue {
            val first = input.toOneList { first() }
            val second = input.toOneList { last() }

            first.sumOf { firstId ->
                second.count { secondId ->
                    secondId == firstId
                } * firstId
            }
        }
        println("Part 2 original, time taken: ${timeTaken.inWholeMilliseconds} milliseconds")
        return value
    }

    fun part2Alt(input: List<String>): Int {
        val (value, timeTaken) = measureTimedValue {
            val first = input.toOneList { first() }
            val second = input.toOneList { last() }

            val secondIdsToCounts: Map<Int, Int> = second.groupingBy { it }.eachCount()

            first.sumOf { firstId ->
                (secondIdsToCounts[firstId] ?: 0) * firstId
            }
        }
        println("Part 2 with map, time taken: ${timeTaken.inWholeMilliseconds} milliseconds")
        return value
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day01_test1")
    check(part1(testInput1) == 11)
    val testInput2 = readInput("Day01_test2")
    check(part2(testInput2) == 31)

    val input = readInput("Day01")
    check(part1(input) == 1873376)
    check(part2(input) == 18997088)
    check(part2Alt(input) == 18997088)
}
