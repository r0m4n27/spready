package spready.lisp.functions.forms

import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Cons.Companion.toCons
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.Unquote
import spready.lisp.sexpr.UnquoteSplice

object Quote : Func("quote") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(1)

        return args[0]
    }
}

// Nested Quasiquotes aren't supported
object Quasiquote : Func("quasiquote") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(1)

        return when (val first = args[0]) {
            is Cons -> {
                first.flatMap {
                    handleExpr(it, env)
                }.toCons()
            }
            else -> args[0]
        }
    }

    private fun handleExpr(expr: SExpr, env: Environment): List<SExpr> {
        return if (expr is Cons) {
            val exprList = expr.toList()

            when (expr.first) {
                is Unquote -> {
                    exprList.checkSize(2)

                    listOf(env.eval(exprList[1]))
                }

                is UnquoteSplice -> {
                    exprList.checkSize(2)
                    val evaluated = env.eval(exprList[1])

                    if (evaluated is Cons) {
                        evaluated.toList()
                    } else {
                        throw EvalException(
                            "Result of UnquoteSplice must be cons not $evaluated"
                        )
                    }
                }
                else -> {
                    listOf(
                        exprList.flatMap {
                            handleExpr(it, env)
                        }.toCons()
                    )
                }
            }
        } else {
            listOf(expr)
        }
    }
}

fun quotingFunctions(): List<Pair<Symbol, Func>> {
    return listOf(Quote, Quasiquote).map {
        Pair(Symbol(it.name), it)
    }
}
