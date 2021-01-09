package spready.lisp.sexpr

import spready.lisp.Environment
import spready.lisp.EvalException
import kotlin.reflect.safeCast

abstract class SExpr {
    open fun eval(env: Environment): SExpr = this
    open fun toBool(): Bool = Bool(true)

    inline fun <reified T : SExpr> cast(): T {
        val type = T::class

        return type.safeCast(this)
            ?: throw EvalException("Expected ${type.simpleName} got $this!")
    }
}

data class Symbol(val value: String) : SExpr() {
    override fun eval(env: Environment): SExpr {
        return env[this]
    }

    override fun toString() = value
}

data class Str(val value: String) : SExpr() {
    override fun toString() = "\"$value\""
}

data class Num(val value: Int) : SExpr() {
    override fun toString() = value.toString()
}

data class Bool(val value: Boolean) : SExpr() {
    override fun toString() = value.toString()
    override fun toBool(): Bool = this
}

object Nil : SExpr() {
    override fun toString() = "nil"
    override fun toBool(): Bool = Bool(false)
}
