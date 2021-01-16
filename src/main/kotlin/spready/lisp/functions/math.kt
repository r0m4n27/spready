package spready.lisp.functions

import spready.lisp.Environment
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Integer
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.cast

fun reduceToInteger(args: List<SExpr>, acc: (Int, Int) -> Int): Integer {
    val argsMapped = args.map {
        it.cast<Integer>().value
    }

    return Integer(argsMapped.reduce(acc))
}

object Plus : Func("+") {

    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        return reduceToInteger(env.eval(args), Int::plus)
    }
}

object Minus : Func("-") {

    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkMinSize(1)

        return reduceToInteger(env.eval(args), Int::minus)
    }
}

object Times : Func("*") {

    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        return reduceToInteger(env.eval(args), Int::times)
    }
}

fun mathFunctions(): List<Pair<Symbol, Func>> {
    return listOf(Plus, Minus, Times).map {
        Pair(Symbol(it.name), it)
    }
}
