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
        fun `toInt num`() {
            equalsEval("123", "(to-int 123)")
        }

        @Test
        fun `toInt str`() {
            equalsEval("123", "(to-int \"123\")")
        }

        @Test
        fun `toInt str fail`() {
            failsEval("(to-num \"abc\")")
        }

        @Test
        fun `toInt fail`() {
            failsEval("(to-num '(1 2 3))")
        }

        @Test
        fun `toFrac fail zero`() {
            failsEval("""(to-fraction "9/0")""")
        }

        @Test
        fun `toFrac fail negative`() {
            failsEval("""(to-fraction "9/-1")""")
        }

        @Test
        fun `toFrac normal`() {
            equalsEval("2/4", """(to-fraction "16/32")""")
        }

        @Test
        fun `toFloat normal`() {
            equalsEval("-123.1", """(to-float "-123.1")""")
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
