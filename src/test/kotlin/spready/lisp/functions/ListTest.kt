package spready.lisp.functions

import org.junit.jupiter.api.Nested
import spready.lisp.BaseEval
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Integer
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
        assertEquals(Cons(Integer(1), Integer(2)), evalString("(cons 1 2)"))
    }

    @Test
    fun `head nil`() {
        equalsEval("nil", "(head nil)")
    }

    @Test
    fun `tail nil`() {
        equalsEval("nil", "(tail nil)")
    }

    @Test
    fun `head normal`() {
        equalsEval("1", "(head '(1 2 3))")
    }

    @Test
    fun `tail normal`() {
        equalsEval("'(2 3)", "(tail '(1 2 3))")
    }

    @Test
    fun `set-head normal`() {
        equalsEval("'(12 2 3)", "(set-head '(1 2 3) 12)")
    }

    @Test
    fun `set-tail normal`() {
        equalsEval("'(1 4 5)", "(set-tail '(1 2 3) '(4 5))")
    }

    @Test
    fun `cons length`() {
        equalsEval("3", "(len '(1 2 3))")
    }

    @Test
    fun `nil length`() {
        equalsEval("0", "(len '())")
    }

    @Test
    fun `append single`() {
        equalsEval("'(1 2 3)", "(append '(1) 2 3)")
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
        equalsEval("'(3 2 1)", "(reverse '(1 2 3))")
    }

    @Test
    fun `reverse empty`() {
        equalsEval("'()", "'()")
    }

    @Test
    fun `get neg position`() {
        failsEval("(get -1 '(1 2))", "Position cant be negative!")
    }

    @Test
    fun `get pos too big cons`() {
        failsEval("(get 2 '(1 2))", "List is only 2 big!")
    }

    @Test
    fun `get pos too big nil`() {
        failsEval("(get 0 '())", "List is only 0 big!")
    }

    @Test
    fun `get pos normal`() {
        equalsEval("'a", "(get 0 '(a b c))")
    }

    @Test
    fun `sublist negative`() {
        failsEval("(sublist -1 '(1 2))", "Position cant be negative!")
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

    @Nested
    inner class MemberTest {
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
        fun `member custom fun wrong`() {
            failsEval("(member 3 '(1 2 3) nil)", "Expected Func got nil!")
        }

        @Test
        fun `member custom fun`() {
            equalsEval(
                "'(4 5)",
                "(member 3 '(1 2 3 4 5) (lambda (x y) (= (+ x 1) y)))"
            )
        }
    }

    @Nested
    inner class AssocTest {
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

        @Test
        fun `assoc custom fun wrong`() {
            failsEval("(assoc 3 '(1 2 3) nil)", "Expected Func got nil!")
        }

        @Test
        fun `assoc custom fun`() {
            equalsEval(
                "'(3 4)",
                "(assoc 2 '((1 2) (3 4) (5)) (lambda (x y) (= (+ x 1) y)))"
            )
        }
    }
}
