package spready.lisp

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
    fun `get Symbol from local`() {
        val locEnv = LocalEnvironment(mutableMapOf(Symbol("123") to Num(3)), env)

        assertEquals(Num(3), locEnv[Symbol("123")])
    }

    @Test
    fun `get Symbol from global`() {
        env += Pair(Symbol("123"), Num(3))
        val locEnv = LocalEnvironment(mutableMapOf(), env)

        assertEquals(Num(3), locEnv[Symbol("123")])
    }

    @Test
    fun `set Symbol local`() {
        val locEnv = LocalEnvironment(mutableMapOf(Symbol("123") to Num(3)), env)
        locEnv[Symbol("123")] = Num(4)

        assertEquals(Num(4), locEnv[Symbol("123")])
    }

    @Test
    fun `set Symbol global`() {
        env += Pair(Symbol("123"), Num(3))
        val locEnv = LocalEnvironment(mutableMapOf(), env)
        locEnv[Symbol("123")] = Num(4)

        assertEquals(Num(4), locEnv[Symbol("123")])
    }

    @Test
    fun `minusAssign Symbol local`() {
        val locEnv = LocalEnvironment(mutableMapOf(Symbol("123") to Num(3)), env)
        locEnv -= Symbol("123")

        assertFailsWith<EvalException> {
            locEnv[Symbol("123")]
        }
    }

    @Test
    fun `minusAssign Symbol global`() {
        env += Pair(Symbol("123"), Num(3))
        val locEnv = LocalEnvironment(mutableMapOf(), env)
        locEnv -= Symbol("123")

        assertFailsWith<EvalException> {
            locEnv[Symbol("123")]
        }
    }
}
