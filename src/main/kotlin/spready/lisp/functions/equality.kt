package spready.lisp.functions

import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.sexpr.Bool
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Num
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Str
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.cast

inline fun createCompFun(
    name: String,
    crossinline compFun: (Int) -> Boolean
): Func {

    return object : Func(name) {
        override fun invoke(env: Environment, args: List<SExpr>): SExpr {

            args.checkMinSize(2)
            val evaluated = args.map { it.eval(env) }

            val compared = when (evaluated.first()) {
                is Num -> {
                    evaluated.map {
                        it.cast<Num>()
                    }.zipWithNext().map {
                        val first = it.first.value
                        val second = it.second.value
                        first.compareTo(second)
                    }
                }
                is Str -> {
                    evaluated.map {
                        it.cast<Str>()
                    }.zipWithNext().map {
                        val first = it.first.value
                        val second = it.second.value
                        first.compareTo(second)
                    }
                }
                else -> throw EvalException("${evaluated.first()} can't be compared!")
            }

            return Bool(compared.all(compFun))
        }
    }
}

object EqExpr : Func("=") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val argsMut = args.toMutableList().checkMinSize(2)
        val first = argsMut.removeFirst().eval(env)
        val evaluated = argsMut.map { it.eval(env) }

        return Bool(evaluated.all { it == first })
    }
}

object NeqExpr : Func("!=") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkMinSize(2)
        val distinct = args.map { it.eval(env) }.toSet()

        return Bool(args.size == distinct.size)
    }
}

val lessFunc = createCompFun("<") { it < 0 }
val lessEqFunc = createCompFun("<=") { it <= 0 }
val greaterFunc = createCompFun(">") { it > 0 }
val greaterEqFunc = createCompFun(">=") { it >= 0 }

fun equalityFunctions(): List<Pair<Symbol, Func>> {
    return listOf(
        EqExpr,
        NeqExpr,
        lessFunc,
        lessEqFunc,
        greaterFunc,
        greaterEqFunc
    ).map {
        Pair(Symbol(it.name), it)
    }
}
