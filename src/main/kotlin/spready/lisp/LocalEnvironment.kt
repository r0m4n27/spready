package spready.lisp

import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol

class LocalEnvironment(private val globEnv: Environment) :
    Environment(mutableMapOf()) {
    override fun get(symbol: Symbol): SExpr {
        return symbols[symbol] ?: globEnv[symbol]
    }

    override fun set(symbol: Symbol, expr: SExpr) {
        if (symbols.containsKey(symbol)) {
            super.set(symbol, expr)
        } else {
            globEnv[symbol] = expr
        }
    }

    override fun minusAssign(symbol: Symbol) {
        if (symbols.containsKey(symbol)) {
            super.minusAssign(symbol)
        } else {
            globEnv.minusAssign(symbol)
        }
    }

    fun addLocal(symbol: Symbol, expr: SExpr) {
        symbols[symbol] = expr
    }

    fun addLocal(mappings: List<Pair<Symbol, SExpr>>) {
        for (item in mappings) {
            addLocal(item.first, item.second)
        }
    }
}
