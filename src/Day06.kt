fun main() {
    fun part1(input: List<String>): Int {
        val map: Map<Position, Location> = simulateGuardsPatrolFor(input)

        return map.asSequence().count { it.value.isVisited }
    }

    fun part2(input: List<String>): Int {
        val mapInit: Map<Position, Location> = buildMapFor(input)
        val guardInit = Guard(
            position = findGuardsInitPosition(input),
            direction = Direction.UP
        )

        val visitedPositions: Set<Position> = simulateGuardsPatrolFor(input)
            .asSequence()
            .filterNot { it.key == guardInit.position }
            .filter { it.value.isVisited }
            .map { it.key }
            .toSet()

        return visitedPositions.parallelStream()
            .filter { potentialPosition ->
                potentialPosition != null && potentialPosition.causesLooping(mapInit, guardInit)
            }
            .count()
            .toInt()
    }

    fun part2Mutable(input: List<String>): Int {
        val mapInit: MutableMap<Position, Location> = buildMutableMapFor(input)
        val guardInit = Guard(
            position = findGuardsInitPosition(input),
            direction = Direction.UP
        )

        val visitedPositions: Set<Position> = simulateGuardsPatrolFor(input)
            .asSequence()
            .filterNot { it.key == guardInit.position }
            .filter { it.value.isVisited }
            .map { it.key }
            .toSet()

        return visitedPositions.parallelStream()
            .filter { potentialPosition ->
                potentialPosition != null && potentialPosition.causesLoopingMutable(mapInit, guardInit)
            }
            .count()
            .toInt()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    val testInput2 = readInput("Day06_test2")
    check(part1(testInput) == 41)
    check(part2(testInput) == 6)
    check(part2Mutable(testInput) == 6)
    check(part1(testInput2) == 3)
    check(part2(testInput2) == 0)
    check(part2Mutable(testInput2) == 0)

    val input = readInput("Day06")
    check(part1(input) == 4602)
//    check(part2(input) == 1703)
    check(part2Mutable(input) == 1703)
}

private fun Position.causesLooping(
    mapInit: Map<Position, Location>,
    guardInit: Guard
): Boolean {
    var map: Map<Position, Location> = mapInit
    map = map.withObstacleAt(this)
    var guard = guardInit

    var borderReached = false
    val prevPositionsToDirections = mutableSetOf(guard.position to guard.direction)
    while (!borderReached) {
        guard = guard.movedOn(map)

        val newPosition = guard.position
        val newDirection = guard.direction
        if ((newPosition to newDirection) in prevPositionsToDirections) {
//            "Causes a loop: $this".println()
            return true
        } else {
            prevPositionsToDirections.add(newPosition to newDirection)
        }
        map = map.updatedWith(newPosition)

        val newLocation = map[newPosition]
        checkNotNull(newLocation) { "Filed to get new location for checking border" }
        if (newLocation.isBorder) {
            borderReached = true
        }
    }
    return false
}

private fun Position.causesLoopingMutable(
    mapInit: MutableMap<Position, Location>,
    guardInit: Guard
): Boolean {
    val map = mapInit.mapValues { (_, location) -> location.copy() }
        .toMutableMap()
    map.addObstacleAt(this)
    val guard = guardInit.copy()

    var borderReached = false
    val prevPositionsToDirections = mutableSetOf(guard.position to guard.direction)
    while (!borderReached) {
        guard.moveOn(map)

        val newPosition = guard.position
        val newDirection = guard.direction
        if ((newPosition to newDirection) in prevPositionsToDirections) {
//            "Causes a loop: $this".println()
            return true
        } else {
            prevPositionsToDirections.add(newPosition to newDirection)
        }
        map.updateWith(newPosition)

        val newLocation = map[newPosition]
        checkNotNull(newLocation) { "Filed to get new location for checking border" }
        if (newLocation.isBorder) {
            borderReached = true
        }
    }
    return false
}

private fun simulateGuardsPatrolFor(input: List<String>): Map<Position, Location> {
    val map: MutableMap<Position, Location> = buildMutableMapFor(input)

    val guard = Guard(
        position = findGuardsInitPosition(input),
        direction = Direction.UP
    )

    var borderReached = false
    while (!borderReached) {
        guard.moveOn(map)

        val newPosition = guard.position
        map.updateWith(newPosition)

        val newLocation = map[newPosition]
        checkNotNull(newLocation) { "Filed to get new location for checking border" }
        if (newLocation.isBorder) {
            borderReached = true
        }
    }
    return map
}

private fun Map<Position, Location>.withObstacleAt(positionOfNewObstacle: Position): Map<Position, Location> {
    val mutableMap = this.toMutableMap()
    val locationForNewObstacle = mutableMap[positionOfNewObstacle]
    checkNotNull(locationForNewObstacle) { "Could not get location for new obstacle" }
    mutableMap[positionOfNewObstacle] = locationForNewObstacle.copy(isObstacle = true)
    return mutableMap.toMap()
}

private fun MutableMap<Position, Location>.addObstacleAt(positionOfNewObstacle: Position) {
    val locationForNewObstacle = this[positionOfNewObstacle]
    checkNotNull(locationForNewObstacle) { "Could not get location for new obstacle" }
    this[positionOfNewObstacle]?.isObstacle = true
}

private const val OBSTACLE_CHAR = '#'
private const val GUARD_CHAR = '^'

private fun Map<Position, Location>.updatedWith(newPosition: Position): Map<Position, Location> {
    val result = this.toMutableMap()
    val newLocation = result[newPosition]
    checkNotNull(newLocation) { "Failed get location for new position" }
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

private fun MutableMap<Position, Location>.updateWith(newPosition: Position) {
    val newLocation = this[newPosition]
    checkNotNull(newLocation) { "Failed get location for new position" }
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
    this[newPosition]?.isVisited = true
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

private fun buildMutableMapFor(input: List<String>): MutableMap<Position, Location> {
    val map = mutableMapOf<Position, Location>()
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
            map.put(key, value)
        }
    }
    return map
}

private fun findGuardsInitPosition(input: List<String>): Position {
    val inputSequence: Sequence<String> = input.asSequence()
    val lineWithGuard: String? = inputSequence.find { it.contains(GUARD_CHAR) }
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
    var isObstacle: Boolean,
    val isBorder: Boolean,
    var isVisited: Boolean = false
)

data class Guard(
    var position: Position,
    var direction: Direction
) {
    fun movedOn(map: Map<Position, Location>): Guard {
        val newDirection = findNewValidDirection(direction, map, 1)
        val newPosition = nextPositionIn(newDirection)
        return Guard(newPosition, newDirection)
    }

    fun moveOn(map: MutableMap<Position, Location>) {
        val newDirection = findNewValidDirection(direction, map, 1)
        direction = newDirection
        position = nextPositionIn(newDirection)
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
        checkNotNull(locationAhead) { "Failed to find next location while guard moving" }
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
