package spready.lisp.functions

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import spready.lisp.BaseEval
import java.util.stream.Stream
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IdentityTest : BaseEval() {

    @Test
    fun `identity to many args`() {
        failsEval("(nil? 1 2 3)", "Can only have 1 arguments not 3")
    }

    @Test
    fun `identity zero args`() {
        failsEval("(nil?)", "Can only have 1 arguments not 0")
    }

    private fun identityNormalProvider() =
        Stream.of(
            "(nil? nil)",
            "(str? \"123\")",
            "(num? 1)",
            "(bool? #t)",
            "(symbol? 'x)",
            "(func? +)",
            "(int? 1)",
            "(fraction? 9/4)",
            "(float? 3.123)"
        )

    @ParameterizedTest
    @MethodSource("identityNormalProvider")
    fun `identity normal`(data: String) {
        equalsEval("#t", data)
    }

    @Test
    fun `identity list`() {
        equalsEval("#t", "(list? '(1 2 3))")
    }

    @Test
    fun `identity cons not list`() {
        equalsEval("#f", "(list? (cons 1 2))")
    }

    @Test
    fun `identity empty list`() {
        equalsEval("#t", "(list? '())")
    }

    @Test
    fun `identity pair`() {
        equalsEval("#t", "(pair? (cons 1 2))")
    }

    @Test
    fun `identity not pair`() {
        equalsEval("#f", "(pair? (cons 1 nil))")
    }
}
