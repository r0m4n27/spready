package spready.lisp

import org.junit.jupiter.api.TestInstance
import spready.lisp.functions.Plus
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UtilTest {
    @Test
    fun `evalAll simple`() {
        val input = Cons(Num(1), Cons(Num(2), Nil))

        val expected = listOf(Num(1), Num(2))
        val env = Environment()

        assertEquals(expected, evalAll(input, env))
    }

    @Test
    fun `evalAll complex`() {
        val input = Cons(Cons(Plus, Cons(Num(2), Cons(Num(3), Nil))), Cons(Num(2), Nil))

        val expected = listOf(Num(5), Num(2))
        val env = Environment()

        assertEquals(expected, evalAll(input, env))
    }
}
