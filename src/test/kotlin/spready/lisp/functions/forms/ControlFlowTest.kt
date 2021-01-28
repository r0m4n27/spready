package spready.lisp.functions.forms

import org.junit.jupiter.api.Nested
import spready.lisp.BaseEval
import kotlin.test.Test

class ControlFlowTest : BaseEval() {

    @Nested
    inner class IfTest {
        @Test
        fun `If normal`() {
            equalsEval("4", "(if (+ 1 2) 4 5)")
        }

        @Test
        fun `If true`() {
            equalsEval("4", "(if #t 4 5)")
        }

        @Test
        fun `If false`() {
            equalsEval("5", "(if #f 4 5)")
        }

        @Test
        fun `If nil`() {
            equalsEval("5", "(if nil 4 5)")
        }
    }

    @Nested
    inner class CondTest {
        @Test
        fun `Cond empty`() {
            equalsEval("nil", "(cond)")
        }

        @Test
        fun `Cond only else`() {
            equalsEval("2", "(cond (else 2))")
        }

        @Test
        fun `Cond after else`() {
            equalsEval("2", "(cond (#f 1) (else 2) (#f 3))")
        }

        @Test
        fun `Cond multiple`() {
            equalsEval("2", "(cond (#f 1) (#t 2) (#f 3))")
        }

        @Test
        fun `cond only test`() {
            equalsEval("2", "(cond (2))")
        }

        @Test
        fun `cond lambda`() {
            equalsEval("3", "(cond (2 => (lambda (x) (+ x 1))))")
        }

        @Test
        fun `cond lambda fail`() {
            failsEval("(cond (2 => x))", "Can't find symbol x")
        }

        @Test
        fun `cond none`() {
            equalsEval("nil", "(cond (#f 1) (#f 2) (#f 3))")
        }
    }

    @Nested
    inner class CaseTest {
        @Test
        fun `case empty`() {
            failsEval("(case)", "Must have at least 2 arguments not 0")
        }

        @Test
        fun `case without branches`() {
            failsEval("(case 2)", "Must have at least 2 arguments not 1")
        }

        @Test
        fun `case else`() {
            equalsEval("4", "(case 3 (else 4))")
        }

        @Test
        fun `case after else`() {
            equalsEval("4", "(case 3 ((4 5 6) 7) (else 4) ((7 8 9) 10))")
        }

        @Test
        fun `case normal`() {
            equalsEval("4", "(case 3 ((1 2) 3) ((3 4 5) 4))")
        }
    }

    @Nested
    inner class RunTest {
        @Test
        fun `Run Normal`() {
            equalsEval("3", "(run 1 2 3)")
        }

        @Test
        fun `Run Nil`() {
            equalsEval("nil", "(run)")
        }
    }
}
