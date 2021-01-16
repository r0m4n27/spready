package spready.lisp

import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.Symbol
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LocalEnvTest {
    private var env = Environment()

    @BeforeTest
    fun resetEnv() {
        env = Environment()
    }

    @Test
    fun `add to local and get Symbol from local`() {
        val locEnv = LocalEnvironment(env)
        locEnv.addLocal(Symbol("123"), Integer(3))

        assertEquals(Integer(3), locEnv[Symbol("123")])
    }

    @Test
    fun `get Symbol from global`() {
        env += Pair(Symbol("123"), Integer(3))
        val locEnv = LocalEnvironment(env)

        assertEquals(Integer(3), locEnv[Symbol("123")])
    }

    @Test
    fun `set Symbol local`() {
        val locEnv = LocalEnvironment(env)
        locEnv.addLocal(Symbol("123"), Integer(3))
        locEnv[Symbol("123")] = Integer(4)

        assertEquals(Integer(4), locEnv[Symbol("123")])
    }

    @Test
    fun `set Symbol global`() {
        env += Pair(Symbol("123"), Integer(3))
        val locEnv = LocalEnvironment(env)
        locEnv[Symbol("123")] = Integer(4)

        assertEquals(Integer(4), locEnv[Symbol("123")])
    }

    @Test
    fun `minusAssign Symbol local`() {
        val locEnv = LocalEnvironment(env)
        locEnv.addLocal(Symbol("123"), Integer(3))
        locEnv -= Symbol("123")

        assertFailsWith<EvalException> {
            locEnv[Symbol("123")]
        }
    }

    @Test
    fun `minusAssign Symbol global`() {
        env += Pair(Symbol("123"), Integer(3))
        val locEnv = LocalEnvironment(env)
        locEnv -= Symbol("123")

        assertFailsWith<EvalException> {
            locEnv[Symbol("123")]
        }
    }
}
