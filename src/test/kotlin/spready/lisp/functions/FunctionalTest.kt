package spready.lisp.functions

import org.junit.jupiter.api.Nested
import spready.lisp.BaseEval
import spready.lisp.functions.forms.Quote
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.ListElem.Companion.toListElem
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionalTest : BaseEval() {
    @Nested
    inner class ZipArgsTest {
        @Test
        fun `zip args normal`() {
            val input = listOf(
                listOf(
                    Quote,
                    listOf(Integer(1), Integer(2), Integer(3)).toListElem()
                ).toListElem(),
                listOf(Quote, listOf(Integer(4), Integer(5)).toListElem()).toListElem(),
                listOf(Quote, listOf(Integer(6), Integer(7)).toListElem()).toListElem()
            )

            assertEquals(
                listOf(
                    listOf(Integer(1), Integer(4), Integer(6)),
                    listOf(Integer(2), Integer(5), Integer(7))
                ),
                zipArgs(input, env)
            )
        }

        @Test
        fun `zip args empty`() {
            val input = listOf(
                listOf(
                    Quote,
                    listOf(Integer(1), Integer(2), Integer(3)).toListElem()
                ).toListElem(),
                listOf<SExpr>().toListElem(),
                listOf(Quote, listOf(Integer(4), Integer(5)).toListElem()).toListElem()
            )

            assertEquals(listOf(), zipArgs(input, env))
        }
    }

    @Nested
    inner class MapTest {
        @Test
        fun `map normal`() {
            equalsEval("'(2 3 4)", "(map (lambda (x) (+ 1 x)) (list 1 2 (+ 1 2)))")
        }

        @Test
        fun `map empty`() {
            equalsEval("'()", "(map (lambda (x) (+ 1 x)) '())")
        }

        @Test
        fun `map multiple`() {
            equalsEval(
                "'(14 17)",
                """
                (map (lambda (x y z) (+ x y z))
                     '(1 2 3)
                     '(4 5 6 7 8)
                     '(9 10))
                """.trimIndent()
            )
        }
    }

    @Nested
    inner class ForEachTest {
        @Test
        fun `foreach normal`() {
            evalString(
                """
                (val z 0)
                """.trimIndent()
            )

            equalsEval(
                "nil",
                """

                (for-each (lambda (x) (val z (+ z x))) '(1 2 3))
                """.trimIndent()
            )

            assertEquals(Integer(6), env[Symbol("z")])
        }

        @Test
        fun `for-each empty`() {
            equalsEval("'()", "(for-each (lambda (x) (+ 1 x)) '())")
        }
    }

    @Nested
    inner class ApplyTest {
        @Test
        fun `apply different types`() {
            equalsEval("15", "(apply + 1 '() 2 '(3 4) 5)")
        }
    }
}
