package spready.lisp

import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EnvironmentTest {

    @Test
    fun `env get success`() {
        val input: MutableMap<Symbol, SExpr> = mutableMapOf(Symbol("123") to Num(123))

        val env = Environment(input)

        assertEquals(env[Symbol("123")], Num(123))
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
        env[Symbol("123")] = Num(123)

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

        val expected = Num(123)
        val sym = Symbol("123")
        val expr = Symbol("456")
        env[expr] = expected

        assertEquals(expected, env.evalAndRegister(sym, expr))
        assertEquals(expected, env[sym])
    }
}
