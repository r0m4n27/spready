package spready.lisp.functions.math

import spready.lisp.Environment
import spready.lisp.sexpr.Flt
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import kotlin.math.E
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.log
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tan

/**
 * Creates a func that transforms a Num to [Flt]
 */
inline fun createDoubleFunc(name: String, crossinline fn: (Double) -> Double): Func {
    return createFuncWithOneArg(name) {
        Flt(fn(it.toFlt().value))
    }
}

val sin = createDoubleFunc("sin", ::sin)
val cos = createDoubleFunc("cos", ::cos)
val tan = createDoubleFunc("tan", ::tan)

val asin = createDoubleFunc("asin", ::asin)
val acos = createDoubleFunc("acos", ::acos)
val atan = createDoubleFunc("atan", ::atan)

val exp = createDoubleFunc("exp", E::pow)

private val logOne = createFuncWithOneArg("temp") {

    Flt(log(it.toFlt().value, E))
}
private val logTwo = createFuncWithTwoArgs("temp") { num, other ->
    Flt(log(num.toFlt().value, other.toFlt().value))
}

/**
 * Computes the logarithm to a base
 *
 * If no base is provided E is used
 */
object LogFunc : Func("log") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkBetweenSize(1, 2)

        return if (args.size == 1) {
            logOne(env, args)
        } else {
            logTwo(env, args)
        }
    }
}

fun floatFunctions(): List<Pair<Symbol, Func>> {
    return listOf(sin, cos, tan, asin, acos, atan, exp, LogFunc).map {
        Pair(Symbol(it.name), it)
    }
}
