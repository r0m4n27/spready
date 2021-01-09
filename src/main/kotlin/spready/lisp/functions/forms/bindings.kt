package spready.lisp.functions.forms

import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.LocalEnvironment
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol

object Let : Func("let") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkMinSize(2)
        val argsMut = args.toMutableList()

        return when (val first = argsMut.removeFirst()) {
            is Cons -> {
                val mappedSymbols = mapSymbols(first, env)

                normalLet(mappedSymbols, env, argsMut)
            }
            is Symbol -> {
                if (argsMut.size > 2) {
                    val symbols = argsMut.removeFirst().cast(Cons::class)

                    val mappedSymbols = mapSymbols(symbols, env)

                    namedLet(first, mappedSymbols, env, argsMut)
                } else {
                    throw EvalException(
                        "Must have at least 3 arguments not ${argsMut.size + 1}"
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
            it.cast(Cons::class).toList().checkSize(2)
        }.map {
            Pair(it[0].cast(Symbol::class), it[1].eval(env))
        }
    }
}

object LetStar : Func("let*") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(2)
        val firstAsCons = args[0].cast(Cons::class)

        val localEnv = LocalEnvironment(env)

        firstAsCons.map {
            it.cast(Cons::class).toList().checkSize(2)
        }.map {
            Pair(it[0].cast(Symbol::class), it[1])
        }.forEach {
            localEnv.addLocal(it.first, it.second.eval(localEnv))
        }

        return args[1].eval(localEnv)
    }
}

object LetRec : Func("letrec") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(2)
        val firstAsCons = args[0].cast(Cons::class)

        val localEnv = LocalEnvironment(env)

        firstAsCons.map {
            it.cast(Cons::class).toList().checkSize(2)
        }.map {
            val first = it[0].cast(Symbol::class)
            localEnv.addLocal(first, Nil)
            Pair(first, it[1])
        }.forEach {
            localEnv.addLocal(it.first, it.second.eval(localEnv))
        }

        return args[1].eval(localEnv)
    }
}

fun bindingsFunctions(): List<Pair<Symbol, Func>> {
    return listOf(Let, LetRec, LetStar).map { Pair(Symbol(it.name), it) }
}
