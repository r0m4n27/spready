package spready.lisp.functions.forms

import spready.lisp.Environment
import spready.lisp.EvalException
import spready.lisp.LocalEnvironment
import spready.lisp.sexpr.Cons
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.cast

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
                if (argsMut.size >= 2) {
                    val symbols = argsMut.removeFirst().cast<Cons>()

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
            it.cast<Cons>().toList().checkSize(2)
        }.map {
            Pair(it[0].cast(), it[1].eval(env))
        }
    }
}

object LetStar : Func("let*") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val argsMut = args.toMutableList().checkMinSize(2)
        val firstAsCons = argsMut.removeFirst().cast<Cons>()

        val localEnv = LocalEnvironment(env)

        firstAsCons.map {
            it.cast<Cons>().toList().checkSize(2)
        }.map {
            Pair(it[0].cast<Symbol>(), it[1])
        }.forEach {
            localEnv.addLocal(it.first, it.second.eval(localEnv))
        }

        return localEnv.eval(argsMut).last()
    }
}

object LetRec : Func("letrec") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val argsMut = args.toMutableList().checkMinSize(2)
        val firstAsCons = argsMut.removeFirst().cast<Cons>()

        val localEnv = LocalEnvironment(env)

        firstAsCons.map {
            it.cast<Cons>().toList().checkSize(2)
        }.map {
            val first = it[0].cast<Symbol>()
            localEnv.addLocal(first, Nil)
            Pair(first, it[1])
        }.forEach {
            localEnv.addLocal(it.first, it.second.eval(localEnv))
        }

        return localEnv.eval(argsMut).last()
    }
}

object DoFunc : Func("do") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val exprs = args.toMutableList().checkMinSize(3)
        val localEnv = LocalEnvironment(env)

        val idBody = exprs.removeFirst().cast<Cons>().toList()
        val updateMap = initIds(idBody, env, localEnv)

        val returnExprs =
            exprs.removeFirst().cast<Cons>().toMutableList().checkMinSize(1)
        val test = returnExprs.removeFirst()

        while (!localEnv.eval(test).toBool().value) {
            localEnv.eval(exprs)

            updateMap.forEach {
                localEnv[it.key] = localEnv.eval(it.value)
            }
        }

        return if (returnExprs.isEmpty()) {
            Nil
        } else {
            localEnv.eval(returnExprs).last()
        }
    }

    private fun initIds(
        idBody: List<SExpr>,
        env: Environment,
        localEnv: LocalEnvironment
    ): MutableMap<Symbol, SExpr> {

        val updateMap: MutableMap<Symbol, SExpr> = mutableMapOf()

        idBody.map {
            it.cast<Cons>().toList().checkBetweenSize(2, 3)
        }.map {

            val first = it[0].cast<Symbol>()
            if (it.size == 3) {
                updateMap[first] = it[2]
            }

            Pair(first, it[1])
        }.forEach {
            localEnv.addLocal(it.first, env.eval(it.second))
        }

        return updateMap
    }
}

fun bindingsFunctions(): List<Pair<Symbol, Func>> {
    return listOf(Let, LetRec, LetStar, DoFunc).map { Pair(Symbol(it.name), it) }
}
