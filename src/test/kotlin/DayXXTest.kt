import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class DayXXTest {

    @ParameterizedTest
    @CsvSource(
        ""
    )
    fun testPuzzle1(input: Int, expected: Int) {
        assertEquals(expected, input)
    }
}