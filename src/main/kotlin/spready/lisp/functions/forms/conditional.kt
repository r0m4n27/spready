package spready.lisp.functions.forms

import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.sexpr.Bool
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol

object IfExpr : Func("if") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val argsList = args.checkSize(3)
        val firstEvaluated = argsList[0].eval(env)

        return if (firstEvaluated.toBool().value) {
            argsList[1].eval(env)
        } else {
            argsList[2].eval(env)
        }
    }
}

object AndExpr : Func("and") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val argsBool = env.eval(args).map { it.toBool() }

        return if (argsBool.all { it.value }) {
            Bool(true)
        } else {
            Bool(false)
        }
    }
}

object OrExpr : Func("or") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val argsBool = env.eval(args).map { it.toBool() }

        return if (argsBool.any { it.value }) {
            Bool(true)
        } else {
            Bool(false)
        }
    }
}

object Cond : Func("cond") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        if (args.isEmpty()) {
            return Nil
        }

        for (rawItem in args) {
            val item = rawItem.cast<Cons>().toMutableList().checkMinSize(1)
            val first = item.removeFirst()

            if (first is Symbol && first.value == "else") {
                return env.eval(item).last()
            }

            val condEvaluated = env.eval(first)
            if (condEvaluated.toBool().value) {
                return when (item.size) {
                    0 -> condEvaluated
                    2 -> {
                        val arrow = item.removeFirst()

                        if (arrow is Symbol && arrow.value == "=>") {
                            handleArrow(condEvaluated, item, env)
                        } else {
                            env.eval(item).last()
                        }
                    }
                    else -> env.eval(item).last()
                }
            }
        }

        return Nil
    }

    private fun handleArrow(
        condEvaluated: SExpr,
        body: List<SExpr>,
        env: Environment
    ): SExpr {
        val lambda = env.eval(body[0])

        if (lambda is Func) {
            return lambda(env, listOf(condEvaluated))
        } else {
            throw EvalException("Third item must be a Func")
        }
    }
}

object Case : Func("case") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val argsMut = args.checkMinSize(2).toMutableList()
        val firstEvaluated = argsMut.removeFirst().eval(env)

        argsMut.forEach { case ->
            val caseList = case.cast<Cons>().toList().checkSize(2)

            val conditions = caseList[0]
            if (conditions is Symbol && conditions.value == "else") {
                return caseList[1].eval(env)
            } else {
                conditions.cast<Cons>().forEach {
                    if (it == firstEvaluated) {
                        return caseList[1].eval(env)
                    }
                }
            }
        }

        return Nil
    }
}

fun conditionalFunctions(): List<Pair<Symbol, Func>> {
    return listOf(IfExpr, AndExpr, OrExpr, Cond, Case).map {
        Pair(Symbol(it.name), it)
    }
}
