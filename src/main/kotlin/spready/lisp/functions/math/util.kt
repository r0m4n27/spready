package spready.lisp.functions.math

import spready.lisp.Environment
import spready.lisp.sexpr.Func
import spready.lisp.sexpr.Num
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.cast

/**
 * Creates a [Func] that reduces is arguments with a func
 *
 * @param fn Will be used to reduce the numbers
 */
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

/**
 * Creates a [Func] that Transforms a number
 *
 * @param fn Used for the transformation
 */
inline fun createFuncWithOneArg(
    name: String,
    crossinline fn: (num: Num) -> SExpr
): Func {
    return object : Func(name) {
        override fun invoke(env: Environment, args: List<SExpr>): SExpr {
            args.checkSize(1)

            return fn(env.eval(args[0]).cast())
        }
    }
}

/**
 * Creates a [Func] that Transforms two numbers
 *
 * @param fn Used for the transformation
 */
inline fun createFuncWithTwoArgs(
    name: String,
    crossinline fn: (num: Num, other: Num) -> SExpr
): Func {
    return object : Func(name) {
        override fun invoke(env: Environment, args: List<SExpr>): SExpr {
            args.checkSize(2)
            val evaluated = env.eval(args).cast<Num>()

            return fn(evaluated[0], evaluated[1])
        }
    }
}
