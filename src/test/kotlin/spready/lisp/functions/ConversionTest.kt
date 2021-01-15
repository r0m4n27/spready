package spready.lisp.functions

import org.junit.jupiter.api.Nested
import spready.lisp.BaseEval
import kotlin.test.Test

class ConversionTest : BaseEval() {
    @Nested
    inner class ToListTest {
        @Test
        fun `toList list`() {
            equalsEval("'(1 2 3)", "(to-list (list 1 2 (+ 1 2)))")
        }

        @Test
        fun `toList str`() {
            equalsEval(
                """
                '("a" "b" "c")
                """.trimIndent(),
                "(to-list \"abc\")"
            )
        }

        @Test
        fun `toList other`() {
            failsEval("(to-list 3)")
        }
    }

    @Nested
    inner class ToStrTest {
        @Test
        fun `toStr str`() {
            equalsEval("\"abc\"", "(to-str \"abc\")")
        }

        @Test
        fun `toStr symbol`() {
            equalsEval("\"x\"", "(to-str 'x)")
        }

        @Test
        fun `toStr num`() {
            equalsEval("\"123\"", "(to-str 123)")
        }

        @Test
        fun `toStr list`() {
            equalsEval(
                "\"hallo\"",
                """
                    (to-str '("h" "a" "l" "l" "o"))
                """.trimIndent()
            )
        }

        @Test
        fun `toStr list multiple`() {
            equalsEval(
                "\"hallo\"",
                """
                    (to-str '("h" "a" ("l" "l") "o"))
                """.trimIndent()
            )
        }

        @Test
        fun `toStr other`() {
            failsEval("(to-str (lambda (x) x))")
        }
    }

    @Nested
    inner class ToNumTest {
        @Test
        fun `toNum num`() {
            equalsEval("123", "(to-num 123)")
        }

        @Test
        fun `toNum str`() {
            equalsEval("123", "(to-num \"123\")")
        }

        @Test
        fun `toNum str fail`() {
            failsEval("(to-num \"abc\")")
        }

        @Test
        fun `toNum fail`() {
            failsEval("(to-num '(1 2 3))")
        }
    }

    @Nested
    inner class ToSymbolTest {
        @Test
        fun `toSym symbol`() {
            equalsEval("'x", "(to-symbol 'x)")
        }

        @Test
        fun `toSym str`() {
            equalsEval("'x", "(to-symbol \"x\")")
        }

        @Test
        fun `toSym other`() {
            failsEval("(to-symbol '(1 2 3))")
        }
    }
}
