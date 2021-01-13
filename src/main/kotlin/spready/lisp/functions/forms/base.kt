package spready.lisp.functions.forms

import spready.lisp.Environment
import spready.lisp.LocalEnvironment
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.cast

fun createLambda(name: String, variables: List<Symbol>, body: List<SExpr>): Func {
    return object : Func(name) {
        override fun invoke(env: Environment, args: List<SExpr>): SExpr {
            val argsEvaluated = env.eval(args)

            val localSymbols = variables.zip(argsEvaluated)
            val localEnv = LocalEnvironment(env)
            localEnv.addLocal(localSymbols)

            return localEnv.eval(body).last()
        }
    }
}

object Lambda : Func("lambda") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val argsMut = args.toMutableList().checkMinSize(2)
        val head = argsMut.removeFirst()

        val representation: String
        val headSymbols: List<Symbol>

        if (head is Nil) {
            representation = "()"
            headSymbols = listOf()
        } else {
            representation = head.toString()

            headSymbols = head.cast<Cons>().map {
                it.cast()
            }
        }

        return createLambda(
            "(lambda $representation)",
            headSymbols,
            argsMut
        )
    }
}

object ValEval : Func("val-eval") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(2)

        val firstAsSym = args[0].eval(env).cast<Symbol>()
        return env.evalAndRegister(firstAsSym, args[1])
    }
}

object Val : Func("val") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(2)

        val firstAsSym = args[0].cast<Symbol>()

        return env.evalAndRegister(firstAsSym, args[1])
    }
}

object FunExpr : Func("fun") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val argsMut = args.toMutableList().checkMinSize(3)

        val sym = argsMut.removeFirst().cast<Symbol>()

        val vars = argsMut.removeFirst()
        val varsSymbols: List<Symbol> = if (vars is Nil) {

            emptyList()
        } else {

            vars.cast<Cons>().map {
                it.cast()
            }
        }

        val lambda = createLambda(sym.toString(), varsSymbols, argsMut)

        env[sym] = lambda
        return sym
    }
}

object RunExpr : Func("run") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        return env.eval(args).lastOrNull() ?: Nil
    }
}

fun baseFunctions(): List<Pair<Symbol, Func>> {
    return listOf(
        Lambda,
        Val,
        ValEval,
        FunExpr,
        IfExpr,
        RunExpr,
    ).map {
        Pair(Symbol(it.name), it)
    }
}
