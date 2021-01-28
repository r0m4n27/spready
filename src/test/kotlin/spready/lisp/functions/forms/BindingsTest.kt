package spready.lisp.functions.forms

import org.junit.jupiter.api.Nested
import spready.lisp.BaseEval
import kotlin.test.Test

class BindingsTest : BaseEval() {

    @Nested
    inner class LetTest {
        @Test
        fun `let normal`() {
            equalsEval("2", "(let ((x 2)) x)")
        }

        @Test
        fun `let not cons`() {
            failsEval("(let 3 x)")
        }

        @Test
        fun `let not Symbol`() {
            failsEval("(let ((3 3)) x)")
        }

        @Test
        fun `let bindings not Cons`() {
            failsEval("(let (3) x)")
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
            equalsEval("5", "(let* ((x 3)(y (+ x 2))) y)")
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
