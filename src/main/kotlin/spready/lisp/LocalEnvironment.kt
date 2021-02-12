package spready.lisp

import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Symbol
import spready.lisp.sexpr.Variable

/**
 * Local environment used for functions and bindings
 *
 * Has a reference to it's parent env
 */
class LocalEnvironment(private val globEnv: Environment) :
    Environment(mutableMapOf()) {

    /**
     * Looks up the variable
     * If it isn't in the local env it will be looked up in the global env
     */
    override fun get(variable: Variable): SExpr {
        return symbols[variable] ?: globEnv[variable]
    }

    /**
     * Sets the variable in the local env if the local env contains it
     * otherwise it will be set in the parent env
     */
    override fun set(variable: Variable, expr: SExpr) {
        if (symbols.containsKey(variable)) {
            super.set(variable, expr)
        } else {
            globEnv[variable] = expr
        }
    }

    /**
     * Removes the variable from the local env if the
     * local env doesn't contain it it will be removed from the parent env
     */
    override fun minusAssign(variable: Variable) {
        if (symbols.containsKey(variable)) {
            super.minusAssign(variable)
        } else {
            globEnv.minusAssign(variable)
        }
    }

    /**
     * Add a variable and its expression to the local environment
     */
    fun addLocal(variable: Variable, expr: SExpr) {
        symbols[variable] = expr
    }

    fun addLocal(mappings: List<Pair<Symbol, SExpr>>) {
        for (item in mappings) {
            addLocal(item.first, item.second)
        }
    }
}
