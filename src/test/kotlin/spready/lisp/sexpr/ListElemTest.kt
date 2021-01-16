package spready.lisp.sexpr

import org.junit.jupiter.api.Nested
import spready.lisp.BaseEval
import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.functions.Plus
import spready.lisp.parse
import spready.lisp.sexpr.ListElem.Companion.toConsWithTail
import spready.lisp.sexpr.ListElem.Companion.toListElem
import spready.lisp.tokenize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ListElemTest : BaseEval() {

    @Nested
    inner class Iterable {
        @Test
        fun `cons iterable normal`() {
            val input = Cons(Cons(Integer(3), Nil), Cons(Symbol("123"), Nil))
            val expected = listOf(Cons(Integer(3), Nil), Symbol("123"))

            assertEquals(expected, input.toList())
        }

        @Test
        fun `cons iterable non nil end`() {
            val input = Cons(Cons(Integer(3), Nil), Cons(Symbol("123"), Integer(3)))
            val expected = listOf(Cons(Integer(3), Nil), Symbol("123"), Integer(3))

            assertEquals(expected, input.toList())
        }

        @Test
        fun `cons iterable nil`() {
            val input = Cons(Nil, Cons(Nil, Cons(Integer(3), Nil)))
            val expected = listOf(Nil, Nil, Integer(3))

            assertEquals(expected, input.toList())
        }

        @Test
        fun `nil iterable`() {
            val input = Nil
            val expected = listOf<SExpr>()

            assertEquals(expected, input.toList())
        }
    }

    @Nested
    inner class ToListElemTest {
        @Test
        fun `toListElem empty`() {

            assertEquals(Nil, listOf<SExpr>().toListElem())
        }

        @Test
        fun `toListElem normal`() {
            val values = listOf(Integer(123), Nil, Str("123"))

            var cons: SExpr = values.toListElem()
            values.forEach {
                assertEquals(it, (cons as Cons).head)
                cons = (cons as Cons).tail
            }

            assertEquals(Nil, cons as Nil)
        }
    }

    @Nested
    inner class ToConsWithTailTest {
        @Test
        fun `toConsWithTail normal`() {
            val input = listOf(Integer(1), Integer(2), Integer(3))
            val expected = Cons(Integer(1), Cons(Integer(2), Integer(3)))

            assertEquals(expected, input.toConsWithTail())
        }

        @Test
        fun `toConsWithTail fail empty`() {
            val input = listOf<SExpr>()

            assertFailsWith<IllegalArgumentException> { input.toConsWithTail() }
        }

        @Test
        fun `toConsWithTail fail one`() {
            val input = listOf<SExpr>(Integer(1))

            assertFailsWith<IllegalArgumentException> { input.toConsWithTail() }
        }
    }

    @Nested
    inner class ToString {
        @Test
        fun `Cons ToString nested`() {
            val input = Cons(Integer(1), Cons(Integer(2), Cons(Integer(3), Nil)))
            val expected = "(1 2 3)"
            assertEquals(expected, input.toString())
        }

        @Test
        fun `Cons toString nested`() {
            val input =
                Cons(Cons(Integer(1), Cons(Integer(2), Nil)), Cons(Integer(3), Nil))
            val expected = "((1 2) 3)"
            assertEquals(expected, input.toString())
        }

        @Test
        fun `Cons toString end is not Nil`() {
            val input = Cons(Integer(1), Cons(Integer(2), Integer(3)))
            val expected = "(1 2 . 3)"
            assertEquals(expected, input.toString())
        }
    }

    @Nested
    inner class Eval {
        @Test
        fun `eval Cons fail`() {
            val input = Cons(Integer(3), Cons(Integer(2), Nil))

            assertFailsWith<EvalException> {
                input.eval(env)
            }
        }

        @Test
        fun `eval Cons were second arg is Cons`() {
            val input = Cons(Plus, Cons(Integer(2), Cons(Integer(3), Nil)))
            val expected = Integer(5)

            assertEquals(expected, input.eval(env))
        }

        @Test
        fun `eval Cons were second arg is not Cons`() {
            val expected = Integer(3)

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

    @Nested
    inner class HeadTail {
        @Test
        fun `nil head`() {
            assertEquals(Nil, Nil.head)
        }

        @Test
        fun `nil tail`() {
            assertEquals(Nil, Nil.tail)
        }

        @Test
        fun `cons head`() {
            assertEquals(Integer(3), Cons(Integer(3), Integer(4)).head)
        }

        @Test
        fun `cons tail`() {
            assertEquals(Integer(4), Cons(Integer(3), Integer(4)).tail)
        }
    }
}
