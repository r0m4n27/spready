package spready.lisp.functions.forms

import org.junit.jupiter.api.Nested
import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.functions.math.plus
import spready.lisp.parse
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import spready.lisp.tokenize
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ControlFlowTest {
    private var env = Environment()

    @BeforeTest
    fun resetEnv() {
        env = Environment()
    }

    fun evalString(string: String): SExpr {
        return env.eval(parse(tokenize(string)).first())
    }

    @Nested
    inner class IfTest {
        @Test
        fun `If normal`() {
            val input = "(if (+ 1 2) 4 5)"
            env[Symbol("if")] = IfExpr
            env[Symbol("+")] = plus

            assertEquals(Integer(4), env.eval(parse(tokenize(input)).first()))
        }

        @Test
        fun `If true`() {
            val input = "(if #t 4 5)"
            env[Symbol("if")] = IfExpr

            assertEquals(Integer(4), env.eval(parse(tokenize(input)).first()))
        }

        @Test
        fun `If false`() {
            val input = "(if #f 4 5)"
            env[Symbol("if")] = IfExpr

            assertEquals(Integer(5), env.eval(parse(tokenize(input)).first()))
        }

        @Test
        fun `If nil`() {
            val input = Cons(IfExpr, Cons(Nil, Cons(Integer(4), Cons(Integer(5), Nil))))
            env[Symbol("if")] = IfExpr

            assertEquals(Integer(5), env.eval(input))
        }
    }

    @Nested
    inner class CondTest {
        @Test
        fun `Cond empty`() {
            val input = Cons(Cond, Nil)
            assertEquals(Nil, input.eval(env))
        }

        @Test
        fun `Cond only else`() {
            val input = "(cond (else 2))"
            env[Symbol("cond")] = Cond
            assertEquals(Integer(2), evalString(input))
        }

        @Test
        fun `Cond after else`() {
            val input = "(cond (#f 1) (else 2) (#f 3))"
            env[Symbol("cond")] = Cond
            assertEquals(Integer(2), evalString(input))
        }

        @Test
        fun `Cond multiple`() {
            val input = "(cond (#f 1) (#t 2) (#f 3))"
            env[Symbol("cond")] = Cond
            assertEquals(Integer(2), evalString(input))
        }

        @Test
        fun `cond only test`() {
            val input = "(cond (2))"
            env[Symbol("cond")] = Cond
            assertEquals(Integer(2), evalString(input))
        }

        @Test
        fun `cond lambda`() {
            val input = "(cond (2 => (lambda (x) (+ x 1))))"
            env[Symbol("cond")] = Cond
            env[Symbol("lambda")] = Lambda
            env[Symbol("+")] = plus
            assertEquals(Integer(3), evalString(input))
        }

        @Test
        fun `cond lambda fail`() {
            val input = "(cond (2 => x))"
            env[Symbol("cond")] = Cond
            env[Symbol("lambda")] = Lambda
            env[Symbol("+")] = plus
            assertFailsWith<EvalException> { evalString(input) }
        }

        @Test
        fun `cond none`() {
            val input = "(cond (#f 1) (#f 2) (#f 3))"
            env[Symbol("cond")] = Cond
            assertEquals(Nil, evalString(input))
        }
    }

    @Nested
    inner class CaseTest {
        @Test
        fun `case empty`() {
            val input = "(case)"
            env[Symbol("case")] = Case
            assertFailsWith<EvalException> {
                evalString(input)
            }
        }

        @Test
        fun `case without branches`() {
            val input = "(case 2)"
            env[Symbol("case")] = Case
            assertFailsWith<EvalException> {
                evalString(input)
            }
        }

        @Test
        fun `case else`() {
            val input = "(case 3 (else 4))"
            env[Symbol("case")] = Case
            assertEquals(Integer(4), evalString(input))
        }

        @Test
        fun `case after else`() {
            val input = "(case 3 ((4 5 6) 7) (else 4) ((7 8 9) 10))"
            env[Symbol("case")] = Case
            assertEquals(Integer(4), evalString(input))
        }

        @Test
        fun `case normal`() {
            val input = "(case 3 ((1 2) 3) ((3 4 5) 4))"
            env[Symbol("case")] = Case
            assertEquals(Integer(4), evalString(input))
        }
    }

    @Nested
    inner class RunTest {
        @Test
        fun `Run Normal`() {
            env[Symbol("run")] = RunExpr

            val input = "(run 1 2 3)"

            assertEquals(Integer(3), env.eval(parse(tokenize(input)).first()))
        }

        @Test
        fun `Run Nil`() {
            env[Symbol("run")] = RunExpr

            val input = Cons(RunExpr, Cons(Nil, Nil))

            assertEquals(Nil, env.eval(input))
        }
    }
}
