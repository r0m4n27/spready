package spready.lisp.functions

import spready.lisp.Environment
import spready.lisp.sexpr.Bool
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol

object AndExpr : Func("and") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val argsBool = env.eval(args).map { it.toBool() }

        return if (argsBool.all { it.value }) {
            Bool(true)
        } else {
            Bool(false)
        }
    }
}

object OrExpr : Func("or") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        val argsBool = env.eval(args).map { it.toBool() }

        return if (argsBool.any { it.value }) {
            Bool(true)
        } else {
            Bool(false)
        }
    }
}

object NotExpr : Func("not") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(1)

        val evalBool = env.eval(args[0]).toBool()
        return Bool(!evalBool.value)
    }
}

object EqExpr : Func("eq?") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(2)

        return Bool(env.eval(args[0]) == env.eval(args[1]))
    }
}

object NeqExpr : Func("neq?") {
    override fun invoke(env: Environment, args: List<SExpr>): SExpr {
        args.checkSize(2)

        return Bool(env.eval(args[0]) != env.eval(args[1]))
    }
}

fun logicFunctions(): List<Pair<Symbol, Func>> =
    listOf(AndExpr, OrExpr, NotExpr, EqExpr, NeqExpr).map {
        Pair(Symbol(it.name), it)
    }
