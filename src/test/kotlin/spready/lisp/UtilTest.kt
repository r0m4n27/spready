package spready.lisp

import org.junit.jupiter.api.TestInstance
import spready.lisp.functions.Plus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UtilTest {
    @Test
    fun `evalAll simple`() {
        val input = Cons(Num(1), Cons(Num(2), Nil))

        val expected = listOf(Num(1), Num(2))
        val env = Environment()

        assertEquals(expected, input.evalAll(env))
    }

    @Test
    fun `evalAll complex`() {
        val input = Cons(Cons(Plus, Cons(Num(2), Cons(Num(3), Nil))), Cons(Num(2), Nil))

        val expected = listOf(Num(5), Num(2))
        val env = Environment()

        assertEquals(expected, input.evalAll(env))
    }

    @Test
    fun `cast success`() {
        val input: SExpr = Num(3)

        assertEquals(Num::class, input.cast(Num::class)::class)
    }

    @Test
    fun `cast fail`() {
        val input: SExpr = Num(3)

        assertFailsWith<EvalException> {
            input.cast(Cons::class)
        }
    }
}
