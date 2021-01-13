package spready.lisp.functions.procedures

import spready.lisp.Environment
import spready.lisp.sexpr.Bool
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.ListElem
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.Num
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Str
import spready.lisp.sexpr.Symbol

inline fun <reified T : SExpr> createIdentity(name: String): Func {

    return object : Func(name) {
        override fun invoke(env: Environment, args: List<SExpr>): SExpr {
            args.checkSize(1)

            return if (env.eval(args[0]) is T) {
                Bool(true)
            } else {
                Bool(false)
            }
        }
    }
}

object IsPair : Func("pair?") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(1)

        val first = env.eval(args[0])
        return if (first is Cons && first.tail !is ListElem) {
            Bool(true)
        } else {
            Bool(false)
        }
    }
}

object IsList : Func("list?") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(1)

        val first = env.eval(args[0])
        return when {
            first is Cons && first.tail is ListElem -> Bool(true)
            first is Nil -> Bool(true)
            else -> Bool(false)
        }
    }
}

fun identityFunctions(): List<Pair<Symbol, Func>> {
    return listOf(
        Pair(Symbol("str?"), createIdentity<Str>("str?")),
        Pair(Symbol("symbol?"), createIdentity<Symbol>("symbol?")),
        Pair(Symbol("num?"), createIdentity<Num>("num?")),
        Pair(Symbol("bool?"), createIdentity<Bool>("bool?")),
        Pair(Symbol("nil?"), createIdentity<Nil>("nil?")),
        Pair(Symbol("func?"), createIdentity<Func>("func?"))
    ) + listOf(Pair(Symbol("pair?"), IsPair), Pair(Symbol("list?"), IsList))
}
