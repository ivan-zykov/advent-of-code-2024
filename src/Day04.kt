private val pattern = """XMAS""".toRegex()

fun main() {
    fun part1(input: List<String>): Int {
        /*
        Directions:
            - Horizontal
            - Horizontal reversed
            - Vertical down
            - Vertical up
            - Diagonal down right
            - Diagonal up right
            - Diagonal down left
            - Diagonal up left
        Transformations:
            - Transpose matrix. Vertical -> Horizontal
            - Reverse rows. Reversed horizontal -> normal horizontal
            - Shift all 2+ rows right/left. Diagonal -> vertical case
        Solutions:
            1. Transform matrix to just horizontal case and find "XMAS" substring in each row
            2.
         */

        "Original:".println()
        input.forEach { it.println() }

        val horizontalCount = countNormalized(input)
        horizontalCount.println()

        val inputReversed = input.map { it.reversed() }
        "Reversed:".println()
        inputReversed.forEach { it.println() }
        val horizontalReversedCount = countNormalized(inputReversed)
        horizontalReversedCount.println()

        val inputVerticalDown = input.transpose()
        "Vertical down:".println()
        inputVerticalDown.forEach { it.println() }
        val verticalDownCount = countNormalized(inputVerticalDown)
        verticalDownCount.println()

        val inputVerticalUp = inputVerticalDown.map { it.reversed() }
        "Vertical up".println()
        inputVerticalUp.forEach { it.println() }
        val verticalUpCount = countNormalized(inputVerticalUp)
        verticalUpCount.println()

        val inputDiagonalDownSkewed = input.skewRight()
        "Diagonal down".println()
        inputDiagonalDownSkewed.forEach { it.println() }
        val inputDiagonalDownNormalized = inputDiagonalDownSkewed.transpose()
        "Diagonal down normalized".println()
        inputDiagonalDownNormalized.forEach { it.println() }
        val countDiagonalDown = countNormalized(inputDiagonalDownNormalized)

        val inputDiagonalDownReversed = inputDiagonalDownNormalized.map { it.reversed() }
        "Diagonal down reversed".println()
        inputDiagonalDownReversed.forEach { it.println() }
        val countDiagonalDownReversed = countNormalized(inputDiagonalDownReversed)

        val inputDiagonalDownToLeftSkewed = input.skewLeft()
        "Diagonal down to left".println()
        inputDiagonalDownToLeftSkewed.forEach { it.println() }
        val inputDiagonalDownToLeftNormalized = inputDiagonalDownToLeftSkewed.transpose()
        "Diagonal down to left normalized".println()
        inputDiagonalDownToLeftNormalized.forEach { it.println() }
        val countDiagonalDownToLeft = countNormalized(inputDiagonalDownToLeftNormalized)

        val inputDiagonalDownToLeftReversed = inputDiagonalDownToLeftNormalized.map { it.reversed() }
        "Diagonal down to left reversed".println()
        inputDiagonalDownToLeftReversed.forEach { it.println() }
        val countDiagonalDownToLeftReversed = countNormalized(inputDiagonalDownToLeftReversed)

        return horizontalCount + horizontalReversedCount + verticalDownCount + verticalUpCount + countDiagonalDown +
                countDiagonalDownReversed + countDiagonalDownToLeft + countDiagonalDownToLeftReversed
    }

    fun part2(input: List<String>): Int {
        /*
            For each A
                - Find diagonal neighbouring chars
                - Check set of neighbours to contain M & S
         */
        var result = 0
        input.forEachIndexed { lineIdx, line ->
            if (lineIdx == 0 || lineIdx == input.lastIndex) return@forEachIndexed

            line.forEachIndexed { charIdx, char ->
                if (charIdx == 0 || charIdx == line.lastIndex) return@forEachIndexed

                if (char == 'A') {
                    val topLeftChar = input[lineIdx - 1][charIdx - 1]
                    val bottomRightChar = input[lineIdx + 1][charIdx + 1]
                    val majorLocalSet = setOf(topLeftChar, bottomRightChar)

                    val topRightChar = input[lineIdx - 1][charIdx + 1]
                    val bottomLeftChar = input[lineIdx + 1][charIdx - 1]
                    val minorLocalSet = setOf(topRightChar, bottomLeftChar)

                    if (majorLocalSet.containsAll(listOf('M', 'S')) &&
                        minorLocalSet.containsAll(listOf('M', 'S'))) {
                        result++
                    }
                }
            }
        }

        return result
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day04_test1")
    check(part1(testInput1) == 4)
    val testInput2 = readInput("Day04_test2")
    check(part1(testInput2) == 18)

    val input = readInput("Day04")
    check(part1(input) == 2414)

    val testInput3 = readInput("Day04_test3")
    check(part2(testInput3) == 9)
    check(part2(input) == 1871)
}

private fun List<String>.skewLeft(): List<String> {
    var prependCount = 0
    var appendCount = this.size - 1
    val result = mutableListOf<String>()
    while (appendCount >= 0) {
        val prefix = "O".repeat(prependCount)
        val suffix = "O".repeat(appendCount)
        val rowIndex = prependCount
        val inputRow = this[rowIndex]
        val skewedRow = "$prefix$inputRow$suffix"
        result.add(skewedRow)
        prependCount++
        appendCount--
    }
    return result.toList()
}

private fun List<String>.skewRight(): List<String> {
    var prependCount = this.size - 1
    var appendCount = 0
    val result = mutableListOf<String>()
    while (prependCount >= 0) {
        val prefix = "O".repeat(prependCount)
        val suffix = "O".repeat(appendCount)
        val rowIndex = appendCount
        val inputRow = this[rowIndex]
        val skewedRow = "$prefix$inputRow$suffix"
        result.add(skewedRow)
        prependCount--
        appendCount++
    }
    return result.toList()
}

private fun List<String>.transpose(): List<String> {
    val result: MutableList<String> = mutableListOf()
    for (j in 0..<this[1].length) {
        val row = buildString {
            for (i in 0..<this@transpose.size) {
                append(this@transpose[i][j])
            }
        }
        result.add(row)
    }
    return result.toList()
}

private fun countNormalized(input: List<String>) = input.sumOf {
    pattern.findAll(it)
        .count()
}
