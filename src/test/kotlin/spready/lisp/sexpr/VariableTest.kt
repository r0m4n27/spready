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
}
