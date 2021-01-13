package spready.lisp.functions

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import spready.lisp.BaseEval
import spready.lisp.EvalException
import spready.lisp.sexpr.Bool
import java.util.stream.Stream
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IdentityTest : BaseEval() {

    @Test
    fun `identity to many args`() {
        val input = "(nil? 1 2 3)"

        assertThrows<EvalException> {
            evalString(input)
        }
    }

    @Test
    fun `identity zero args`() {
        val input = "(nil?)"

        assertThrows<EvalException> {
            evalString(input)
        }
    }

    private fun identityNormalProvider() =
        Stream.of(
            "(nil? nil)",
            "(str? \"123\")",
            "(num? 1)",
            "(bool? #t)",
            "(symbol? 'x)",
            "(func? +)"
        )

    @ParameterizedTest
    @MethodSource("identityNormalProvider")
    fun `identity normal`(data: String) {

        assertEquals(Bool(true), evalString(data))
    }

    @Test
    fun `identity list`() {
        val input = "(list? '(1 2 3))"

        assertEquals(Bool(true), evalString(input))
    }

    @Test
    fun `identity cons not list`() {
        equalsEval("#f", "(list? (cons 1 2))")
    }

    @Test
    fun `identity empty list`() {
        val input = "(list? '())"

        assertEquals(Bool(true), evalString(input))
    }

    @Test
    fun `identity pair`() {
        val input = "(pair? (cons 1 2))"

        assertEquals(Bool(true), evalString(input))
    }

    @Test
    fun `identity not pair`() {
        val input = "(pair? (cons 1 nil))"

        assertEquals(Bool(false), evalString(input))
    }
}
