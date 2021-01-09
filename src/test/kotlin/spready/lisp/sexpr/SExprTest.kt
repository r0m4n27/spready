package spready.lisp.sexpr

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import spready.lisp.Environment
import spready.lisp.EvalException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SExprTest {

    private var env = Environment()

    @BeforeTest
    fun resetEnv() {
        env = Environment()
    }

    @Nested
    inner class Cast {
        @Test
        fun `cast success`() {
            val input: SExpr = Num(3)

            assertEquals(Num::class, input.cast<Num>()::class)
        }

        @Test
        fun `cast fail`() {
            val input: SExpr = Num(3)

            assertFailsWith<EvalException> {
                input.cast<Cons>()
            }
        }
    }

    @Nested
    inner class Eval {
        @Test
        fun `eval Symbol success`() {
            val expected = Num(3)
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
    }
}
