package spready.lisp.functions

import org.junit.jupiter.api.TestInstance
import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.Num
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
    fun `foldToNum success`() {
        val input = listOf(Num(1), Num(2), Num(3))
        val expected = Num(6)

        assertEquals(expected, foldToNum(input, 0, Int::plus))
    }

    @Test
    fun `foldToNum fail`() {
        val input = listOf(Num(1), Symbol("123"), Num(3))

        assertFailsWith<EvalException> {
            foldToNum(input, 0, Int::plus)
        }
    }

    @Test
    fun `Plus normal`() {
        val input = Cons(Plus, Cons(Num(1), Cons(Num(2), Cons(Num(3), Nil))))
        val expected = Num(6)
        assertEquals(expected, input.eval(env))
    }

    @Test
    fun `Minus normal`() {
        val input = Cons(Minus, Cons(Num(1), Cons(Num(2), Cons(Num(3), Nil))))
        val expected = Num(-6)
        assertEquals(expected, input.eval(env))
    }

    @Test
    fun `Times normal`() {
        val input =
            Cons(Times, Cons(Num(1), Cons(Num(2), Cons(Num(3), Cons(Num(4), Nil)))))
        val expected = Num(24)
        assertEquals(expected, input.eval(env))
    }

    @Test
    fun `Times zero`() {
        val input =
            Cons(Times, Cons(Num(1), Cons(Num(2), Cons(Num(0), Cons(Num(4), Nil)))))
        val expected = Num(0)
        assertEquals(expected, input.eval(env))
    }
}
