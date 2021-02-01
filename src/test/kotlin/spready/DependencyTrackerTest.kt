package spready

import org.junit.jupiter.api.Nested
import spready.lisp.Environment
import spready.lisp.functions.math.plus
import spready.lisp.sexpr.Cell
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.ListElem.Companion.toListElem
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DependencyTrackerTest {

    private var env = Environment.defaultEnv()
    private var parsed: MutableMap<Cell, SExpr> = mutableMapOf()
    private var tracker = DependencyTracker(env, parsed)

    @BeforeTest
    fun reset() {
        env = Environment.defaultEnv()
        parsed = mutableMapOf()
        tracker = DependencyTracker(env, parsed)
    }

    @Nested
    inner class UpdateCell {

        @Test
        fun `Cell references itself`() {
            val cell = Cell(12, 13)
            val expr =
                listOf(Symbol("x"), listOf(cell, Nil).toListElem(), cell).toListElem()

            parsed[cell] = expr

            val exception = assertFailsWith<SpreadException> {
                tracker.updateCell(cell)
            }

            assertEquals("Cell cant reference itself", exception.message)
        }

        @Test
        fun `Can't find parsed`() {
            val cell = Cell(12, 13)
            val exception = assertFailsWith<SpreadException> {
                tracker.updateCell(cell)
            }

            assertEquals("Cant find cell!", exception.message)
        }

        @Test
        fun `notify dependencies`() {
            parsed.putAll(
                mutableMapOf(
                    Cell(1, 1) to Integer(3),
                    Cell(2, 1) to listOf(plus, Cell(1, 1), Integer(3)).toListElem(),
                    Cell(2, 2) to listOf(plus, Cell(2, 1), Integer(3)).toListElem()
                )
            )

            for (item in parsed) {
                env.evalAndRegister(item.key, item.value)
                tracker.updateCell(item.key)
            }

            assertEquals(Integer(3), env[Cell(1, 1)])
            assertEquals(Integer(6), env[Cell(2, 1)])
            assertEquals(Integer(9), env[Cell(2, 2)])

            env.evalAndRegister(Cell(1, 1), Integer(4))
            parsed[Cell(1, 1)] = Integer(4)
            tracker.updateCell(Cell(1, 1))

            assertEquals(Integer(4), env[Cell(1, 1)])
            assertEquals(Integer(7), env[Cell(2, 1)])
            assertEquals(Integer(10), env[Cell(2, 2)])
        }
    }

    @Test
    fun `remove cell fail`() {
        parsed.putAll(
            mutableMapOf(
                Cell(1, 1) to Integer(3),
                Cell(2, 1) to listOf(plus, Cell(1, 1), Integer(3)).toListElem(),
                Cell(2, 2) to listOf(plus, Cell(2, 1), Integer(3)).toListElem()
            )
        )

        for (item in parsed) {
            tracker.updateCell(item.key)
        }

        val exception = assertFailsWith<SpreadException> {
            tracker.removeCell(Cell(1, 1))
        }

        assertEquals("#1.1 influences others!", exception.message)
    }
}
