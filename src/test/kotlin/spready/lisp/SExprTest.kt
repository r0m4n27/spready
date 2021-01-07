package spready.lisp

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import spready.lisp.Cons.Companion.toCons
import spready.lisp.functions.Plus
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SExprTest {

    private var env = Environment()

    @BeforeTest
    fun resetEnv() {
        env = Environment()
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
        fun `eval Symbol success`() {
            val expected = Num(3)
            val input = Symbol("3")

            env[input] = expected

            assertEquals(expected, input.eval(env))
        }

        @Test
        fun `eval Symbol fail`() {
            val input = Symbol("3")

            assertFailsWith<EvalException> {
                input.eval(env)
            }
        }

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
                override fun invoke(env: Environment, args: Cons): SExpr {
                    assertEquals(expected, args.first)
                    assertEquals(Nil, args.second)

                    return Nil
                }
            }

            val input = Cons(func, expected)
            input.eval(env)
        }
    }

    @Nested
    inner class ConsTest {
        @Test
        fun `toListCheckSize normal`() {
            val input = Cons(Num(1), Cons(Num(2), Nil))
            val expected = listOf(Num(1), Num(2))

            assertEquals(expected, input.toListWithSize(2))
        }

        @Test
        fun `toListCheckSize fail`() {
            val input = Cons(Num(1), Cons(Num(2), Nil))

            assertFailsWith<EvalException> {
                input.toListWithSize(5)
            }
        }

        @Test
        fun `toMutListMinSize fail`() {
            val input = Cons(Num(1), Nil)

            assertFailsWith<EvalException> {
                input.toMutListMinSize(2)
            }
        }

        @Test
        fun `toMutListMinSize normal`() {
            val input = Cons(Num(1), Cons(Num(2), Cons(Symbol("x"), Nil)))
            val expected = mutableListOf(Num(1), Num(2), Symbol("x"))

            assertEquals(expected, input.toMutListMinSize(1))
        }

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
        fun `toCons empty`() {

            assertEquals(Cons(Nil, Nil), listOf<SExpr>().toCons())
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
    }
}
