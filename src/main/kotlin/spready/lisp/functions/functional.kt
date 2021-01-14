package spready.lisp.functions

import spready.lisp.Environment
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.ListElem
import spready.lisp.sexpr.ListElem.Companion.toListElem
import spready.lisp.sexpr.Nil
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.cast

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
