package spready.lisp

import kotlin.test.Test
import kotlin.test.assertEquals

class SExprTest {
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
    fun `Cons toString end is not Nil`() {
        val input = Cons(Num(1), Cons(Num(2), Num(3)))
        val expected = "(1 2 3)"
        assertEquals(expected, input.toString())
    }
}
