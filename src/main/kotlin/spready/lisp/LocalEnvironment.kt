package spready.lisp

import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.Variable

class LocalEnvironment(private val globEnv: Environment) :
    Environment(mutableMapOf()) {
    override fun get(variable: Variable): SExpr {
        return symbols[variable] ?: globEnv[variable]
    }

    override fun set(variable: Variable, expr: SExpr) {
        if (symbols.containsKey(variable)) {
            super.set(variable, expr)
        } else {
            globEnv[variable] = expr
        }
    }

    override fun minusAssign(variable: Variable) {
        if (symbols.containsKey(variable)) {
            super.minusAssign(variable)
        } else {
            globEnv.minusAssign(variable)
        }
    }

    fun addLocal(variable: Variable, expr: SExpr) {
        symbols[variable] = expr
    }

    fun addLocal(mappings: List<Pair<Symbol, SExpr>>) {
        for (item in mappings) {
            addLocal(item.first, item.second)
        }
    }
}
