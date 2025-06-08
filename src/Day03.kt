fun main() {
    fun part1(input: List<String>): Int {
        val instructPattern = """mul\(\d{1,3},\d{1,3}\)""".toRegex()

        return instructPattern.findAll(input.joinToString())
            .map { instruction -> instruction.value.substring(4, instruction.value.lastIndex) }
            .sumOf { numberCommaNumber -> numberCommaNumber.substringBefore(",").toInt() * numberCommaNumber.substringAfter(",").toInt() }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 161)

    val inputPart1 = readInput("Day03_part1")
    check(part1(inputPart1) == 159833790)
//    part2(input).println()
}
