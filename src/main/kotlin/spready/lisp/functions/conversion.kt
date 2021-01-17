package spready.lisp.functions

import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.sexpr.Flt
import spready.lisp.sexpr.Fraction
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.ListElem
import spready.lisp.sexpr.ListElem.Companion.toListElem
import spready.lisp.sexpr.Num
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Str
import spready.lisp.sexpr.Symbol

private inline fun createConv(
    name: String,
    crossinline evalBlock: (SExpr) -> SExpr
): Func {
    return object : Func(name) {
        override fun invoke(env: Environment, args: List<SExpr>): SExpr {
            args.checkSize(1)

            return evalBlock(env.eval(args[0]))
        }
    }
}

val toListConv = createConv("to-list") { sExpr ->
    when (sExpr) {
        is ListElem -> sExpr
        is Str -> sExpr.value.map { Str(it.toString()) }.toListElem()
        else -> throw EvalException("Can't convert $sExpr to list!")
    }
}

object ToStringFunc : Func("to-str") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(1)

        return convertSingleValue(env.eval(args[0]))
    }

    private fun convertSingleValue(expr: SExpr): Str {
        return when (expr) {
            is Str -> expr
            is Symbol -> Str(expr.value)
            is Num -> Str(expr.toString())
            is ListElem -> {
                Str(
                    buildString {
                        expr.forEach {
                            append(convertSingleValue(it).value)
                        }
                    }
                )
            }
            else -> throw EvalException("Can't convert $expr to str!")
        }
    }
}

val toIntConv = createConv("to-int") {
    when (it) {
        is Num -> it.toInteger()
        is Str -> {
            try {
                Integer(it.value.toInt())
            } catch (_: NumberFormatException) {
                throw EvalException("Can't convert $it to int!")
            }
        }
        else -> throw EvalException("Can't convert $it to int!")
    }
}

val toFltConv = createConv("to-float") {
    when (it) {
        is Num -> it.toFlt()
        is Str -> {
            try {
                Flt(it.value.toDouble())
            } catch (_: NumberFormatException) {
                throw EvalException("Can't convert $it to float!")
            }
        }
        else -> throw EvalException("Can't convert $it to float!")
    }
}

val toFractionConv = createConv("to-fraction") {
    when (it) {
        is Num -> it.toFraction()
        is Str -> {
            try {
                val fractionRegex = Regex("""(-?\d+)/(\d+)""")
                val match = fractionRegex.matchEntire(it.value)
                    ?: throw EvalException("Can't convert $it to fraction!")

                val (num, den) = match.destructured

                Fraction.create(num.toInt(), den.toInt())
            } catch (_: NumberFormatException) {
                throw EvalException("Can't convert $it to fraction!")
            }
        }
        else -> throw EvalException("Can't convert $it to fraction!")
    }
}

val toBoolConv = createConv("to-bool") {
    it.toBool()
}

val toSymbolConv = createConv("to-symbol") {
    when (it) {
        is Symbol -> it
        is Str -> Symbol(it.value)
        else -> throw EvalException("Can't convert $it to symbol!")
    }
}

fun conversionFunctions(): List<Pair<Symbol, Func>> {

    return listOf(
        toListConv,
        toIntConv,
        toFltConv,
        toFractionConv,
        toBoolConv,
        toSymbolConv,
        ToStringFunc
    ).map {
        Pair(Symbol(it.name), it)
    }
}
