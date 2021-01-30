package spready.lisp

import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.Variable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EnvironmentTest {

    @Test
    fun `env get success`() {
        val input: MutableMap<Variable, SExpr> =
            mutableMapOf(Symbol("123") to Integer(123))

        val env = Environment(input)

        assertEquals(env[Symbol("123")], Integer(123))
    }

    @Test
    fun `env get fail`() {
        assertFailsWith<EvalException> {
            Environment()[Symbol("123")]
        }
    }

    @Test
    fun `minus assign success`() {
        val env = Environment()
        env[Symbol("123")] = Integer(123)

        assertDoesNotThrow {
            env -= Symbol("123")
        }

        assertThrows<EvalException> {
            env[Symbol("123")]
        }
    }

    @Test
    fun `minus assign fail`() {
        assertThrows<EvalException> {
            Environment() -= Symbol("123")
        }
    }

    @Test
    fun `registerSExpr normal`() {
        val env = Environment()

        val expected = Integer(123)
        val sym = Symbol("123")
        val expr = Symbol("456")
        env[expr] = expected

        assertEquals(expected, env.evalAndRegister(sym, expr))
        assertEquals(expected, env[sym])
    }
}
