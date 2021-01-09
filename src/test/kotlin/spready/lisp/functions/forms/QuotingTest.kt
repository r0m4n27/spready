package spready.lisp.functions.forms

import org.junit.jupiter.api.Nested
import spready.lisp.Environment
import spready.lisp.functions.Plus
import spready.lisp.parse
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.Num
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import spready.lisp.tokenize
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class QuotingTest {
    private var env = Environment()

    @BeforeTest
    fun resetEnv() {
        env = Environment()
        env += quotingFunctions()
    }

    fun evalString(string: String): SExpr {
        return env.eval(parse(tokenize(string)).first())
    }

    @Nested
    inner class QuoteTest {
        @Test
        fun `quote normal`() {
            val input = "'x"

            assertEquals(Symbol("x"), evalString(input))
        }

        @Test
        fun `quote cons`() {
            val input = "'(1 2 3)"

            assertEquals(
                Cons(Num(1), Cons(Num(2), Cons(Num(3), Nil))),
                evalString(input)
            )
        }

        @Test
        fun `quote empty`() {
            val input = "'()"

            assertEquals(
                Nil,
                evalString(input)
            )
        }
    }

    @Nested
    inner class QuasiQuoteTest {
        @Test
        fun `quasiquote symbol`() {
            val input = "`x"

            assertEquals(Symbol("x"), evalString(input))
        }

        @Test
        fun `quasiquote cons`() {
            val input = "`(1 2 ,(+ 1 2))"
            env[Symbol("+")] = Plus

            assertEquals(
                Cons(Num(1), Cons(Num(2), Cons(Num(3), Nil))),
                evalString(input)
            )
        }

        @Test
        fun `quasiquote mutiple cons`() {
            val input = "`(1 (2 ,(+ 1 2)))"
            env[Symbol("+")] = Plus

            assertEquals(
                Cons(Num(1), Cons(Cons(Num(2), Cons(Num(3), Nil)), Nil)),
                evalString(input)
            )
        }

        // TODO: Test QuoteSplicing
    }
}
