package spready.lisp.functions.math

import spready.lisp.BaseEval
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.Symbol
import kotlin.test.Test

class UtilTest : BaseEval() {

    @Test
    fun reduce() {
        val func = createReduceFunc("test") { acc, num ->
            acc + num
        }

        env[Symbol(func.name)] = func

        equalsEval("6", "(test 1 2 3)")
    }

    @Test
    fun funcOneArg() {
        val func = createFuncWithOneArg("test") { it + Integer(1) }
        env[Symbol(func.name)] = func

        equalsEval("4", "(test 3)")
    }

    @Test
    fun funcTwoArgs() {
        val func = createFuncWithTwoArgs("test") { num, other ->
            Integer(1) + num + other
        }
        env[Symbol(func.name)] = func
        equalsEval("8", "(test 3 4)")
    }
}
