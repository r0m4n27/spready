package spready.lisp.sexpr

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.functions.Plus
import spready.lisp.parse
import spready.lisp.sexpr.Cons.Companion.toCons
import spready.lisp.tokenize
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ConsTest {
    private var env = Environment()

    @BeforeTest
    fun resetEnv() {
        env = Environment()
    }

    @Nested
    inner class Iterable {
        @Test
        fun `cons iterable normal`() {
            val input = Cons(Cons(Num(3), Nil), Cons(Symbol("123"), Nil))
            val expected = listOf(Cons(Num(3), Nil), Symbol("123"))

            assertEquals(expected, input.toList())
        }

        @Test
        fun `cons iterable non nil end`() {
            val input = Cons(Cons(Num(3), Nil), Cons(Symbol("123"), Num(3)))
            val expected = listOf(Cons(Num(3), Nil), Symbol("123"), Num(3))

            assertEquals(expected, input.toList())
        }

        @Test
        fun `cons iterable nil`() {
            val input = Cons(Nil, Cons(Nil, Cons(Num(3), Nil)))
            val expected = listOf(Nil, Nil, Num(3))

            assertEquals(expected, input.toList())
        }
    }

    @Test
    fun `toCons empty`() {

        assertEquals(Nil, listOf<SExpr>().toCons())
    }

    @Test
    fun `build cons normal`() {
        val values = listOf(Num(123), Nil, Str("123"))

        var cons: SExpr = values.toCons()
        values.forEach {
            assertEquals(it, (cons as Cons).first)
            cons = (cons as Cons).second
        }

        assertEquals(Nil, cons as Nil)
    }

    @Nested
    inner class ToString {
        @Test
        fun `Cons ToString nested`() {
            val input = Cons(Num(1), Cons(Num(2), Cons(Num(3), Nil)))
            val expected = "(1 2 3)"
            assertEquals(expected, input.toString())
        }

        @Test
        fun `Cons toString nested`() {
            val input = Cons(Cons(Num(1), Cons(Num(2), Nil)), Cons(Num(3), Nil))
            val expected = "((1 2) 3)"
            assertEquals(expected, input.toString())
        }

        @Test
        @Disabled
        fun `Cons toString end is not Nil`() {
            val input = Cons(Num(1), Cons(Num(2), Num(3)))
            val expected = "(1 2 3)"
            assertEquals(expected, input.toString())
        }
    }

    @Nested
    inner class Eval {
        @Test
        fun `eval Cons fail`() {
            val input = Cons(Num(3), Cons(Num(2), Nil))

            assertFailsWith<EvalException> {
                input.eval(env)
            }
        }

        @Test
        fun `eval Cons were second arg is Cons`() {
            val input = Cons(Plus, Cons(Num(2), Cons(Num(3), Nil)))
            val expected = Num(5)

            assertEquals(expected, input.eval(env))
        }

        @Test
        fun `eval Cons were second arg is not Cons`() {
            val expected = Num(3)

            val func = object : Func("Test") {
                override fun invoke(env: Environment, args: List<SExpr>): SExpr {
                    assertEquals(expected, args[0])

                    return Nil
                }
            }

            val input = Cons(func, expected)
            input.eval(env)
        }

        @Test
        fun `eval Unquote fail`() {
            val input = ",(+ 1 2)"
            env[Symbol("+")] = Plus

            assertFailsWith<EvalException> {
                env.eval(parse(tokenize(input)))
            }
        }

        @Test
        fun `eval UnquoteSplice fail`() {
            val input = ",@(+ 1 2)"
            env[Symbol("+")] = Plus

            assertFailsWith<EvalException> {
                env.eval(parse(tokenize(input)))
            }
        }
    }
}
