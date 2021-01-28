package spready.lisp.functions

import org.junit.jupiter.api.Nested
import spready.lisp.BaseEval
import kotlin.test.Test

class StringTest : BaseEval() {

    @Test
    fun `str-length normal`() {
        equalsEval("5", "(string-length \"12345\")")
    }

    @Nested
    inner class StrGetTest {
        @Test
        fun `strGet pos neg`() {
            failsEval(
                """
                (string-get -1 "hallo")
                """.trimIndent(),
                "Position cant be negative!"
            )
        }

        @Test
        fun `strGet pos to Big`() {
            failsEval(
                """
                (string-get 5 "hallo")
                """.trimIndent(),
                "String is only 5 big!"
            )
        }

        @Test
        fun `strGet normal`() {
            equalsEval(
                "\"a\"",
                """
                (string-get 1 "hallo")
                """.trimIndent()
            )
        }
    }

    @Nested
    inner class ReplaceCharTest {
        @Test
        fun `replaceStr pos neg`() {
            failsEval(
                """
                (replace-char -1 "t" "hallo")
                """.trimIndent(),
                "Position cant be negative!"
            )
        }

        @Test
        fun `replaceStr pos to big`() {
            failsEval(
                """
                (replace-char 20 "t" "hallo")
                """.trimIndent(),
                "String is only 5 big!"
            )
        }

        @Test
        fun `replaceStr new Str to long`() {
            failsEval(
                """
                (replace-char 2 "test" "hallo")
                """.trimIndent(),
                "ReplacementString can only be 1 long!"
            )
        }

        @Test
        fun `replaceStr normal`() {
            equalsEval(
                "\"hollo\"",
                """
                (replace-char 1 "o" "hallo")
                """.trimIndent()
            )
        }
    }

    @Nested
    inner class SubstringTest {
        @Test
        fun `substring pos neg`() {
            failsEval(
                """
                (substring -1 "hallo")
                """.trimIndent(),
                "Position cant be negative!"
            )
        }

        @Test
        fun `substring pos to big`() {
            failsEval(
                """
                (substring 20 "hallo")
                """.trimIndent(),
                "String is only 5 big!"
            )
        }

        @Test
        fun `replaceStr normal`() {
            equalsEval(
                "\"allo\"",
                """
                (substring 1 "hallo")
                """.trimIndent()
            )
        }

        @Test
        fun `replaceStr zero`() {
            equalsEval(
                "\"hallo\"",
                """
                (substring 0 "hallo")
                """.trimIndent()
            )
        }
    }

    @Nested
    inner class AppendTest {
        @Test
        fun `append single`() {
            equalsEval(
                "\"hallo\"",
                """
                (string-append "hallo")
                """.trimIndent()
            )
        }

        @Test
        fun `append normal`() {
            equalsEval(
                "\"halloTest\"",
                """
                (string-append "hallo" "Test")
                """.trimIndent()
            )
        }
    }

    @Nested
    inner class FillTest {
        @Test
        fun `fill replace multiple chars`() {
            failsEval(
                """
                (string-fill "test" "hallo")
                """.trimIndent(),
                "New Char can only be 1 long!"
            )
        }

        @Test
        fun `fill normal`() {
            equalsEval(
                "\"11111\"",
                """
                  (string-fill "1" "hallo")
                """.trimIndent()
            )
        }
    }
}
