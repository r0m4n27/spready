package spready.lisp.functions.forms

import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.cast

/**
 * If the arg is truthy eval the first branch otherwise the second
 */
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

/**
 * Iterates over all branches if the first elem of a branch is truthy handle that branch
 *
 * If a else is found it will handle this branch
 *
 * Handling:
 *
 * Only test: Return the evaluated test
 * "=>": The third arg must be a [Func], the evaluated test will be used as a arg for the lambda
 * else: Eval the branch and return the last evaluated item
 */
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

/**
 * Checks if the evaluated first item can be found in one of
 * the first elements (lists) of the branches, it will evaluate the args of the branch
 * and return the last one
 *
 * If a else is found it will handle this branch
 */
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

/**
 * Eval all args and return the last one
 */
object RunExpr : Func("run") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        return env.eval(args).lastOrNull() ?: Nil
    }
}

fun controlFlowFunctions(): List<Pair<Symbol, Func>> {
    return listOf(IfExpr, Cond, Case, RunExpr).map {
        Pair(Symbol(it.name), it)
    }
}
