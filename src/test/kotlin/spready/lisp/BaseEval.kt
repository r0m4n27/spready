package spready.lisp

import spready.lisp.sexpr.SExpr
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

open class BaseEval {
    var env = Environment()

    @BeforeTest
    fun resetEnv() {
        env = Environment.defaultEnv()
    }

    fun evalString(string: String): SExpr {
        return env.eval(parse(tokenize(string)).first())
    }

    fun equalsEval(expected: String, actual: String) {
        assertEquals(evalString(expected), evalString(actual))
    }

    fun failsEval(expr: String) {
        assertFailsWith<EvalException> {
            evalString(expr)
        }
    }

    fun failsEval(expr: String, message: String) {
        assertFailsWith<EvalException>(message) {
            evalString(expr)
        }
    }
}
