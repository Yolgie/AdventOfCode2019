package at.cnoize.adventOfCode2019.day03

import Worker
import runOnInputFile
import kotlin.math.abs

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
    val allWiresA = getPathCoordinates(origin, wireA).toList().zipWithNext()
    val allWiresB = getPathCoordinates(origin, wireB).zipWithNext().toList()

    val wireboard = Wireboard()
        .addAllWires(allWiresA) { node -> node.copy(wireA = true) }
        .addAllWires(allWiresB) { node -> node.copy(wireB = true) }

    //wireboard.visualize()

    input.toString()
}

val workerPuzzle2 = Worker { input ->
    input.toString()
}

fun getPathCoordinates(start: Coordinate, wire: List<WirePath>): Sequence<Coordinate> {
    return sequence {
        yield(start)
        var current = start
        for (nextWirePath in wire) {
            current = nextWirePath.getDestinationCoordinate(current)
            yield(current)
        }
    }
}

data class WirePath(val direction: Direction, val lenght: Int)

fun WirePath.getDestinationCoordinate(start: Coordinate): Coordinate {
    return when (direction) {
        Direction.U -> Coordinate(start.x, start.y + lenght)
        Direction.D -> Coordinate(start.x, start.y - lenght)
        Direction.L -> Coordinate(start.x - lenght, start.y)
        Direction.R -> Coordinate(start.x + lenght, start.y)
    }
}

fun String.toWirePath(): WirePath {
    return WirePath(Direction.valueOf(this.substring(0, 1)), this.substring(1).toInt())
}

enum class Direction { U, D, L, R }

data class Wireboard(override val nodes: Map<Coordinate, WireboardElement> = emptyMap()) :
    EndlessGrid<WireboardElement> {
    override val coordinates: Set<Coordinate> = nodes.keys

    override fun visualize() {
        require(coordinates.isNotEmpty()) { "Empty board can not be visualized" }
        val fullBoard = MinimalSquare(*(coordinates.toTypedArray()), padding = 1)
        val fullBoardWithFrame = MinimalSquare(fullBoard, padding = 1)
        for (coordinate in fullBoardWithFrame) {
            val node: Node? = when {
                coordinate !in fullBoard -> FrameNode()
                else -> nodes[coordinate]
            }
            print(node?.gridChar ?: ' ')
            if (coordinate.x == fullBoardWithFrame.maxX) {
                println()
            }
        }
    }

    fun addAllWires(
        wires: List<Pair<Coordinate, Coordinate>>,
        updater: (WireboardElement) -> WireboardElement
    ): Wireboard {
        return wires.fold(this) { board, wire -> board.addWire(wire.first, wire.second, updater) }
    }

    fun addWire(
        start: Coordinate,
        end: Coordinate,
        updater: (WireboardElement) -> WireboardElement
    ): Wireboard {
        return (start..end).fold(this) { board, coordinate -> board.addOrUpdateNode(coordinate, updater) }
    }

    fun addOrUpdateNode(
        coordinate: Coordinate,
        updater: (WireboardElement) -> WireboardElement
    ): Wireboard {
        return when (coordinate) {
            in this.coordinates -> Wireboard(this.nodes.mapValues { (key, node) ->
                print('.')
                if (key == coordinate) updater(
                    node
                ) else (node)
            })
            else -> Wireboard(this.nodes.plus(Pair(coordinate, updater(WireboardElement()))))
        }
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
    val xComparator = compareBy<Coordinate> { it.x }
    val yComparator = compareBy<Coordinate> { it.y }
    private val digitLength = Math.max(x.toString().length, y.toString().length)

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

    companion object {
        private val regex = Regex("""\[(-?\d*?),(-?\d*?)]""")
        private val origin = Coordinate(0, 0)

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
        val current = next
        next = when {
            next.x < minimalSquare.maxX -> Coordinate(next.x + 1, next.y)
            else -> Coordinate(minimalSquare.minX, next.y - 1)
        }
        return current
    }
}

fun Int.zeroPad(lenght: Int): String = this.toString().padStart(lenght, '0')

class MinimalSquare(vararg coordinates: Coordinate, padding: Int = 0) : Iterable<Coordinate> {
    constructor(minimalSquare: MinimalSquare, padding: Int = 0) : this(
        minimalSquare.bottomRight,
        minimalSquare.topLeft,
        padding = padding
    )

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

//// assumptions: 2d cartesian coordinates
//interface Grid<NODE, COORDINATE> where NODE : GridNode<COORDINATE>, COORDINATE : Coordinate {
//    val nodes: List<NODE>
//
//    fun visualize()
//    fun getNode(position : COORDINATE) : NODE?
//}
//
//interface EndlessGrid<NODE, COORDINATE> : Grid<NODE, COORDINATE> where NODE : GridNode<COORDINATE>, COORDINATE : Coordinate {
//    val min: COORDINATE = nodes.map(GridNode::coordinate)
//    val max: COORDINATE
//}
//
//interface GridNode<COORDINATE : Coordinate> {
//    val gridChar: Char
//    val coordinate: COORDINATE
//}
//
//interface Coordinate : Comparable<Coordinate>
//
//class Coordinate2d(val x: Int, val y: Int) : Coordinate, Comparable<Coordinate2d> {
//    override fun compareTo(other: Coordinate2d): Int {
//        TODO("Not yet implemented")
//    }
//
//    override fun compareTo(other: Coordinate): Int {
//        TODO("Not yet implemented")
//    }
//}