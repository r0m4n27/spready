package spready.lisp.functions.forms

import org.junit.jupiter.api.Nested
import spready.lisp.BaseEval
import spready.lisp.EvalException
import spready.lisp.parse
import spready.lisp.sexpr.Integer
import spready.lisp.tokenize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BindingsTest : BaseEval() {

    @Nested
    inner class LetTest {
        @Test
        fun `let normal`() {
            val input = "(let ((x 2)) x)"

            assertEquals(Integer(2), env.eval(parse(tokenize(input)).first()))
        }

        @Test
        fun `let not cons`() {
            val input = "(let 3 x)"

            assertFailsWith<EvalException> {
                env.eval(parse(tokenize(input)).first())
            }
        }

        @Test
        fun `let not Symbol`() {
            val input = "(let ((3 3)) x)"

            assertFailsWith<EvalException> {
                env.eval(parse(tokenize(input)).first())
            }
        }

        @Test
        fun `let bindings not Cons`() {
            val input = "(let (3) x)"

            assertFailsWith<EvalException> {
                env.eval(parse(tokenize(input)).first())
            }
        }

        @Test
        fun `named let`() {
            equalsEval(
                "6",
                """
                (let fac ((n 1))
                  (if (= 4 n)
                    1
                    (* n (fac (+ n 1)))))
                """.trimIndent()
            )
        }
    }

    @Nested
    inner class LetStarTest {
        @Test
        fun `letStar normal`() {
            val input = "(let* ((x 3)(y (+ x 2))) y)"

            assertEquals(Integer(5), env.eval(parse(tokenize(input)).first()))
        }
    }

    @Nested
    inner class LetRecTest {
        @Test
        fun `letrec normal`() {
            equalsEval(
                "24",
                """
                (letrec ((fac1 (lambda (x)
                          (if (= x 1) 1
                          (* x (fac2 (- x 1))))))
                         (fac2 (lambda (x)
                          (if (= x 1) 1
                          (* x (fac1 (- x 1)))))))
                  (fac1 4))
                """.trimIndent()
            )
        }
    }

    @Nested
    inner class DoTest {
        @Test
        fun `do normal`() {
            equalsEval(
                "25",
                """
        (do ((sum 0 (+ sum (head x)))
             (x '(1 3 5 7 9) (tail x)))
            ((nil? x) sum)
            1)
                """.trimIndent()
            )
        }
    }
}
