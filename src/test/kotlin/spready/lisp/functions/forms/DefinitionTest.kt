package spready.lisp.functions.forms

import org.junit.jupiter.api.Nested
import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.Num
import spready.lisp.sexpr.Symbol
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DefinitionTest {

    private var env = Environment()

    @BeforeTest
    fun resetEnv() {
        env = Environment()
    }

    @Test
    fun `createLambda overrideVal`() {
        val lambda = createLambda("test", listOf(Symbol("x")), listOf(Symbol("x")))

        env[Symbol("x")] = Num(3)

        assertEquals(Num(5), lambda(env, listOf(Num(5))))
    }

    @Test
    fun `createLambda empty args`() {
        val lambda = createLambda("test", listOf(), listOf(Symbol("x")))

        env[Symbol("x")] = Num(3)

        assertEquals(Num(3), lambda(env, listOf(Num(3))))
    }

    @Nested
    inner class LambdaTest {
        @Test
        fun `Lambda normal`() {
            val lambdaFunc =
                Lambda(
                    env,
                    listOf(Cons(Symbol("x"), Nil), Symbol("x"))
                ) as Func

            assertEquals(Num(3), lambdaFunc(env, listOf(Num(3))))
        }

        @Test
        fun `Lambda Empty args`() {
            val lambdaFunc =
                Lambda(env, listOf(Nil, Num(3))) as Func

            assertEquals(Num(3), lambdaFunc(env, listOf()))
        }

        @Test
        fun `Lambda fail head with other than Symbol`() {
            assertFailsWith<EvalException> {
                Lambda(
                    env,
                    listOf(Cons(Symbol("x"), Cons(Num(4), Nil)), Symbol("x"))
                )
            }
        }
    }

    @Nested
    inner class ValEvalTest {
        @Test
        fun `ValEval normal`() {
            env[Symbol("y")] = Symbol("x")

            assertEquals(Num(3), ValEval(env, listOf(Symbol("y"), Num(3))))
            assertEquals(Num(3), env[Symbol("x")])
        }

        @Test
        fun `ValEval fail`() {
            assertFailsWith<EvalException> {
                ValEval(env, listOf(Symbol("y"), Num(3)))
            }
        }
    }

    @Nested
    inner class ValTest {
        @Test
        fun `Val normal`() {
            assertEquals(Num(3), Val(env, listOf(Symbol("y"), Num(3))))
            assertEquals(Num(3), env[Symbol("y")])
        }

        @Test
        fun `Val fail`() {
            // TODO: Check message
            assertFailsWith<EvalException> {
                Val(env, listOf(Num(3), Num(3)))
            }
        }
    }

    @Nested
    inner class FunTest {
        @Test
        fun `FuncExpr normal`() {
            val funcSymbol = FunExpr(
                env,
                listOf(
                    Symbol("test"),
                    Cons(Symbol("x"), Nil),
                    Symbol("x")
                )
            )

            assertEquals(Symbol("test"), funcSymbol)
            assertEquals(Num(3), env.eval(Cons(Symbol("test"), Cons(Num(3), Nil))))
        }

        @Test
        fun `FuncExpr empty args`() {
            val funcSymbol = FunExpr(
                env,
                listOf(Symbol("test"), Nil, Num(3))
            )

            assertEquals(Symbol("test"), funcSymbol)
            assertEquals(Num(3), env.eval(Cons(Symbol("test"), Cons(Nil, Nil))))
        }

        @Test
        fun `FuncExpr fail not Symbol`() {
            assertFailsWith<EvalException> {
                FunExpr(
                    env,
                    listOf(Num(3), Nil, Num(3))
                )
            }
        }
    }
}
