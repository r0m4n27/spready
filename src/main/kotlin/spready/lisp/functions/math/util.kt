package spready.lisp.functions.math

import spready.lisp.Environment
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Num
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.cast

inline fun createReduceFunc(
    name: String,
    crossinline fn: (acc: Num, num: Num) -> Num
): Func {
    return object : Func(name) {
        override fun invoke(env: Environment, args: List<SExpr>): SExpr {
            args.checkMinSize(2)

            return env.eval(args).cast<Num>().reduce(fn)
        }
    }
}

inline fun createFuncWithOneArg(
    name: String,
    crossinline fn: (num: Num) -> Num
): Func {
    return object : Func(name) {
        override fun invoke(env: Environment, args: List<SExpr>): SExpr {
            args.checkSize(1)

            return fn(env.eval(args[0]).cast())
        }
    }
}

inline fun createFuncWithTwoArgs(
    name: String,
    crossinline fn: (num: Num, other: Num) -> Num
): Func {
    return object : Func(name) {
        override fun invoke(env: Environment, args: List<SExpr>): SExpr {
            args.checkSize(2)
            val evaluated = env.eval(args).cast<Num>()

            return fn(evaluated[0], evaluated[1])
        }
    }
}
