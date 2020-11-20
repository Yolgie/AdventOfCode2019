import at.cnoize.adventOfCode2019.day02.runIntcodeProgram
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day02Test {

    @ParameterizedTest
    @CsvSource(
        delimiter = ';', value = [
            "1,9,10,3,2,3,11,0,99,30,40,50; 3500",
            "1,0,0,0,99; 2",
            "2,3,0,3,99; 2",
            "1,1,1,4,99,5,6,0,99; 30"
        ]
    )
    fun testPuzzle1(input: String, expected: String) {
        val intcode = input.split(',').map(String::toInt).toMutableList()
        assertEquals(expected, runIntcodeProgram(intcode).toString())
    }
}