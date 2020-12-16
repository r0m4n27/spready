package spready.lisp.functions

import spready.lisp.Cons
import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.Func
import spready.lisp.Num
import spready.lisp.SExpr
import spready.lisp.Symbol
import spready.lisp.evalAll

fun foldToNum(args: List<SExpr>, start: Int, acc: (Int, Int) -> Int): Num {
    val argsMapped = args.map {
        (it as? Num)?.value ?: throw EvalException("$it has to be Num!")
    }

    return Num(argsMapped.fold(start, acc))
}

object Plus : Func("+") {

    override fun invoke(env: Environment, args: Cons): SExpr {
        return foldToNum(evalAll(args, env), 0, Int::plus)
    }
}

object Minus : Func("-") {

    override fun invoke(env: Environment, args: Cons): SExpr {
        return foldToNum(evalAll(args, env), 0, Int::minus)
    }
}

object Times : Func("*") {

    override fun invoke(env: Environment, args: Cons): SExpr {
        return foldToNum(evalAll(args, env), 1, Int::times)
    }
}

fun mathFunctions(): List<Pair<Symbol, Func>> {
    return listOf(Plus, Minus, Times).map {
        Pair(Symbol(it.name), it)
    }
}
