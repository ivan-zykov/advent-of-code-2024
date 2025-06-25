fun main() {
    fun part1(input: List<String>): Int {
        val inputReversed = input.map { it.reversed() }
        val inputVerticalDown = input.transpose()
        val inputVerticalUp = inputVerticalDown.map { it.reversed() }
        val inputDiagonalDown = input.skewRight().transpose()
        val inputDiagonalDownReversed = inputDiagonalDown.map { it.reversed() }
        val inputDiagonalDownToLeft = input.skewLeft().transpose()
        val inputDiagonalDownToLeftReversed = inputDiagonalDownToLeft.map { it.reversed() }

        val inputsCombined = input + inputReversed + inputVerticalDown + inputVerticalUp + inputDiagonalDown +
                inputDiagonalDownReversed + inputDiagonalDownToLeft + inputDiagonalDownToLeftReversed

        return inputsCombined.sumOf {
            """XMAS""".toRegex().findAll(it)
                .count()
        }
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
                        minorLocalSet.containsAll(listOf('M', 'S'))
                    ) {
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
    var appendCount = this.lastIndex
    return buildList {
        while (appendCount >= 0) {
            val prefix = "O".repeat(prependCount)
            val suffix = "O".repeat(appendCount)
            val inputRow = this@skewLeft[prependCount]
            add("$prefix$inputRow$suffix")
            prependCount++
            appendCount--
        }
    }
}

private fun List<String>.skewRight(): List<String> {
    var prependCount = this.lastIndex
    var appendCount = 0
    return buildList {
        while (prependCount >= 0) {
            val prefix = "O".repeat(prependCount)
            val suffix = "O".repeat(appendCount)
            val inputRow = this@skewRight[appendCount]
            add("$prefix$inputRow$suffix")
            prependCount--
            appendCount++
        }
    }
}

private fun List<String>.transpose() = buildList {
    for (j in this@transpose[0].indices) {
        add(buildString {
            for (i in this@transpose.indices) {
                this.append(this@transpose[i][j])
            }
        })
    }
}
