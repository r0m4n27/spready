package spready.lisp.functions.procedures

import spready.lisp.BaseEval
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.Num
import kotlin.test.Test
import kotlin.test.assertEquals

class ListTest : BaseEval() {
    @Test
    fun `list empty`() {
        equalsEval("nil", "(list)")
    }

    @Test
    fun `list normal`() {
        equalsEval("'(1 2 3)", "(list 1 2 (+ 1 2))")
    }

    @Test
    fun `cons normal`() {
        assertEquals(Cons(Num(1), Num(2)), evalString("(cons 1 2)"))
    }

    @Test
    fun `head nil`() {
        assertEquals(Nil, evalString("(head nil)"))
    }

    @Test
    fun `tail nil`() {
        assertEquals(Nil, evalString("(tail nil)"))
    }

    @Test
    fun `head normal`() {
        assertEquals(Num(1), evalString("(head '(1 2 3))"))
    }

    @Test
    fun `tail normal`() {
        assertEquals(evalString("'(2 3)"), evalString("(tail '(1 2 3))"))
    }

    @Test
    fun `set-head normal`() {
        assertEquals(evalString("'(12 2 3)"), evalString("(set-head '(1 2 3) 12 )"))
    }

    @Test
    fun `set-tail normal`() {
        assertEquals(evalString("'(1 4 5)"), evalString("(set-tail '(1 2 3) '(4 5))"))
    }

    @Test
    fun `cons length`() {
        assertEquals(evalString("3"), evalString("(len '(1 2 3))"))
    }

    @Test
    fun `nil length`() {
        equalsEval("0", "(len '())")
    }

    @Test
    fun `append single`() {
        assertEquals(evalString("'(1 2 3)"), evalString("(append '(1) 2 3)"))
    }

    @Test
    fun `append cons`() {
        assertEquals(
            evalString("'(1 2 3 4 5)"),
            evalString("(append '(1) '(2 3) '(4 5))")
        )
    }

    @Test
    fun `append nil`() {
        equalsEval("'(1 2 3 4 5)", "(append '() '(1 2) '() '(3 4 5))")
    }

    @Test
    fun `reverse normal`() {
        assertEquals(evalString("'(3 2 1)"), evalString("(reverse '(1 2 3))"))
    }

    @Test
    fun `reverse empty`() {
        assertEquals(evalString("'()"), evalString("(reverse '())"))
    }

    @Test
    fun `get neg position`() {
        failsEval("(get -1 '(1 2))")
    }

    @Test
    fun `get pos too big cons`() {
        failsEval("(get 2 '(1 2))")
    }

    @Test
    fun `get pos too big nil`() {
        failsEval("(get 0 '())")
    }

    @Test
    fun `get pos normal`() {
        equalsEval("'a", "(get 0 '(a b c))")
    }

    @Test
    fun `sublist negative`() {
        failsEval("(sublist -1 '(1 2))")
    }

    @Test
    fun `sublist zero symbol`() {
        equalsEval("'x", "(sublist 0 'x)")
    }

    @Test
    fun `sublist zero cons`() {
        equalsEval("'(1 2 3)", "(sublist 0 '(1 2 3))")
    }

    @Test
    fun `sublist normal`() {
        equalsEval("'(2 3)", "(sublist 1 '(1 2 3))")
    }

    @Test
    fun `member nil`() {
        equalsEval("'()", "(member 2 '())")
    }

    @Test
    fun `member not found`() {
        equalsEval("'()", "(member 2 '(1 3 4))")
    }

    @Test
    fun `member normal`() {
        equalsEval("'(3 4 5)", "(member 3 '(1 2 3 4 5))")
    }

    @Test
    fun `assoc nil`() {
        equalsEval("'()", "(assoc 2 '())")
    }

    @Test
    fun `assoc not found`() {
        equalsEval("'()", "(assoc 7 '((1 2) (3 4 5) (6)))")
    }

    @Test
    fun `assoc normal`() {
        equalsEval("'(3 4 5)", "(assoc 3 '((1 2) (3 4 5) (6)))")
    }
}
