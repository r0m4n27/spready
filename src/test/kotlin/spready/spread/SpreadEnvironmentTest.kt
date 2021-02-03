package spready.spread

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertDoesNotThrow
import spready.lisp.functions.math.plus
import spready.lisp.sexpr.Cell
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.ListElem.Companion.toListElem
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.Symbol
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SpreadEnvironmentTest {

    private var results = mutableMapOf<Cell, String>()
    private var env =
        SpreadEnvironment(
            { cell, result -> results[cell] = result.toString() },
            { results[it] = "#Err" }
        )

    @BeforeTest
    fun reset() {
        results = mutableMapOf()
        env = SpreadEnvironment(
            { cell, result -> results[cell] = result.toString() },
            { results[it] = "#Err" }
        )
    }

    @Nested
    inner class Cycles {
        @Test
        fun `simple cycle`() {
            val input = listOf(
                Cell(1, 1) to Integer(3),
                Cell(2, 1) to Cell(1, 1),
                Cell(3, 1) to Cell(2, 1),
                Cell(4, 1) to Cell(3, 1)
            )

            input.forEach {
                env[it.first] = it.second
            }

            val exception = assertFailsWith<SpreadException> {
                env[Cell(2, 1)] = listOf(Cell(1, 1), Cell(4, 1)).toListElem()
            }

            assertEquals("Cycle found!", exception.message)
        }

        @Test
        fun `no cycles`() {
            assertDoesNotThrow {
                val input = mutableMapOf(
                    Cell(1, 1) to Integer(3),
                    Cell(2, 1) to listOf(plus, Cell(1, 1), Integer(3)).toListElem(),
                    Cell(2, 2) to listOf(plus, Cell(2, 1), Integer(3)).toListElem()
                )

                input.forEach {
                    env[it.key] = it.value
                }
            }
        }

        @Test
        fun `Cell references itself`() {
            val cell = Cell(12, 13)
            val expr =
                listOf(Symbol("x"), listOf(cell, Nil).toListElem(), cell).toListElem()

            val exception = assertFailsWith<SpreadException> {
                env[cell] = expr
            }

            assertEquals("Cycle found!", exception.message)
        }
    }

    @Test
    fun `remove cell fail`() {
        val input = mutableMapOf(
            Cell(1, 1) to Integer(3),
            Cell(2, 1) to listOf(plus, Cell(1, 1), Integer(3)).toListElem(),
            Cell(2, 2) to listOf(plus, Cell(2, 1), Integer(3)).toListElem()
        )

        input.forEach {
            env[it.key] = it.value
        }

        val exception = assertFailsWith<SpreadException> {
            env.removeCell(Cell(1, 1))
        }

        assertEquals("#1.1 influences others!", exception.message)
    }

    @Nested
    inner class EvalCell {
        @Test
        fun `notify dependencies`() {
            mutableMapOf(
                Cell(1, 1) to Integer(3),
                Cell(2, 1) to listOf(plus, Cell(1, 1), Integer(3)).toListElem(),
                Cell(2, 2) to listOf(plus, Cell(2, 1), Integer(3)).toListElem()
            ).forEach {
                env[it.key] = it.value
            }

            assertEquals("3", results[Cell(1, 1)])
            assertEquals("6", results[Cell(2, 1)])
            assertEquals("9", results[Cell(2, 2)])
            env[Cell(1, 1)] = Integer(4)

            assertEquals("4", results[Cell(1, 1)])
            assertEquals("7", results[Cell(2, 1)])
            assertEquals("10", results[Cell(2, 2)])
        }

        @Test
        fun `notify complex`() {
            listOf(
                Cell(1, 1) to Integer(3),
                Cell(2, 2) to listOf(plus, Cell(1, 1), Cell(2, 1)).toListElem(),
                Cell(3, 1) to Cell(2, 2),
                Cell(4, 1) to Cell(3, 1)
            ).forEach {
                env[it.first] = it.second
            }

            assertEquals("3", results[Cell(1, 1)])
            assertEquals("#Err", results[Cell(2, 2)])
            assertEquals("#Err", results[Cell(3, 1)])
            assertEquals("#Err", results[Cell(4, 1)])

            env[Cell(2, 1)] = Integer(4)

            assertEquals("7", results[Cell(2, 2)])
            assertEquals("7", results[Cell(4, 1)])
        }
    }
}
