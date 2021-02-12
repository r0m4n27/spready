package spready.lisp

import spready.lisp.functions.cellFunctions
import spready.lisp.functions.conversionFunctions
import spready.lisp.functions.equalityFunctions
import spready.lisp.functions.forms.bindingsFunctions
import spready.lisp.functions.forms.controlFlowFunctions
import spready.lisp.functions.forms.definitionFunctions
import spready.lisp.functions.forms.quotingFunctions
import spready.lisp.functions.functionalFunctions
import spready.lisp.functions.identityFunctions
import spready.lisp.functions.listFunctions
import spready.lisp.functions.logicFunctions
import spready.lisp.functions.math.approxFunctions
import spready.lisp.functions.math.arithmeticFunctions
import spready.lisp.functions.math.floatFunctions
import spready.lisp.functions.math.fractionFunctions
import spready.lisp.functions.math.predicateFunctions
import spready.lisp.functions.stringFunctions
import spready.lisp.sexpr.SExpr
import spready.lisp.sexpr.Variable

/**
 * Holds variables with expressions associated to them
 */
open class Environment(protected val symbols: MutableMap<Variable, SExpr>) {
    constructor() : this(mutableMapOf())

    /**
     * Evaluates the [expr] with this env
     */
    fun eval(expr: SExpr): SExpr {
        return expr.eval(this)
    }

    fun eval(exprs: List<SExpr>): List<SExpr> {
        return exprs.map { it.eval(this) }
    }

    /**
     * Evaluates the expression
     * and sets the variable to its return value
     *
     * @return The evaluated expression
     */
    fun evalAndRegister(variable: Variable, expr: SExpr): SExpr {
        set(variable, expr.eval(this))
        return get(variable)
    }

    /**
     * @return the saved expression for the variable
     * @throws EvalException When the variable isn't in the env
     */
    open operator fun get(variable: Variable): SExpr {
        return symbols[variable]
            ?: throw EvalException("Can't find variable $variable")
    }

    /**
     * Adds the variable with the expr to the env
     */
    open operator fun set(variable: Variable, expr: SExpr) {
        symbols[variable] = expr
    }

    /**
     * Removes the variable from the env
     *
     * @throws EvalException When the env doesn't contain the variable
     */
    open operator fun minusAssign(variable: Variable) {
        if (symbols.containsKey(variable)) {
            symbols.remove(variable)
        } else {
            throw EvalException("Can't find variable $variable")
        }
    }

    operator fun minusAssign(removeVars: Iterable<Variable>) {
        for (item in removeVars) {
            minusAssign(item)
        }
    }

    operator fun plusAssign(pair: Pair<Variable, SExpr>) {
        set(pair.first, pair.second)
    }

    operator fun plusAssign(pairs: Iterable<Pair<Variable, SExpr>>) {
        for (pair in pairs) {
            plusAssign(pair)
        }
    }

    companion object {

        /**
         * Creates a env with all default functions provided
         */
        fun defaultEnv(): Environment {
            val env = Environment()
            // Forms
            env += definitionFunctions()
            env += bindingsFunctions()
            env += controlFlowFunctions()
            env += quotingFunctions()

            // Procedures
            env += identityFunctions()
            env += listFunctions()
            env += logicFunctions()
            env += functionalFunctions()
            env += conversionFunctions()
            env += stringFunctions()
            env += equalityFunctions()
            env += cellFunctions()

            // Math
            env += arithmeticFunctions()
            env += approxFunctions()
            env += fractionFunctions()
            env += predicateFunctions()
            env += floatFunctions()

            return env
        }
    }
}
