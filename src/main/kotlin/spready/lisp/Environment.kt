package spready.lisp

import spready.lisp.functions.forms.baseFunctions
import spready.lisp.functions.forms.bindingsFunctions
import spready.lisp.functions.forms.conditionalFunctions
import spready.lisp.functions.forms.quotingFunctions
import spready.lisp.functions.mathFunctions
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol

open class Environment(protected val symbols: MutableMap<Symbol, SExpr>) {
    constructor() : this(mutableMapOf())

    fun eval(expr: SExpr): SExpr {
        return expr.eval(this)
    }

    fun eval(exprs: List<SExpr>): List<SExpr> {
        return exprs.map { it.eval(this) }
    }

    fun evalAndRegister(symbol: Symbol, expr: SExpr): SExpr {
        set(symbol, expr.eval(this))
        return get(symbol)
    }

    open operator fun get(symbol: Symbol): SExpr {
        return symbols[symbol]
            ?: throw EvalException("Can't find symbol $symbol")
    }

    open operator fun set(symbol: Symbol, expr: SExpr) {
        symbols[symbol] = expr
    }

    open operator fun minusAssign(symbol: Symbol) {
        if (symbols.containsKey(symbol)) {
            symbols.remove(symbol)
        } else {
            throw EvalException("Can't find symbol $symbol")
        }
    }

    operator fun minusAssign(removeSymbols: Iterable<Symbol>) {
        for (symbol in removeSymbols) {
            minusAssign(symbol)
        }
    }

    operator fun plusAssign(pair: Pair<Symbol, SExpr>) {
        set(pair.first, pair.second)
    }

    operator fun plusAssign(pairs: Iterable<Pair<Symbol, SExpr>>) {
        for (pair in pairs) {
            plusAssign(pair)
        }
    }

    companion object {

        fun defaultEnv(): Environment {
            val env = Environment()
            // Forms
            env += baseFunctions()
            env += bindingsFunctions()
            env += conditionalFunctions()
            env += quotingFunctions()

            env += mathFunctions()

            return env
        }
    }
}
