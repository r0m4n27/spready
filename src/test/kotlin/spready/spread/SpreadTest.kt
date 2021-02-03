package spready.spread

import org.junit.jupiter.api.Nested
import spready.lisp.sexpr.Cell
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SpreadTest {

    private var spread = Spread()

    @BeforeTest
    fun resetSpread() {
        spread = Spread()
    }

    @Nested
    inner class InputTest {
        @Test
        fun `get successfully`() {
            val cell = Cell(12, 12)

            spread[cell] = "(+ 1 2 3)"

            assertEquals("(+ 1 2 3)", spread.getInput(cell))
        }

        @Test
        fun `get all`() {
            val input = mapOf(Cell(1, 1) to "1", Cell(1, 2) to "2")
            input.forEach {
                spread[it.key] = it.value
            }

            assertEquals(input, spread.allInputs)
        }

        @Test
        fun `get fail`() {
            val ex = assertFailsWith<SpreadException> {
                spread.getInput(Cell(1, 1))
            }

            assertEquals("Cell not found!", ex.message)
        }
    }

    @Nested
    inner class AllExprs {
        @Test
        fun `allResults empty`() {
            assertEquals(0, spread.allResults.size)
        }

        @Test
        fun `allResults multiple values`() {
            val expected = mapOf(Cell(1, 2) to "6", Cell(2, 3) to "\"hallo\"")
            val input = mapOf(Cell(1, 2) to "(+ 1 2 3)", Cell(2, 3) to "\"hallo\"")

            for (item in input) {
                spread[item.key] = item.value
            }

            for (item in spread.allResults) {
                assertEquals(expected[item.key], item.value)
            }
        }
    }

    @Nested
    inner class SetTest {
        @Test
        fun `set fail`() {
            val exception = assertFailsWith<SpreadException> {
                spread[Cell(12, 12)] = "1 2 3"
            }

            assertEquals("Cell input can only have 1 Expression!", exception.message)
        }

        @Test
        fun `set normal`() {
            spread[Cell(12, 12)] = "(+ 1 2 3)"

            assertEquals("6", spread.getResult(Cell(12, 12)))
            assertEquals("(+ 1 2 3)", spread.getInput(Cell(12, 12)))
        }
    }

    @Nested
    inner class MinusAssign {
        @Test
        fun `minusAssign normal`() {
            spread[Cell(12, 12)] = "(+ 1 2 3)"
            assertEquals(
                "(+ 1 2 3)",
                spread.getInput(Cell(12, 12))
            )

            spread -= Cell(12, 12)

            assertFailsWith<SpreadException> {
                spread.getInput(Cell(12, 12))
            }
        }

        @Test
        fun `minusAssign fail`() {
            spread[Cell(12, 12)] = "(+ 1 2 3)"
            spread[Cell(1, 1)] = "#12.12"

            val exception = assertFailsWith<SpreadException> {
                spread -= Cell(12, 12)
            }

            assertEquals("#12.12 influences others!", exception.message)
        }
    }
}
