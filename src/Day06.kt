fun main() {
    fun part1(input: List<String>): Int {
        var map = buildMapFor(input)

        var guard = Guard(
            position = findGuardsInitPosition(input),
            direction = Direction.UP
        )

        var borderReached = false
        while (!borderReached) {
            guard = guard.moveOn(map)

            val newPosition = guard.position
            map = map.updatedWith(newPosition)

            val newLocation = map[newPosition]
            check(newLocation != null) { "Filed to get new location for checking border" }
            if (newLocation.isBorder) {
                borderReached = true
            }
        }

        return map.count { it.value.isVisited }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 41)

    val input = readInput("Day06")
    check(part1(input) == 4602)
//    part2(input).println()
}

private const val OBSTACLE_CHAR = '#'
private const val GUARD_CHAR = '^'

private fun Map<Position, Location>.updatedWith(newPosition: Position): Map<Position, Location> {
    val result = this.toMutableMap()
    val newLocation = result[newPosition]
    check(newLocation != null) {"Failed get location for new position"}
    result[newPosition] = newLocation.copy(isVisited = true)
    return result.toMap()
}

private fun buildMapFor(input: List<String>) = buildMap {
    input.forEachIndexed { i, line ->
        line.forEachIndexed { j, char ->
            val key = Position(i, j)

            val isVerticalBorder = j in listOf(0, line.lastIndex)
            val isHorizontalBorder = i in listOf(0, input.lastIndex)

            val value = Location(
                isObstacle = char == OBSTACLE_CHAR,
                isBorder = isVerticalBorder || isHorizontalBorder,
                isVisited = char == GUARD_CHAR
            )
            put(key, value)
        }
    }
}

private fun findGuardsInitPosition(input: List<String>): Position {
    val lineWithGuard = input.find { it.contains(GUARD_CHAR) }
    val i = input.indexOf(lineWithGuard)
    val j = lineWithGuard?.indexOf(GUARD_CHAR) ?: -1
    check(i > 0) { "Could not find initial x coordinate of guard" }
    return Position(i, j)
}

data class Position(
    val i: Int,
    val j: Int
)

data class Location(
    val isObstacle: Boolean,
    val isBorder: Boolean,
    val isVisited: Boolean = false
)

data class Guard(
    val position: Position,
    val direction: Direction
) {
    fun moveOn(map: Map<Position, Location>): Guard {
        val locationAhead = map[nextPositionIn(direction)]
        check(locationAhead != null) { "Failed to find next location while guard moving" }
        val newDirection = if (locationAhead.isObstacle) {
            when (direction) {
                Direction.UP -> Direction.RIGHT
                Direction.RIGHT -> Direction.DOWN
                Direction.DOWN -> Direction.LEFT
                Direction.LEFT -> Direction.UP
            }
        } else {
            direction
        }
        val newPosition = nextPositionIn(newDirection)
        return Guard(newPosition, newDirection)
    }

    private fun nextPositionIn(newDirection: Direction) = when (newDirection) {
        Direction.UP -> position.copy(i = position.i - 1)
        Direction.DOWN -> position.copy(i = position.i + 1)
        Direction.LEFT -> position.copy(j = position.j - 1)
        Direction.RIGHT -> position.copy(j = position.j + 1)
    }
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}
