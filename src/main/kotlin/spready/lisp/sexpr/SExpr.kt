package spready.lisp.sexpr

import spready.lisp.Environment
import spready.lisp.EvalException

/**
 * A expression that can be evaluated
 */
interface SExpr {

    /**
     * Evaluates the expression given the env
     *
     * By default the expression will return itself
     */
    fun eval(env: Environment): SExpr = this

    /**
     * Every expression can be converted to a [Bool]
     *
     * Every [SExpr] is by default true
     */
    fun toBool(): Bool = Bool(true)
}

/**
 * Tries to cast the expression to a certain type
 *
 * @param T Type of the [SExpr] to cast to
 * @throws EvalException If the expression can't be casted to type [T]
 */
inline fun <reified T : SExpr> SExpr.cast(): T {
    return this as? T
        ?: throw EvalException("Expected ${T::class.simpleName} got $this!")
}

/**
 * Tries to cast a iterable of [SExpr]
 */
inline fun <reified T : SExpr> Iterable<SExpr>.cast(): List<T> {
    return this.map {
        it.cast()
    }
}

/**
 * Holds a string
 *
 * Can be compared
 */
data class Str(val value: String) : SExpr, Comparable<Str> {
    override fun toString() = "\"$value\""

    override fun compareTo(other: Str): Int {
        return value.compareTo(other.value)
    }
}

/**
 * Holds a bool value
 */
data class Bool(val value: Boolean) : SExpr {
    override fun toString() = if (value) {
        "#t"
    } else {
        "#f"
    }

    override fun toBool(): Bool = this
}

/**
 * Marker to unquote the expression after it
 */
object Unquote : SExpr {
    override fun toString() = "#<unquote>"
}

/**
 * Marker to unquote-splice the expression after it
 */
object UnquoteSplice : SExpr {
    override fun toString() = "#<unquote-splice>"
}
