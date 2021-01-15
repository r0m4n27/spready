package spready.lisp.functions

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import spready.lisp.BaseEval
import java.util.stream.Stream
import kotlin.test.Test

class EqualityTest : BaseEval() {

    @Nested
    inner class EqTest {
        @Test
        fun `Eq True`() {
            equalsEval("#t", "(= '(1 2) '(1 2))")
        }

        @Test
        fun `Eq True nil`() {
            equalsEval("#t", "(= '() nil)")
        }

        @Test
        fun `Eq False`() {
            equalsEval("#f", "(= 3 '(1 2))")
        }

        @Test
        fun `Eq function`() {
            equalsEval("#t", "(= + +)")
        }

        @Test
        fun `Eq multiple`() {
            equalsEval("#t", "(= '(1 2) '(1 2) '(1 2))")
        }

        @Test
        fun `Eq lambda`() {
            equalsEval("#f", "(= (lambda (x) x) (lambda (x) x))")
        }
    }

    @Nested
    inner class NeqTest {
        @Test
        fun `Neq True`() {
            equalsEval("#f", "(!= '(1 2) '(1 2))")
        }

        @Test
        fun `Neq True nil`() {
            equalsEval("#f", "(!= '() nil)")
        }

        @Test
        fun `Neq False`() {
            equalsEval("#t", "(!= 3 '(1 2))")
        }

        @Test
        fun `Neq lambda`() {
            equalsEval("#t", "(!= (lambda (x) x) (lambda (x) x))")
        }

        @Test
        fun `Neq Multiple`() {
            equalsEval("#t", "(!= 3 '(1 2) 4)")
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class CompTest {
        @Test
        fun `compare other than num and str`() {
            failsEval(
                """
                (< 'x 'y 'z)
                """.trimIndent()
            )
        }

        private fun compareMultipleProvider() = Stream.of(
            Pair("#t", "(< 1 2 3)"),
            Pair("#t", "(<= 1 2 3)"),
            Pair("#t", "(<= 1 2 2 3)"),
            Pair("#f", "(< 1 2 2 3)"),
            Pair("#f", "(< 1 1 2 3)"),
            Pair("#t", """(< "a" "b" "c")"""),
            Pair("#t", "(> 3 2 1)"),
            Pair("#t", """(> "z" "b" "a")"""),
        )

        @ParameterizedTest
        @MethodSource("compareMultipleProvider")
        fun `compare multiple`(data: Pair<String, String>) {
            equalsEval(data.first, data.second)
        }
    }
}
