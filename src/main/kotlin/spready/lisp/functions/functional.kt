package spready.lisp.functions

import spready.lisp.Environment
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.ListElem
import spready.lisp.sexpr.ListElem.Companion.toListElem
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.cast

/**
 * Evaluates args and zips the lists together
 *
 * Is needed because Iterable.zip can only zip with another one
 */
fun zipArgs(args: List<SExpr>, env: Environment): List<List<SExpr>> {
    val lists = args.map {
        env.eval(it).cast<ListElem>().toList()
    }

    val minSize = lists.minOf { it.size }

    val zipped = List(minSize) { mutableListOf<SExpr>() }

    lists.forEach {
        it.forEachIndexed { index, expr ->
            if (index < minSize) {
                zipped[index].add(expr)
            }
        }
    }

    return zipped
}

/**
 * Maps a [Func] over the rest of args
 */
object MapFunc : Func("map") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val argsMut = args.toMutableList().checkMinSize(2)
        val func = env.eval(argsMut.removeFirst()).cast<Func>()

        val zipped = zipArgs(argsMut, env)

        return zipped.map {
            func(env, it)
        }.toListElem()
    }
}

/**
 * Evaluates a [Func] over the rest of the args
 */
object ForEachFunc : Func("for-each") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val argsMut = args.toMutableList().checkMinSize(2)
        val func = env.eval(argsMut.removeFirst()).cast<Func>()

        val zipped = zipArgs(argsMut, env)

        zipped.forEach() {
            func(env, it)
        }

        return Nil
    }
}

/**
 * Evaluates the [Func] with the rest of the args
 */
object ApplyFunc : Func("apply") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val argsMut = args.toMutableList().checkMinSize(2)

        val func = env.eval(argsMut.removeFirst()).cast<Func>()

        val input = argsMut.map {
            env.eval(it)
        }.flatMap {
            if (it is ListElem) {
                it
            } else {
                listOf(it)
            }
        }

        return func(env, input)
    }
}

fun functionalFunctions(): List<Pair<Symbol, Func>> {
    return listOf(MapFunc, ForEachFunc, ApplyFunc).map {
        Pair(Symbol(it.name), it)
    }
}
