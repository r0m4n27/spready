package spready.lisp.functions.forms

import spready.lisp.Cons
import spready.lisp.Environment
import spready.lisp.Func
import spready.lisp.LocalEnvironment
import spready.lisp.Nil
import spready.lisp.SExpr
import spready.lisp.Symbol

fun createLambda(name: String, variables: List<Symbol>, body: SExpr): Func {
    return object : Func(name) {
        override fun invoke(env: Environment, args: Cons): SExpr {
            val argsEvaluated = args.evalAll(env)

            val localSymbols = variables.zip(argsEvaluated)
            val localEnv = LocalEnvironment(env)
            localEnv.addLocal(localSymbols)

            return body.eval(localEnv)
        }
    }
}

object Lambda : Func("lambda") {
    override fun invoke(env: Environment, args: Cons): SExpr {
        val argsList = args.toListWithSize(2)

        val representation: String
        val headSymbols: List<Symbol>

        if (argsList[0] is Nil) {
            representation = "()"
            headSymbols = listOf()
        } else {
            val head = argsList[0].cast(Cons::class)

            representation = head.toString()

            headSymbols = head.map {
                it.cast(Symbol::class)
            }
        }

        return createLambda("(lambda $representation)", headSymbols, argsList[1])
    }
}

object ValEval : Func("val-eval") {
    override fun invoke(env: Environment, args: Cons): SExpr {
        val argsList = args.toListWithSize(2)

        val firstAsSym = argsList[0].eval(env).cast(Symbol::class)
        return env.evalAndRegister(firstAsSym, argsList[1])
    }
}

object Val : Func("val") {
    override fun invoke(env: Environment, args: Cons): SExpr {
        val argsList = args.toListWithSize(2)

        val firstAsSym = argsList[0].cast(Symbol::class)

        return env.evalAndRegister(firstAsSym, argsList[1])
    }
}

object FunExpr : Func("fun") {
    override fun invoke(env: Environment, args: Cons): SExpr {
        val argsList = args.toListWithSize(3)

        val sym = argsList[0].cast(Symbol::class)

        val varsSymbols: List<Symbol> = if (argsList[1] is Nil) {

            emptyList()
        } else {

            argsList[1].cast(Cons::class).map {
                it.cast(Symbol::class)
            }
        }

        val lambda = createLambda(sym.toString(), varsSymbols, argsList[2])

        env[sym] = lambda
        return sym
    }
}

object IfExpr : Func("if") {
    override fun invoke(env: Environment, args: Cons): SExpr {
        val argsList = args.toListWithSize(3)
        val firstEvaluated = argsList[0].eval(env)

        return if (firstEvaluated.toBool().value) {
            argsList[1].eval(env)
        } else {
            argsList[2].eval(env)
        }
    }
}

object RunExpr : Func("run") {
    override fun invoke(env: Environment, args: Cons): SExpr {

        // The empty args list has still Nil as the last element
        return args.evalAll(env).last()
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
