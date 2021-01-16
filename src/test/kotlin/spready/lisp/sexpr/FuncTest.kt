package spready.lisp.sexpr

import org.junit.jupiter.api.assertDoesNotThrow
import spready.lisp.Environment
import spready.lisp.EvalException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class FuncTest {
    private var env = Environment()

    @BeforeTest
    fun resetEnv() {
        env = Environment()
    }

    @Test
    fun `checkSize normal`() {

        object : Func("test") {
            override fun invoke(env: Environment, args: List<SExpr>): SExpr {
                assertDoesNotThrow {
                    args.checkSize(2)
                }

                return Nil
            }
        }(env, listOf(Integer(1), Integer(2)))
    }

    @Test
    fun `checkSize fail`() {
        object : Func("test") {
            override fun invoke(env: Environment, args: List<SExpr>): SExpr {
                assertFailsWith<EvalException> {
                    args.checkSize(5)
                }

                return Nil
            }
        }(env, listOf(Integer(1), Integer(2)))
    }

    @Test
    fun `checkMinSize fail`() {
        object : Func("test") {
            override fun invoke(env: Environment, args: List<SExpr>): SExpr {
                assertFailsWith<EvalException> {
                    args.checkMinSize(2)
                }

                return Nil
            }
        }(env, listOf(Integer(1)))
    }

    @Test
    fun `checkMinSize normal`() {
        object : Func("test") {
            override fun invoke(env: Environment, args: List<SExpr>): SExpr {
                assertDoesNotThrow {
                    args.checkMinSize(1)
                }

                return Nil
            }
        }(env, listOf(Integer(1), Integer(2), Symbol("x")))
    }

    @Test
    fun `checkBetweenSize normal`() {
        object : Func("test") {
            override fun invoke(env: Environment, args: List<SExpr>): SExpr {
                assertDoesNotThrow {
                    args.checkBetweenSize(1, 3)
                }

                return Nil
            }
        }(env, listOf(Integer(1), Integer(2), Symbol("x")))
    }

    @Test
    fun `checkBetweenSize fail`() {
        object : Func("test") {
            override fun invoke(env: Environment, args: List<SExpr>): SExpr {
                assertFailsWith<EvalException> {
                    args.checkBetweenSize(4, 10)
                }

                return Nil
            }
        }(env, listOf(Integer(1)))
    }
}
