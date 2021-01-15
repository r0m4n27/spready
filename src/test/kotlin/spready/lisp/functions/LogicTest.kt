package spready.lisp.functions

import org.junit.jupiter.api.Nested
import spready.lisp.BaseEval
import kotlin.test.Test

class LogicTest : BaseEval() {

    @Nested
    inner class AndTest {
        @Test
        fun `And True`() {
            equalsEval("#t", "(and 1 2 3)")
        }

        @Test
        fun `And False`() {
            equalsEval("#f", "(and 1 2 #f)")
        }

        @Test
        fun `And Empty`() {
            equalsEval("#t", "(and)")
        }
    }

    @Nested
    inner class OrTest {
        @Test
        fun `Or True`() {
            equalsEval("#t", "(or 1 2 #f)")
        }

        @Test
        fun `And False`() {
            equalsEval("#f", "(or nil #f #f)")
        }

        @Test
        fun `And Empty`() {
            equalsEval("#f", "(or)")
        }
    }

    @Nested
    inner class NotTest {
        @Test
        fun `Not False`() {
            equalsEval("#f", "(not 'x)")
        }

        @Test
        fun `Not True`() {
            equalsEval("#t", "(not '())")
        }
    }
}
