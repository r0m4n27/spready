package spready.lisp.functions.forms

import org.junit.jupiter.api.Nested
import spready.lisp.BaseEval
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.Symbol
import kotlin.test.Test
import kotlin.test.assertEquals

class DefinitionTest : BaseEval() {

    @Nested
    inner class CreateLambdaTest {
        @Test
        fun `createLambda overrideVal`() {
            val lambda = createLambda("test", listOf(Symbol("x")), listOf(Symbol("x")))

            env[Symbol("x")] = Integer(3)

            assertEquals(Integer(5), lambda(env, listOf(Integer(5))))
        }

        @Test
        fun `createLambda empty args`() {
            val lambda = createLambda("test", listOf(), listOf(Symbol("x")))

            env[Symbol("x")] = Integer(3)

            assertEquals(Integer(3), lambda(env, listOf(Integer(6))))
        }
    }

    @Nested
    inner class LambdaTest {
        @Test
        fun `Lambda normal`() {
            equalsEval("3", "((lambda (x) x) 3)")
        }

        @Test
        fun `Lambda Empty args`() {
            equalsEval("3", "((lambda () 3))")
        }

        @Test
        fun `Lambda fail head with other than Symbol`() {
            failsEval("(lambda (x 123) x)")
        }
    }

    @Nested
    inner class ValEvalTest {
        @Test
        fun `ValEval normal`() {
            env[Symbol("y")] = Symbol("x")
            equalsEval("3", "(val-eval y 3)")
            equalsEval("3", "x")
        }

        @Test
        fun `ValEval fail`() {
            failsEval("(val-eval y 3)")
        }
    }

    @Nested
    inner class ValTest {
        @Test
        fun `Val normal`() {
            equalsEval("3", "(val y 3)")
            equalsEval("3", "y")
        }

        @Test
        fun `Val fail`() {
            // TODO: Check message
            failsEval("(val 3 3)")
        }
    }

    @Nested
    inner class FunTest {
        @Test
        fun `FuncExpr normal`() {
            equalsEval("'test", "(fun test (x) x)")
            equalsEval("3", "(test 3)")
        }

        @Test
        fun `FuncExpr empty args`() {
            equalsEval("'test", "(fun test () 3)")
            equalsEval("3", "(test 3)")
        }

        @Test
        fun `FuncExpr fail not Symbol`() {
            failsEval("(fun test (3 4) 3)")
        }
    }
}
