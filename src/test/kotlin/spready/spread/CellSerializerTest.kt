package spready.spread

import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import spready.lisp.sexpr.Cell
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CellSerializerTest {
    @Test
    fun `serialize normal`() {
        val cell = Cell(1, 1)
        val jsonString = Json.encodeToString(cell)
        assertEquals(""""1.1"""", jsonString)
    }

    @Nested
    inner class Deserialize {
        @Test
        fun `deserialize Fail`() {
            val input =
                """"1234""""

            val ex = assertFailsWith<SerializationException> {
                Json.decodeFromString<Cell>(input)
            }
            assertEquals("Can't parse Cell!", ex.message)
        }

        @Test
        fun `deserialize normal`() {
            val input =
                """"1234.456""""

            assertEquals(Cell(1234, 456), Json.decodeFromString(input))
        }
    }
}
