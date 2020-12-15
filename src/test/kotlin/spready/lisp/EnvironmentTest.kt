package spready.lisp

import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EnvironmentTest {

    @Test
    fun `env copy empty`() {
        val input: MutableMap<Symbol, SExpr> = mutableMapOf(Symbol("123") to Num(123))

        val env = Environment(input)

        val copiedEnv = env.copy()

        assertEquals(copiedEnv[Symbol("123")], Num(123))
    }

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
}
