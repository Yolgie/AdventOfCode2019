import at.cnoize.adventOfCode2019.day01.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day01Test {

    @ParameterizedTest
    @CsvSource(
        "12, 2",
        "14, 2",
        "1969, 654",
        "100756, 33583"
    )
    fun testGetFuel(input: Int, expected: Int) {
        assertEquals(expected, getFuel(input))
    }

    @ParameterizedTest
    @CsvSource(
        "12, 2",
        "1969, 966",
        "100756, 50346"
    )
    fun testGetFuelRecursive(input: Int, expected: Int) {
        assertEquals(expected, getFuelRecursive(input))
    }
}