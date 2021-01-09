package spready.lisp.functions

import spready.lisp.Environment
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Num
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol

fun foldToNum(args: List<SExpr>, start: Int, acc: (Int, Int) -> Int): Num {
    val argsMapped = args.map {
        it.cast<Num>().value
    }

    return Num(argsMapped.fold(start, acc))
}

object Plus : Func("+") {

    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        return foldToNum(env.eval(args), 0, Int::plus)
    }
}

// TODO: Wrong implemented
object Minus : Func("-") {

    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        return foldToNum(env.eval(args), 0, Int::minus)
    }
}

object Times : Func("*") {

    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        return foldToNum(env.eval(args), 1, Int::times)
    }
}

fun mathFunctions(): List<Pair<Symbol, Func>> {
    return listOf(Plus, Minus, Times).map {
        Pair(Symbol(it.name), it)
    }
}
