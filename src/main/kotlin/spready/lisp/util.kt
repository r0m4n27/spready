package spready.lisp

import kotlin.reflect.KClass
import kotlin.reflect.safeCast

fun Cons.evalAll(environment: Environment): List<SExpr> {
    return this.map {
        it.eval(environment)
    }
}

fun <T : SExpr> SExpr.cast(type: KClass<T>): T {
    return type.safeCast(this)
        ?: throw EvalException("Expected ${type.simpleName} got $this!")
}
