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

data class Symbol(val value: String) : SExpr {
    override fun eval(env: Environment): SExpr {
        return env[this]
    }

    override fun toString() = value
}

data class Str(val value: String) : SExpr {
    override fun toString() = "\"$value\""
}

data class Num(val value: Int) : SExpr {
    override fun toString() = value.toString()
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
