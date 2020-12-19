package spready.lisp.functions

import spready.lisp.Cons
import spready.lisp.Environment
import spready.lisp.Func
import spready.lisp.Num
import spready.lisp.SExpr
import spready.lisp.Symbol

fun foldToNum(args: List<SExpr>, start: Int, acc: (Int, Int) -> Int): Num {
    val argsMapped = args.map {
        it.cast(Num::class).value
    }

    return Num(argsMapped.fold(start, acc))
}

object Plus : Func("+") {

    override fun invoke(env: Environment, args: Cons): SExpr {
        return foldToNum(args.evalAll(env), 0, Int::plus)
    }
}

// TODO: Wrong implemented
object Minus : Func("-") {

    override fun invoke(env: Environment, args: Cons): SExpr {
        return foldToNum(args.evalAll(env), 0, Int::minus)
    }
}

object Times : Func("*") {

    override fun invoke(env: Environment, args: Cons): SExpr {
        return foldToNum(args.evalAll(env), 1, Int::times)
    }
}

fun mathFunctions(): List<Pair<Symbol, Func>> {
    return listOf(Plus, Minus, Times).map {
        Pair(Symbol(it.name), it)
    }
}
