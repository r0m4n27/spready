package spready

import org.junit.jupiter.api.Nested
import spready.lisp.Environment
import spready.lisp.sexpr.Cell
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SpreadTest {

    private var spread = Spread(Environment.defaultEnv())

    @BeforeTest
    fun resetSpread() {
        spread = Spread(Environment.defaultEnv())
    }

    @Nested
    inner class GetTest {
        @Test
        fun `get successfully`() {
            val cell = Cell(12, 12)

            spread[cell] = "(+ 1 2 3)"

            assertEquals("(+ 1 2 3)", spread[cell])
        }

        @Test
        fun `get fail`() {
            val ex = assertFailsWith<SpreadException> {
                spread[Cell(13, 13)]
            }

            assertEquals("Cell not found!", ex.message)
        }
    }

    @Nested
    inner class AllExprs {
        @Test
        fun `allExprs empty`() {
            assertEquals(0, spread.allExprs.size)
        }

        @Test
        fun `allExprs multiple values`() {
            val expected = mapOf(Cell(1, 2) to "6", Cell(2, 3) to "\"hallo\"")
            val input = mapOf(Cell(1, 2) to "(+ 1 2 3)", Cell(2, 3) to "\"hallo\"")

            for (item in input) {
                spread[item.key] = item.value
            }

            for (item in spread.allExprs) {
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

            assertEquals("(+ 1 2 3)", spread[Cell(12, 12)])

            val allExprs = spread.allExprs
            assertEquals(1, allExprs.size)
            assertEquals("6", allExprs[Cell(12, 12)])
        }
    }

    @Test
    fun `minusAssign normal`() {
        spread[Cell(12, 12)] = "(+ 1 2 3)"
        assertEquals("(+ 1 2 3)", spread[Cell(12, 12)])

        spread -= Cell(12, 12)

        assertFailsWith<SpreadException> {
            spread[Cell(12, 12)]
        }
    }
}
