package spready.lisp.functions.forms

import org.junit.jupiter.api.Nested
import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.Num
import spready.lisp.Symbol
import spready.lisp.functions.Plus
import spready.lisp.parse
import spready.lisp.tokenize
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BindingsTest {

    private var env = Environment()

    @BeforeTest
    fun resetEnv() {
        env = Environment()
    }

    @Nested
    inner class LetTest {
        @Test
        fun `let normal`() {
            val input = "(let ((x 2)) x)"
            env[Symbol("let")] = Let

            assertEquals(Num(2), env.eval(parse(tokenize(input)).first()))
        }

        @Test
        fun `let not cons`() {
            val input = "(let 3 x)"
            env[Symbol("let")] = Let

            assertFailsWith<EvalException> {
                env.eval(parse(tokenize(input)).first())
            }
        }

        @Test
        fun `let not Symbol`() {
            val input = "(let ((3 3)) x)"
            env[Symbol("let")] = Let

            assertFailsWith<EvalException> {
                env.eval(parse(tokenize(input)).first())
            }
        }

        @Test
        fun `let bindings not Cons`() {
            val input = "(let (3) x)"
            env[Symbol("let")] = Let

            assertFailsWith<EvalException> {
                env.eval(parse(tokenize(input)).first())
            }
        }
    }

    @Nested
    inner class LetStarTest {
        @Test
        fun `letStar normal`() {
            env[Symbol("let*")] = LetStar
            env[Symbol("+")] = Plus

            val input = "(let* ((x 3)(y (+ x 2))) y)"

            assertEquals(Num(5), env.eval(parse(tokenize(input)).first()))
        }
    }

    // TODO: Test Letrec
    // Its hard without predicates
}
