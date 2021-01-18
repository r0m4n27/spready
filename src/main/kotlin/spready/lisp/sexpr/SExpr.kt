package spready.lisp.sexpr

import spready.lisp.Environment
import spready.lisp.EvalException

interface SExpr {
    fun eval(env: Environment): SExpr = this
    fun toBool(): Bool = Bool(true)
}

inline fun <reified T : SExpr> SExpr.cast(): T {
    return this as? T
        ?: throw EvalException("Expected ${T::class.simpleName} got $this!")
}

inline fun <reified T : SExpr> Iterable<SExpr>.cast(): List<T> {
    return this.map {
        it.cast()
    }
}

data class Symbol(val value: String) : SExpr {
    override fun eval(env: Environment): SExpr {
        return env[this]
    }

    override fun toString() = value
}

data class Str(val value: String) : SExpr, Comparable<Str> {
    override fun toString() = "\"$value\""

    override fun compareTo(other: Str): Int {
        return value.compareTo(other.value)
    }
}

data class Bool(val value: Boolean) : SExpr {
    override fun toString() = value.toString()
    override fun toBool(): Bool = this
}

object Unquote : SExpr {
    override fun toString() = "#<unquote>"
}

object UnquoteSplice : SExpr {
    override fun toString() = "#<unquote-splice>"
}
