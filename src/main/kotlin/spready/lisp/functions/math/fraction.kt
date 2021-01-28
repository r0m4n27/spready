package spready.lisp.functions.math

import spready.lisp.sexpr.Fraction
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.cast

val numerator = createFuncWithOneArg("numerator") {
    Integer(it.cast<Fraction>().numerator)
}

val denominator = createFuncWithOneArg("denominator") {
    Integer(it.cast<Fraction>().denominator)
}

fun fractionFunctions(): List<Pair<Symbol, Func>> {
    return listOf(numerator, denominator).map {
        Pair(Symbol(it.name), it)
    }
}
