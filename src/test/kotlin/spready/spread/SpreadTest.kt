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

            spread.setCell(cell, "(+ 1 2 3)")

            assertEquals("(+ 1 2 3)", spread.getInput(cell))
        }

        @Test
        fun `get all`() {
            val input = mapOf(Cell(1, 1) to "1", Cell(1, 2) to "2")
            input.forEach {
                spread.setCell(it.key, it.value)
            }

            assertEquals(input, spread.allInputs)
        }

        @Test
        fun `get Input null`() {
            assertEquals(null, spread.getInput(Cell(1, 1)))
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
                spread.setCell(item.key, item.value)
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
                spread.setCell(Cell(12, 12), " 1 2 3")
            }

            assertEquals("Cell input can only have 1 Expression!", exception.message)
        }

        @Test
        fun `set normal`() {
            spread.setCell(Cell(12, 12), "(+ 1 2 3)")

            assertEquals(mapOf(Cell(12, 12) to "6"), spread.allResults)
            assertEquals("(+ 1 2 3)", spread.getInput(Cell(12, 12)))
        }

        @Test
        fun `set script`() {
            spread.setScript("test", "(val x 2) (val y (+ x 3))")

            spread.setCell(Cell(1, 1), "y")
            assertEquals(mapOf(Cell(1, 1) to "5"), spread.allResults)
        }
    }

    @Nested
    inner class MinusAssign {
        @Test
        fun `minusAssign normal`() {
            spread.setCell(Cell(12, 12), "(+ 1 2 3)")
            assertEquals(
                "(+ 1 2 3)",
                spread.getInput(Cell(12, 12))
            )

            spread -= Cell(12, 12)

            assertEquals(null, spread.getInput(Cell(12, 12)))
        }

        @Test
        fun `minusAssign fail`() {
            spread.setCell(Cell(12, 12), "(+ 1 2 3)")
            spread.setCell(Cell(1, 1), "#12.12")

            val exception = assertFailsWith<SpreadException> {
                spread -= Cell(12, 12)
            }

            assertEquals("#12.12 influences others!", exception.message)
        }
    }

    @Nested
    inner class Changed {
        @Test
        fun `change cells`() {
            spread.setCell(Cell(1, 1), "#2.1")
            spread.setCell(Cell(2, 1), "1")

            assertEquals(setOf(Cell(1, 1), Cell(2, 1)), spread.changedCells)

            spread.setCell(Cell(3, 1), "123")

            assertEquals(setOf(Cell(3, 1)), spread.changedCells)
        }
    }
}
