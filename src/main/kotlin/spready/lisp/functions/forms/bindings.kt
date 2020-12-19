package spready.lisp.functions.forms

import spready.lisp.Cons
import spready.lisp.Environment
import spready.lisp.Func
import spready.lisp.LocalEnvironment
import spready.lisp.Nil
import spready.lisp.SExpr
import spready.lisp.Symbol

object Let : Func("let") {
    override fun invoke(env: Environment, args: Cons): SExpr {
        val argsList = args.toListWithSize(2)

        val firstAsCons = argsList[0].cast(Cons::class)

        val symbolsMapped = firstAsCons.map {
            it.cast(Cons::class).toListWithSize(2)
        }.map {
            Pair(it[0].cast(Symbol::class), it[1].eval(env))
        }

        val localEnv = LocalEnvironment(env)
        localEnv.addLocal(symbolsMapped)

        return argsList[1].eval(localEnv)
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
            Pair(it[0].cast(Symbol::class), it[1])
        }.forEach {
            localEnv.addLocal(it.first, it.second.eval(localEnv))
        }

        return argsList[1].eval(localEnv)
    }
}

fun bindingsFunctions(): List<Pair<Symbol, Func>> {
    return listOf(Let, LetRec, LetStar).map { Pair(Symbol(it.name), it) }
}
