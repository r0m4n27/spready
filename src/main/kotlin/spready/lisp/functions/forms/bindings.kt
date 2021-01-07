package spready.lisp.functions.forms

import spready.lisp.Cons
import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.Func
import spready.lisp.LocalEnvironment
import spready.lisp.Nil
import spready.lisp.SExpr
import spready.lisp.Symbol

object Let : Func("let") {
    override fun invoke(env: Environment, args: Cons): SExpr {
        val argsList = args.toMutListMinSize(2)

        return when (val first = argsList.removeFirst()) {
            is Cons -> {
                val mappedSymbols = mapSymbols(first, env)

                normalLet(mappedSymbols, env, argsList)
            }
            is Symbol -> {
                if (argsList.size > 2) {
                    val symbols = argsList.removeFirst().cast(Cons::class)

                    val mappedSymbols = mapSymbols(symbols, env)

                    namedLet(first, mappedSymbols, env, argsList)
                } else {
                    throw EvalException(
                        "Must have at least 3 arguments not ${argsList.size} + 1"
                    )
                }
            }
            else -> throw EvalException("First argument must be Cons or Symbol!")
        }
    }

    private fun normalLet(
        symbols: List<Pair<Symbol, SExpr>>,
        env: Environment,
        body: List<SExpr>
    ): SExpr {
        val localEnv = LocalEnvironment(env)
        localEnv.addLocal(symbols)

        return localEnv.eval(body).last()
    }

    private fun namedLet(
        name: Symbol,
        mappedSymbols: List<Pair<Symbol, SExpr>>,
        env: Environment,
        body: List<SExpr>
    ): SExpr {
        val localEnv = LocalEnvironment(env)
        localEnv.addLocal(mappedSymbols)
        val symbols = mappedSymbols.map { it.first }

        val lambda = createLambda(name.value, symbols, body)

        localEnv[name] = lambda

        return localEnv.eval(body).last()
    }

    private fun mapSymbols(symbols: Cons, env: Environment): List<Pair<Symbol, SExpr>> {
        return symbols.map {
            it.cast(Cons::class).toListWithSize(2)
        }.map {
            Pair(it[0].cast(Symbol::class), it[1].eval(env))
        }
    }
}

object LetStar : Func("let*") {
    override fun invoke(env: Environment, args: Cons): SExpr {
        val argsList = args.toListWithSize(2)
        val firstAsCons = argsList[0].cast(Cons::class)

        val localEnv = LocalEnvironment(env)

        firstAsCons.map {
            it.cast(Cons::class).toListWithSize(2)
        }.map {
            Pair(it[0].cast(Symbol::class), it[1])
        }.forEach {
            localEnv.addLocal(it.first, it.second.eval(localEnv))
        }

        return argsList[1].eval(localEnv)
    }
}

object LetRec : Func("letrec") {
    override fun invoke(env: Environment, args: Cons): SExpr {
        val argsList = args.toListWithSize(2)
        val firstAsCons = argsList[0].cast(Cons::class)

        val localEnv = LocalEnvironment(env)

        firstAsCons.map {
            it.cast(Cons::class).toListWithSize(2)
        }.map {
            val first = it[0].cast(Symbol::class)
            localEnv.addLocal(first, Nil)
            Pair(first, it[1])
        }.forEach {
            localEnv.addLocal(it.first, it.second.eval(localEnv))
        }

        return argsList[1].eval(localEnv)
    }
}

fun bindingsFunctions(): List<Pair<Symbol, Func>> {
    return listOf(Let, LetRec, LetStar).map { Pair(Symbol(it.name), it) }
}
