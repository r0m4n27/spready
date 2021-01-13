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

    @Nested
    inner class EqTest {
        @Test
        fun `Eq True`() {
            equalsEval("#t", "(eq? '(1 2) '(1 2))")
        }

        @Test
        fun `Eq True nil`() {
            equalsEval("#t", "(eq? '() nil)")
        }

        @Test
        fun `Eq False`() {
            equalsEval("#f", "(eq? 3 '(1 2))")
        }
    }

    @Nested
    inner class NeqTest {
        @Test
        fun `Neq True`() {
            equalsEval("#f", "(neq? '(1 2) '(1 2))")
        }

        @Test
        fun `Neq True nil`() {
            equalsEval("#f", "(neq? '() nil)")
        }

        @Test
        fun `Neq False`() {
            equalsEval("#t", "(neq? 3 '(1 2))")
        }
    }
}
