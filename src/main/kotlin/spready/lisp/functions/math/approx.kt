package spready.lisp.functions.math

import spready.lisp.sexpr.Flt
import spready.lisp.sexpr.Fraction
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.Symbol
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.truncate

inline fun createApproxFunc(name: String, crossinline fn: (Double) -> Double): Func {
    return createFuncWithOneArg(name) {
        when (it) {
            is Integer -> it
            is Fraction, is Flt -> Integer(fn(it.toFlt().value).toInt())
        }
    }
}

val ceilFunc = createApproxFunc("ceil", ::ceil)
val floorFunc = createApproxFunc("floor", ::floor)
val truncateFunc = createApproxFunc("truncate", ::truncate)
val roundFunc = createApproxFunc("round", ::round)

fun approxFunctions(): List<Pair<Symbol, Func>> {
    return listOf(ceilFunc, floorFunc, truncateFunc, roundFunc).map {
        Pair(Symbol(it.name), it)
    }
}
