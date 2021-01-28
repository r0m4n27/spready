package spready.lisp.functions.math

import spready.lisp.BaseEval
import kotlin.test.Test

class FractionTest : BaseEval() {

    @Test
    fun numerator() {
        equalsEval("5", "(numerator 5/2)")
    }

    @Test
    fun denominator() {
        equalsEval("2", "(denominator 5/2)")
    }
}
