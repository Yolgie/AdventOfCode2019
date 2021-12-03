import at.cnoize.adventOfCode2019.day03.*
import at.cnoize.adventOfCode2019.day03.Coordinate.Companion.toCoordinate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.converter.ArgumentConverter
import org.junit.jupiter.params.converter.ConvertWith
import org.junit.jupiter.params.provider.CsvSource

class Day03Test {

//    @ParameterizedTest
//    @CsvSource(
//        ""
//    )
//    fun testPuzzle1(input: Int, expected: Int) {
//        assertEquals(expected, input)
//    }

    @ParameterizedTest
    @CsvSource(
        delimiter = ';', value = [
            "[1,1];D;10;[1,-9]",
            "[1,1];L;3;[-2,1]",
            "[1,1];R;99;[100,1]",
            "[1,1];U;2;[1,3]",
            ])
    fun testStep(
        @ConvertWith(CoordinateConverter::class) origin: Coordinate,
        direction: Direction,
        distance: Int,
        @ConvertWith(CoordinateConverter::class) expected: Coordinate,
    ) {
        assertEquals(expected, origin.step(direction, distance))
    }

    // grid tests


    // coordinate test

    @ParameterizedTest
    @CsvSource(
        delimiter = ';', value = [
            "[1,1];[1,2]",
            "[1,1];[2,1]",
            "[1,1];[2,2]",
            "[1,10];[2,10]",
            "[1,100000];[2,1]",
            "[1,2];[2,1]",
            "[-2,-2];[-1,-1]",
            "[-2,-2];[1,-1]",
            "[-2,-2];[-1,1]",
            "[-2,-2];[1,1]",
        ]
    )
    fun testCompareTo(
        @ConvertWith(CoordinateConverter::class) smaller: Coordinate,
        @ConvertWith(CoordinateConverter::class) bigger: Coordinate
    ) {
        assertTrue(smaller < bigger)
        assertTrue(smaller <= bigger)
        assertFalse(bigger < smaller)
        assertFalse(bigger <= smaller)
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = ';', value = [
            "[1,1];[1,1]",
            "[0,0];[0,0]",
            "[-2,0];[-2,0]",
            "[5,-3];[5,-3]",
            "[-10,-12];[-10,-12]",
        ]
    )
    fun testCompareToEquals(
        @ConvertWith(CoordinateConverter::class) first: Coordinate,
        @ConvertWith(CoordinateConverter::class) other: Coordinate
    ) {
        assertEquals(0, first.compareTo(other))
        assertTrue(first <= other)
        assertTrue(first >= other)
        assertFalse(first < other)
        assertFalse(first > other)
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = ';', value = [
            "[1,1];[1,1]",
            "[1,1];[1,2]",
            "[1,1];[2,1]",
            "[1,1];[2,2]",
            "[1,1];[1,10]",
            "[1,1];[10,1]",
            "[10,1];[1,1]",
            "[1,10];[1,1]",
            "[-10,-10];[1,1]",
            "[0,0];[0,0]",
        ]
    )
    fun testRange(
        @ConvertWith(CoordinateConverter::class) start: Coordinate,
        @ConvertWith(CoordinateConverter::class) end: Coordinate
    ) {
        var allTuples = mutableSetOf<Pair<Int, Int>>()
        val closedRange = start..end
        for (x in start.x..end.x) {
            for (y in start.y..end.y){
                assertTrue(closedRange.contains(Coordinate(x, y)), "Coordinate $x,$y is not in closed range")
                allTuples.add(Pair(x, y))
            }
        }
        val rangeList = (start..end).toList()
        assertEquals(allTuples.size, rangeList.size)

        for (coordinate in start..end) {
            assertTrue(allTuples.remove(Pair(coordinate.x, coordinate.y)), "Coordinate $coordinate can not be removed from all coordinates")
        }
        for ((coordinate, tuple) in (start..end).zip(allTuples)) {
            assertEquals(coordinate.x, tuple.first)
            assertEquals(coordinate.y, tuple.second)
        }
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = ';', value = [
            "[-1,-1];[1,1]",
            "[1,1];[-1,-1]",
            "[1,-1];[-1,1]",
            "[-1,1];[1,-1]",
])
          fun testMinimalSquareIterator(
        @ConvertWith(CoordinateConverter::class) start: Coordinate,
        @ConvertWith(CoordinateConverter::class) end: Coordinate

    ) {
        val expectedList = listOf<Coordinate>(
            Coordinate(-1,1),
            Coordinate(0,1),
            Coordinate(1,1),
            Coordinate(-1,0),
            Coordinate(0,0),
            Coordinate(1,0),
            Coordinate(-1,-1),
            Coordinate(0,-1),
            Coordinate(1,-1),
        )
        assertEquals(expectedList.size, MinimalSquare(start, end).toList().size)
        for ((expected, actual) in expectedList.zip(MinimalSquare(start, end))) {
            assertEquals(expected, actual)
        }
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = ';', value = [
            "[1,3];1;3",
            "[-2,3];-2;3",
            "[4,-5];4;-5",
            "[0,0];0;0",
            "[-0,-0];0;0"
        ]
    )
    fun testParseToCoordinate(source: String, expectedX: Int, expectedY: Int) {
        val coordinate = source.toCoordinate()
        assertEquals(expectedX, coordinate.x)
        assertEquals(expectedY, coordinate.y)
    }

    @ParameterizedTest
    @CsvSource(
        delimiter = ';', value = [
            "[1,1];[3,4];5",
            "[-1,-1];[3,4];9",
            "[1,-1];[3,4];7",
            "[1,1];[-3,4];7"
        ]
    )
    fun testManhattanDistantce(
        @ConvertWith(CoordinateConverter::class) start: Coordinate,
        @ConvertWith(CoordinateConverter::class) end: Coordinate,
        expected: Int
    ) {
        assertEquals(expected, start.getManhattanDistance(end))
    }

    class CoordinateConverter : ArgumentConverter {
        override fun convert(source: Any?, context: ParameterContext?): Any {
            return (source as? String)
                ?.toCoordinate()
                ?: throw IllegalArgumentException("Argument has to be a String: $source")
        }
    }
}
