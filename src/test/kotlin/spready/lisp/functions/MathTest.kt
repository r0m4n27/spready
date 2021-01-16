package spready.lisp.functions

import org.junit.jupiter.api.TestInstance
import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.Symbol
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MathTest {
    private var env = Environment()

    @BeforeTest
    fun resetEnv() {
        env = Environment()
    }

    @Test
    fun `reduceToInteger success`() {
        val input = listOf(Integer(1), Integer(2), Integer(3))
        val expected = Integer(6)

        assertEquals(expected, reduceToInteger(input, Int::plus))
    }

    @Test
    fun `reduceToInteger fail`() {
        val input = listOf(Integer(1), Symbol("123"), Integer(3))

        assertFailsWith<EvalException> {
            reduceToInteger(input, Int::plus)
        }
    }

    @Test
    fun `Plus normal`() {
        val input =
            Cons(Plus, Cons(Integer(1), Cons(Integer(2), Cons(Integer(3), Nil))))
        val expected = Integer(6)
        assertEquals(expected, input.eval(env))
    }

    @Test
    fun `Minus normal`() {
        val input =
            Cons(Minus, Cons(Integer(10), Cons(Integer(2), Cons(Integer(3), Nil))))
        val expected = Integer(5)
        assertEquals(expected, input.eval(env))
    }

    @Test
    fun `Times normal`() {
        val input =
            Cons(
                Times,
                Cons(
                    Integer(1),
                    Cons(Integer(2), Cons(Integer(3), Cons(Integer(4), Nil)))
                )
            )
        val expected = Integer(24)
        assertEquals(expected, input.eval(env))
    }

    @Test
    fun `Times zero`() {
        val input =
            Cons(
                Times,
                Cons(
                    Integer(1),
                    Cons(Integer(2), Cons(Integer(0), Cons(Integer(4), Nil)))
                )
            )
        val expected = Integer(0)
        assertEquals(expected, input.eval(env))
    }
}
