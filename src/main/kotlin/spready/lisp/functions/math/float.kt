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

inline fun createTrigFunc(name: String, crossinline fn: (Double) -> Double): Func {
    return createFuncWithOneArg(name) {
        Flt(fn(it.toFlt().value))
    }
}

val sin = createTrigFunc("sin", ::sin)
val cos = createTrigFunc("cos", ::cos)
val tan = createTrigFunc("tan", ::tan)

val asin = createTrigFunc("asin", ::asin)
val acos = createTrigFunc("acos", ::acos)
val atan = createTrigFunc("atan", ::atan)

private val exp = createFuncWithOneArg("exp") {
    Flt(E.pow(it.toFlt().value))
}

private val logOne = createFuncWithOneArg("temp") {

    Flt(log(it.toFlt().value, E))
}
private val logTwo = createFuncWithTwoArgs("temp") { num, other ->
    Flt(log(num.toFlt().value, other.toFlt().value))
}

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
