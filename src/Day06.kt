fun main() {
    fun part1(input: List<String>): Int {
        val map = simulateGuardsPatrolFor(input)

        return map.asSequence().count { it.value.isVisited }
    }

    fun part2(input: List<String>): Int {
        /*
        1. Run part 1 and note all visited locations
        2. Find potential locations for new Obst.
            - Can be visited (from 1. above)
            - (Optional: Will not direct guard to border)
        3. For each potential location with new Obst.
            - Start guard moving
            - Record each visited location and guard's direction
        4. If position with same direction visited twice
            - Guard is in the loop
            - Stop trial for this potential location
            - Record this location
         */

        val mapInit = buildMapFor(input)
        val guardInit = Guard(
            position = findGuardsInitPosition(input),
            direction = Direction.UP
        )

        val visitedPositions = simulateGuardsPatrolFor(input)
            .asSequence()
            .filterNot { it.key == guardInit.position }
            .filter { it.value.isVisited }
            .map { it.key }
        val positionsCausingLoop = mutableSetOf<Position>()
//        todo: Try check for all positions
//        todo: Try checking potential positions in parallel (co-routines)
        visitedPositions.asSequence().forEach { potentialPosition ->
            var map = mapInit
            map = map.withObstacleAt(potentialPosition)
            var guard = guardInit

            var borderReached = false
            val prevPositionsToDirections = mutableSetOf(guard.position to guard.direction)
            while (!borderReached) {
                guard = guard.movedOn(map)

                val newPosition = guard.position
                val newDirection = guard.direction
                if ((newPosition to newDirection) in prevPositionsToDirections) {
                    positionsCausingLoop.add(potentialPosition)
                    "Causes a loop: $potentialPosition".println()
                    return@forEach
                } else {
                    prevPositionsToDirections.add(newPosition to newDirection)
                }
                map = map.updatedWith(newPosition)

                val newLocation = map[newPosition]
                check(newLocation != null) { "Filed to get new location for checking border" }
                if (newLocation.isBorder) {
                    borderReached = true
                }
            }
        }

        return positionsCausingLoop.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    val testInput2 = readInput("Day06_test2")
    check(part1(testInput) == 41)
    check(part2(testInput) == 6)
    check(part1(testInput2) == 3)
    check(part2(testInput2) == 0)

    val input = readInput("Day06")
    check(part1(input) == 4602)
    check(part2(input) == 1703)
}

private fun simulateGuardsPatrolFor(input: List<String>): Map<Position, Location> {
    var map = buildMapFor(input)

    var guard = Guard(
        position = findGuardsInitPosition(input),
        direction = Direction.UP
    )

    var borderReached = false
    while (!borderReached) {
        guard = guard.movedOn(map)

        val newPosition = guard.position
        map = map.updatedWith(newPosition)

        val newLocation = map[newPosition]
        check(newLocation != null) { "Filed to get new location for checking border" }
        if (newLocation.isBorder) {
            borderReached = true
        }
    }
    return map
}

private fun Map<Position, Location>.withObstacleAt(positionOfNewObstacle: Position): Map<Position, Location> {
    val mutableMap = this.toMutableMap()
    val locationForNewObstacle = mutableMap[positionOfNewObstacle]
    check(locationForNewObstacle != null) { "Could not get location for new obstacle" }
    mutableMap[positionOfNewObstacle] = locationForNewObstacle.copy(isObstacle = true)
    return mutableMap.toMap()
}

private const val OBSTACLE_CHAR = '#'
private const val GUARD_CHAR = '^'

private fun Map<Position, Location>.updatedWith(newPosition: Position): Map<Position, Location> {
    val result = this.toMutableMap()
    val newLocation = result[newPosition]
    check(newLocation != null) { "Failed get location for new position" }
    check(!newLocation.isObstacle) {
        """
        | Error! Trying to step on obstacle.
        | New position:
        | $newPosition
        | New location:
        | $newLocation
        | Map:
        | $this
    """.trimIndent()
    }
    result[newPosition] = newLocation.copy(isVisited = true)
    return result.toMap()
}

private fun buildMapFor(input: List<String>) = buildMap {
    input.asSequence().forEachIndexed { i, line ->
        line.asSequence().forEachIndexed { j, char ->
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
    val inputSequence = input.asSequence()
    val lineWithGuard = inputSequence.find { it.contains(GUARD_CHAR) }
    val i = inputSequence.indexOf(lineWithGuard)
    val j = lineWithGuard?.asSequence()?.indexOf(GUARD_CHAR) ?: -1
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
    fun movedOn(map: Map<Position, Location>): Guard {
        val newDirection = findNewValidDirection(direction, map, 1)
        val newPosition = nextPositionIn(newDirection)
        return Guard(newPosition, newDirection)
    }

    private fun findNewValidDirection(
        currentDirection: Direction,
        map: Map<Position, Location>,
        iterCount: Int
    ): Direction {
        require(iterCount <= 3) {
            """
            | Guards tries turning 4th time. So, he is in an invalid position i.e. trapped.
            | Current iteration number:
            | $iterCount
            | Current direction:
            | $currentDirection
            | Guard:
            | $this
            | Locations with obstacles:
            | ${map.filter { it.value.isObstacle }}
            | Map:
            | $map
        """.trimIndent()
        }
        val locationAhead = map[nextPositionIn(currentDirection)]
        check(locationAhead != null) { "Failed to find next location while guard moving" }
        val newDirection = if (locationAhead.isObstacle) {
            when (currentDirection) {
                Direction.UP -> Direction.RIGHT
                Direction.RIGHT -> Direction.DOWN
                Direction.DOWN -> Direction.LEFT
                Direction.LEFT -> Direction.UP
            }
        } else {
            currentDirection
        }
        // Check location ahead again and so on
        if (map[nextPositionIn(newDirection)]?.isObstacle == true) {
            return findNewValidDirection(newDirection, map, iterCount + 1)
        }
        return newDirection
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
