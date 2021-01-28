package spready.lisp.functions.math

import spready.lisp.sexpr.Bool
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.Symbol

val zeroPredicate = createFuncWithOneArg("zero?") {
    Bool(it == Integer(0))
}

val negativePredicate = createFuncWithOneArg("neg?") {
    Bool(it < Integer(0))
}

val positivePredicate = createFuncWithOneArg("pos?") {
    Bool(it > Integer(0))
}

val oddPredicate = createFuncWithOneArg("odd?") {
    Bool(it % Integer(2) == Integer(1))
}

val evenPredicate = createFuncWithOneArg("even?") {
    Bool(it % Integer(2) == Integer(0))
}

fun predicateFunctions(): List<Pair<Symbol, Func>> {
    return listOf(
        zeroPredicate,
        negativePredicate,
        positivePredicate,
        oddPredicate,
        evenPredicate
    ).map {
        Pair(Symbol(it.name), it)
    }
}
