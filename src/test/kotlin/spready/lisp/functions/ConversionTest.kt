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
            failsEval("(to-list 3)", "Can't convert 3 to list!")
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
            failsEval(
                "(to-str (lambda (x) x))",
                "Can't convert #<(lambda (x))> to str!"
            )
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
            failsEval("(to-int \"abc\")", "Can't convert \"abc\" to int!")
        }

        @Test
        fun `toInt fail`() {
            failsEval("(to-int '(1 2 3))", "Can't convert (1 2 3) to int!")
        }

        @Test
        fun `toFrac fail zero`() {
            failsEval("""(to-fraction "9/0")""", "Denominator can't be zero!")
        }

        @Test
        fun `toFrac fail negative`() {
            failsEval("""(to-fraction "9/-1")""", "Can't convert \"9/-1\" to fraction!")
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
            failsEval("(to-symbol '(1 2 3))", "Can't convert (1 2 3) to symbol!")
        }
    }

    @Nested
    inner class ToCellTest {
        @Test
        fun `toCell str`() {
            equalsEval("'#12.12", "(to-cell \"12.12\")")
        }

        @Test
        fun `toCell str fail`() {
            failsEval("(to-cell \"123\")", "Can't convert \"123\" to cell")
        }

        @Test
        fun `toCell num fail`() {
            failsEval("(to-cell 123)", "Can't convert 123 to cell")
        }
    }
}
