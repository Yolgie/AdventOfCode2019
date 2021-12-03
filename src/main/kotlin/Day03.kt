package at.cnoize.adventOfCode2019.day03

import Worker
import runOnInputFile
import kotlin.math.abs
import kotlin.math.max

const val INPUT_FILE = "Day03.input"

fun main() {
    runOnInputFile(INPUT_FILE, workerPuzzle1)
    //runOnInputFile(INPUT_FILE, workerPuzzle2)
}

val workerPuzzle1 = Worker { input ->
    val (wireA, wireB) = input.map { wireString ->
        wireString.split(',').map(String::toWirePath)
    }

    val origin = Coordinate(0, 0)
    val allWiresA = getAllCoordinatesForWire(origin, wireA)
    val allWiresB = getAllCoordinatesForWire(origin, wireB)

    val wireboard = Wireboard()
        .addOrUpdateAllCoordinates(allWiresA) { node -> node.copy(wireA = true) }
        .addOrUpdateAllCoordinates(allWiresB) { node -> node.copy(wireB = true) }

    wireboard.visualize()

    input.toString()
}

private fun getAllCoordinatesForWire(
    origin: Coordinate,
    wirePath: List<WirePath>
): Set<Coordinate> {
    return getPathCoordinates(origin, wirePath)
        .zipWithNext()
        .flatMap { (start, end) -> (start..end) }
        .toSet()
}

val workerPuzzle2 = Worker { input ->
    input.toString()
}

fun getPathCoordinates(start: Coordinate, wire: List<WirePath>): Sequence<Coordinate> {
    return sequence {
        yield(start)
        var current = start
        for (nextWirePath in wire) {
            current = current.step(nextWirePath.direction, nextWirePath.distance)
            yield(current)
        }
    }
}

data class WirePath(val direction: Direction, val distance: Int)

fun WirePath.getDestinationCoordinate(start: Coordinate): Coordinate {
    return when (direction) {
        Direction.U -> Coordinate(start.x, start.y + distance)
        Direction.D -> Coordinate(start.x, start.y - distance)
        Direction.L -> Coordinate(start.x - distance, start.y)
        Direction.R -> Coordinate(start.x + distance, start.y)
    }
}

fun String.toWirePath(): WirePath {
    return WirePath(Direction.valueOf(this.substring(0, 1)), this.substring(1).toInt())
}

enum class Direction {
    U, L, D, R;

    fun nextClockwise(): Direction {
        return valueArray[(this.ordinal + valueArray.size - 1) % valueArray.size]
    }

    fun nextCounterClockwise(): Direction {
        return valueArray[(this.ordinal + 1) % valueArray.size]
    }

    companion object {
        private val valueArray = values()
    }
}


data class Wireboard(override val nodes: Map<Coordinate, WireboardElement> = emptyMap()) :
    EndlessGrid<WireboardElement> {
    override val coordinates: Set<Coordinate> = nodes.keys

    override fun visualize() {
        require(coordinates.isNotEmpty()) { "Empty board can not be visualized" }
        val fullBoard = MinimalSquare(coordinates, padding = 1)
        val fullBoardWithFrame = MinimalSquare(fullBoard, padding = 1)
        for (coordinate in fullBoardWithFrame) {
            val node: Node? = when (coordinate) {
                !in fullBoard -> FrameNode()
                else -> nodes[coordinate]
            }
            print(node?.gridChar ?: ' ')
            if (coordinate.x == fullBoardWithFrame.maxX) {
                println()
            }
        }
    }

    fun addOrUpdateNode(
        coordinate: Coordinate,
        updater: (WireboardElement) -> WireboardElement
    ): Wireboard {
        val newNodes = this.nodes.toMutableMap()
        newNodes[coordinate] = newNodes.getOrDefault(coordinate, WireboardElement()).run(updater)
        return Wireboard(newNodes.toMutableMap())
    }

    fun addOrUpdateAllCoordinates(
        coordinates: Collection<Coordinate>,
        updater: (WireboardElement) -> WireboardElement
    ): Wireboard {
        val newNodes = this.nodes.toMutableMap()
        coordinates.forEach { coordinate ->
            newNodes[coordinate] = newNodes.getOrDefault(coordinate, WireboardElement()).run(updater)
        }
        return Wireboard(newNodes.toMutableMap())
    }
}

data class WireboardElement(val wireA: Boolean = false, val wireB: Boolean = false) : Node {
    override val gridChar: Char = when {
        wireA && wireB -> 'X'
        wireA && !wireB -> 'A'
        !wireA && wireB -> 'B'
        else -> '.'
    }
}

data class FrameNode(override val gridChar: Char = '#') : Node

interface EndlessGrid<NODE : Node> {
    val coordinates: Set<Coordinate>
    val nodes: Map<Coordinate, NODE>
    fun visualize()
}

interface Node {
    val gridChar: Char
}

data class Coordinate(val x: Int, val y: Int) : Comparable<Coordinate> {
    //    val xComparator = compareBy<Coordinate> { it.x }
//    val yComparator = compareBy<Coordinate> { it.y }
    private val digitLength = max(x.toString().length, y.toString().length)

    fun getManhattanDistance(other: Coordinate): Int {
        return abs(this.x - other.x) + abs(this.y - other.y)
    }

    override fun compareTo(other: Coordinate): Int {
        val comparator = compareBy<Coordinate> { it.x }.thenBy { it.y }
        return comparator.compare(this, other)
        //return origin.getManhattanDistance(this).compareTo(origin.getManhattanDistance(other))
    }

    operator fun rangeTo(that: Coordinate) = CoordinateRange(this, that)

    override fun toString(): String {
        return """[${x.zeroPad(digitLength)},${y.zeroPad(digitLength)}]"""
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Coordinate

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    fun step(direction: Direction, distance: Int = 1): Coordinate {
        return when (direction) {
            Direction.U -> Coordinate(x, y + distance)
            Direction.D -> Coordinate(x, y - distance)
            Direction.L -> Coordinate(x - distance, y)
            Direction.R -> Coordinate(x + distance, y)
        }

    }

    companion object {
        private val regex = Regex("""\[(-?\d*?),(-?\d*?)]""")

        fun String.toCoordinate(): Coordinate {
            if (!this.matches(regex))
                throw IllegalArgumentException("Could not parse $this into a coordinate")
            val (x, y) = regex.find(this)!!.destructured
            return Coordinate(x.toInt(), y.toInt())
        }
    }
}

class CoordinateRange(override val start: Coordinate, override val endInclusive: Coordinate) : ClosedRange<Coordinate>,
    Iterable<Coordinate> {

    override fun iterator(): Iterator<Coordinate> {
        return EasyCoordinateIterator(start, endInclusive)
    }
}

class EasyCoordinateIterator(val start: Coordinate, val endInclusive: Coordinate) : Iterator<Coordinate> {
    var next = start

    override fun hasNext(): Boolean {
        return next <= endInclusive
    }

    override fun next(): Coordinate {
        if (!hasNext())
            throw NoSuchElementException()
        val current = next
        next = when {
            next.y < endInclusive.y -> Coordinate(next.x, next.y + 1)
            else -> Coordinate(next.x + 1, start.y)
        }
        return current
    }
}

class ReadingDirectionCoordinateIterator(val minimalSquare: MinimalSquare) : Iterator<Coordinate> {
    var next = minimalSquare.topLeft

    override fun hasNext(): Boolean {
        return minimalSquare.contains(next)
    }

    override fun next(): Coordinate {
        if (!hasNext())
            throw NoSuchElementException()
        val current = next
        next = when {
            next.x < minimalSquare.maxX -> Coordinate(next.x + 1, next.y)
            else -> Coordinate(minimalSquare.minX, next.y - 1)
        }
        return current
    }
}

class SpiralCoordinateSequence(val start: Coordinate, val endInclusive: Coordinate) : Sequence<Coordinate> {
    override fun iterator(): Iterator<Coordinate> {
        return SpiralCoordinateIterator(start, endInclusive)
    }
}

// todo write test for this
// todo use this to find the closest crossing to origin
class SpiralCoordinateIterator(val start: Coordinate, val endInclusive: Coordinate) : Iterator<Coordinate> {
    var next = start
    var top = start.y
    var bottom = start.y
    var left = start.x
    var right = start.x
    var direction = Direction.R
    var hasNext = true

    override fun hasNext(): Boolean {
        return hasNext
    }

    override fun next(): Coordinate {
        if (!hasNext())
            throw NoSuchElementException()

        val current = next
        if (current == endInclusive) {
            hasNext = false;
        }
        if (current.x !in left..right || current.y !in bottom..top) {
            direction = direction.nextClockwise()
        }
        top = max(top, next.y)
        bottom = kotlin.math.min(bottom, next.y)
        left = kotlin.math.min(left, next.x)
        right = max(right, next.x)

        next = next.step(direction)

        return current
    }
}

fun Int.zeroPad(length: Int): String = this.toString().padStart(length, '0')

class MinimalSquare(coordinates: Collection<Coordinate>, padding: Int = 0) : Iterable<Coordinate> {
    constructor(minimalSquare: MinimalSquare, padding: Int = 0) : this(
        minimalSquare.bottomRight,
        minimalSquare.topLeft,
        padding = padding
    )

    constructor(vararg coordinates: Coordinate, padding: Int = 0) : this(coordinates.toList(), padding)

    val minX = coordinates.minOf { it.x } - padding
    val minY = coordinates.minOf { it.y } - padding
    val maxY = coordinates.maxOf { it.y } + padding
    val maxX = coordinates.maxOf { it.x } + padding

    val topLeft = Coordinate(minX, maxY)
    val topRight = Coordinate(maxX, maxY)
    val bottomLeft = Coordinate(minX, minY)
    val bottomRight = Coordinate(maxX, minY)

    override fun iterator(): Iterator<Coordinate> {
        return ReadingDirectionCoordinateIterator(this)
    }

    fun contains(element: Coordinate): Boolean {
        return (minX..maxX).contains(element.x) && (minY..maxY).contains(element.y)
    }
}
