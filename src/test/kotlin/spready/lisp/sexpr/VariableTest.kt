package spready.lisp.sexpr

import org.junit.jupiter.api.Nested
import spready.lisp.BaseEval
import spready.lisp.EvalException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VariableTest : BaseEval() {

    @Nested
    inner class Eval {
        @Test
        fun `eval Variable success`() {
            val expected = Integer(3)
            val input = Symbol("3")

            env[input] = expected

            assertEquals(expected, input.eval(env))
        }

        @Test
        fun `eval Variable fail`() {
            val input = Symbol("3")

            val exception = assertFailsWith<EvalException> {
                input.eval(env)
            }

            assertEquals("Can't find variable 3", exception.message)
        }
    }

    @Nested
    inner class Range {
        @Test
        fun `empty range`() {
            assertEquals(listOf(), Cell(12, 12)..Cell(12, 10))
        }

        @Test
        fun `normal range`() {
            assertEquals(
                setOf(
                    Cell(1, 1),
                    Cell(1, 2),
                    Cell(2, 1),
                    Cell(2, 2),
                    Cell(3, 1),
                    Cell(3, 2),
                ),
                (Cell(1, 1)..Cell(3, 2)).toSet()
            )
        }
    }
}
