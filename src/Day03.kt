fun main() {
    fun part1(input: List<String>): Int {
        val instructPattern = """mul\(\d{1,3},\d{1,3}\)""".toRegex()

        return instructPattern.findAll(input.joinToString())
            .map { instruction -> instruction.value.substring(4, instruction.value.lastIndex) }
            .sumOf { numberCommaNumber ->
                numberCommaNumber.substringBefore(",").toInt() * numberCommaNumber.substringAfter(",").toInt()
            }
    }

    fun part1Alt(input: List<String>): Int {
        val instructPattern = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()

        return input.sumOf { lineOfInput ->
            instructPattern.findAll(lineOfInput)
                .sumOf {
                    val (first, second) = it.destructured
                    first.toInt() * second.toInt()
                }
        }
    }

    fun part2(input: List<String>): Int = input.joinToString().splitToSequence("do()")
        .map { piece -> piece.substringBefore("don't()") }
        .sumOf { cleanPiece -> part1(listOf(cleanPiece)) }

    fun part2Alt(input: List<String>): Int {
        val cleanInput = input.joinToString()
            .splitToSequence("do()")
            .map { it.substringBefore("don't()") }
            .toList()

        return part1Alt(cleanInput)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 161)

    val input = readInput("Day03")
    check(part1(input) == 159833790)
    check(part1Alt(input) == 159833790)
    check(part2(input) == 89349241)
    check(part2Alt(input) == 89349241)
}
