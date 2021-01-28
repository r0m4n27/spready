package spready.lisp.sexpr

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import spready.lisp.BaseEval
import spready.lisp.EvalException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SExprTest : BaseEval() {

    @Nested
    inner class Cast {
        @Test
        fun `cast success`() {
            val input: SExpr = Integer(3)

            assertEquals(Integer::class, input.cast<Integer>()::class)
        }

        @Test
        fun `cast multiple`() {
            val input: List<SExpr> = listOf(Integer(3), Integer(3))

            input.cast<Integer>().forEach {
                assertEquals(Integer::class, it::class)
            }
        }

        @Test
        fun `cast fail`() {
            val input: SExpr = Integer(3)

            assertFailsWith<EvalException> {
                input.cast<Cons>()
            }
        }
    }

    @Nested
    inner class Eval {
        @Test
        fun `eval Symbol success`() {
            val expected = Integer(3)
            val input = Symbol("3")

            env[input] = expected

            assertEquals(expected, input.eval(env))
        }

        @Test
        fun `eval Symbol fail`() {
            val input = Symbol("3")

            assertFailsWith<EvalException> {
                input.eval(env)
            }
        }

        @Test
        fun `eval Unquote fail`() {
            failsEval(",(+ 1 2)", """, cant be used outside of "`"""")
        }

        @Test
        fun `eval UnquoteSplice fail`() {
            failsEval(",@(+ 1 2)", ",@ cant be used outside of \"`\"")
        }
    }
}
