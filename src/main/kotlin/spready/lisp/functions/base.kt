package spready.lisp.functions

import spready.lisp.Bool
import spready.lisp.Cons
import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.Func
import spready.lisp.LocalEnvironment
import spready.lisp.Nil
import spready.lisp.SExpr
import spready.lisp.Symbol
import spready.lisp.evalAll

fun registerSExpr(symbol: Symbol, expr: SExpr, env: Environment): SExpr {
    val evaluated = expr.eval(env)
    env[symbol] = evaluated

    return evaluated
}

fun Cons.toListCheckSize(size: Int): List<SExpr> {
    val argsList = this.toList()
    if (argsList.size != size) {
        throw EvalException("Can only have $size arguments not ${argsList.size}")
    }

    return argsList
}

fun createLambda(name: String, variables: List<Symbol>, body: SExpr): Func {
    return object : Func(name) {
        override fun invoke(env: Environment, args: Cons): SExpr {
            val argsEvaluated = evalAll(args, env)

            val localSymbols =
                mutableMapOf(*variables.zip(argsEvaluated).toTypedArray())
            val localEnv =
                LocalEnvironment(localSymbols, env)

            return body.eval(localEnv)
        }
    }
}

object Lambda : Func("lambda") {
    override fun invoke(env: Environment, args: Cons): SExpr {
        val argsList = args.toListCheckSize(2)

        val representation: String
        val headSymbols: List<Symbol>

        if (argsList[0] is Nil) {
            representation = "()"
            headSymbols = listOf()
        } else {
            val head = argsList[0] as? Cons
                ?: throw EvalException("First argument must be cons not $argsList[0]")

            representation = head.toString()

            headSymbols = head.map {
                it as? Symbol
                    ?: throw EvalException("Arguments must be Symbols not $it")
            }
        }

        return createLambda("(lambda $representation)", headSymbols, argsList[1])
    }
}

object ValEval : Func("val-eval") {
    override fun invoke(env: Environment, args: Cons): SExpr {
        val argsList = args.toListCheckSize(2)

        val firstAsSym = argsList[0].eval(env) as? Symbol
            ?: throw EvalException("First arg must eval to Symbol!")

        return registerSExpr(firstAsSym, argsList[1], env)
    }
}

object Val : Func("val") {
    override fun invoke(env: Environment, args: Cons): SExpr {
        val argsList = args.toListCheckSize(2)

        val firstAsSym = argsList[0] as? Symbol
            ?: throw EvalException("First arg must be a Symbol!")

        return registerSExpr(firstAsSym, argsList[1], env)
    }
}

object FunExpr : Func("fun") {
    override fun invoke(env: Environment, args: Cons): SExpr {
        val argsList = args.toListCheckSize(3)

        val sym = argsList[0] as? Symbol
            ?: throw EvalException("First arg must be a Symbol!")

        val varsSymbols: List<Symbol> = if (argsList[1] is Nil) {

            emptyList()
        } else {

            val vars = argsList[1] as? Cons
                ?: throw EvalException("Second arg must be cons not $argsList[0]")

            vars.map {
                it as? Symbol ?: throw EvalException("Variable must be Symbols not $it")
            }
        }

        val lambda = createLambda(sym.toString(), varsSymbols, argsList[2])

        env[sym] = lambda
        return sym
    }
}

object IfExpr : Func("if") {
    override fun invoke(env: Environment, args: Cons): SExpr {
        val argsList = args.toListCheckSize(3)

        return when (val firstEvaluated = argsList[0].eval(env)) {
            is Nil -> argsList[2].eval(env)
            is Bool -> {
                if (firstEvaluated.value) {
                    argsList[1].eval(env)
                } else {
                    argsList[2].eval(env)
                }
            }
            else -> argsList[1].eval(env)
        }
    }
}

object RunExpr : Func("run") {
    override fun invoke(env: Environment, args: Cons): SExpr {

        // The empty args list has still Nil as the last element
        return evalAll(args, env).last()
    }
}

object Let : Func("let") {
    override fun invoke(env: Environment, args: Cons): SExpr {
        val argsList = args.toListCheckSize(2)

        val firstAsCons =
            argsList[0] as? Cons ?: throw EvalException("First arg must be Cons!")

        val symbolsMapped = firstAsCons.map {
            (it as? Cons)?.toListCheckSize(2)
                ?: throw EvalException("The bindings must be Cons!")
        }.map {
            val firstAsSym = it[0] as? Symbol
                ?: throw EvalException("The first arg in the let must be a symbol!")

            Pair(firstAsSym, it[1].eval(env))
        }.toMap()

        val localEnv = LocalEnvironment(symbolsMapped.toMutableMap(), env)

        return argsList[1].eval(localEnv)
    }
}

fun baseFunctions(): List<Pair<Symbol, Func>> {
    return listOf(Lambda, Val, ValEval, FunExpr, IfExpr, RunExpr, Let).map {
        Pair(Symbol(it.name), it)
    }
}
