package spready.lisp.functions

import spready.lisp.Environment
import spready.lisp.sexpr.Bool
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Flt
import spready.lisp.sexpr.Fraction
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Integer
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
        createIdentity<Str>("str?"),
        createIdentity<Symbol>("symbol?"),
        createIdentity<Num>("num?"),
        createIdentity<Bool>("bool?"),
        createIdentity<Nil>("nil?"),
        createIdentity<Func>("func?"),
        createIdentity<Integer>("int?"),
        createIdentity<Flt>("float?"),
        createIdentity<Fraction>("fraction?"),
        IsPair,
        IsList
    ).map {
        Pair(Symbol(it.name), it)
    }
}
