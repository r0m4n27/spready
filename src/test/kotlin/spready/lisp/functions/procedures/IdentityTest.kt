package spready.lisp.functions.procedures

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.evalString
import spready.lisp.functions.forms.Quote
import spready.lisp.sexpr.Bool
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.Num
import java.util.stream.Stream
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IdentityTest {
    private var env = Environment()

    @BeforeTest
    fun resetEnv() {
        env = Environment.defaultEnv()
    }

    @Test
    fun `identity to many args`() {
        val input = "(nil? 1 2 3)"

        assertThrows<EvalException> {
            evalString(input, env)
        }
    }

    @Test
    fun `identity zero args`() {
        val input = "(nil?)"

        assertThrows<EvalException> {
            evalString(input, env)
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

        assertEquals(Bool(true), evalString(data, env))
    }

    @Test
    fun `identity list`() {
        val input = "(list? '(1 2 3))"

        assertEquals(Bool(true), evalString(input, env))
    }

    @Test
    fun `identity pair`() {
        val input =
            Cons(IsPair, Cons(Cons(Quote, Cons(Cons(Num(1), Num(2)), Nil)), Nil))

        assertEquals(Bool(true), env.eval(input))
    }
}
