package spready.lisp.functions.math

import spready.lisp.sexpr.Flt
import spready.lisp.sexpr.Fraction
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.Num
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.cast
import kotlin.math.truncate

val plus = createReduceFunc("+", Num::plus)
val minus = createReduceFunc("-", Num::minus)
val times = createReduceFunc("*", Num::times)
val div = createReduceFunc("/", Num::div)

val negate = createFuncWithOneArg("negate", Num::unaryMinus)
val invert = createFuncWithOneArg("invert", Num::invert)
val abs = createFuncWithOneArg("abs", Num::abs)

val pow = createFuncWithTwoArgs("pow", Num::pow)
val modulo = createFuncWithTwoArgs("modulo", Num::rem)

val gcd = createFuncWithTwoArgs("gcd") { num, other ->
    Integer(Num.gcd(num.cast<Integer>().value, other.cast<Integer>().value))
}

val lcm = createFuncWithTwoArgs("lcm") { num, other ->
    Integer(Num.lcm(num.cast<Integer>().value, other.cast<Integer>().value))
}

val quotient = createFuncWithTwoArgs("quotient") { num, other ->
    when (val divided = num / other) {
        is Integer -> divided
        is Fraction, is Flt -> Integer(truncate(divided.toFlt().value).toInt())
    }
}

fun arithmeticFunctions(): List<Pair<Symbol, Func>> {
    return listOf(
        plus,
        minus,
        times,
        div,
        negate,
        invert,
        abs,
        gcd,
        lcm,
        quotient,
        pow,
        modulo
    ).map {
        Pair(Symbol(it.name), it)
    }
}
