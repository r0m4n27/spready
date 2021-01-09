package spready.lisp.functions.forms

import spready.lisp.Environment
import spready.lisp.LocalEnvironment
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol

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
        args.checkSize(2)

        val representation: String
        val headSymbols: List<Symbol>

        if (args[0] is Nil) {
            representation = "()"
            headSymbols = listOf()
        } else {
            val head = args[0].cast<Cons>()

            representation = head.toString()

            headSymbols = head.map {
                it.cast()
            }
        }

        return createLambda(
            "(lambda $representation)",
            headSymbols,
            listOf(args[1])
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
        args.checkSize(3)

        val sym = args[0].cast<Symbol>()

        val varsSymbols: List<Symbol> = if (args[1] is Nil) {

            emptyList()
        } else {

            args[1].cast<Cons>().map {
                it.cast()
            }
        }

        val lambda = createLambda(sym.toString(), varsSymbols, listOf(args[2]))

        env[sym] = lambda
        return sym
    }
}

object IfExpr : Func("if") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(3)
        val firstEvaluated = args[0].eval(env)

        return if (firstEvaluated.toBool().value) {
            args[1].eval(env)
        } else {
            args[2].eval(env)
        }
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
