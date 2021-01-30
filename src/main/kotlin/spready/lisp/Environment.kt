package spready.lisp

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

open class Environment(protected val symbols: MutableMap<Variable, SExpr>) {
    constructor() : this(mutableMapOf())

    fun eval(expr: SExpr): SExpr {
        return expr.eval(this)
    }

    fun eval(exprs: List<SExpr>): List<SExpr> {
        return exprs.map { it.eval(this) }
    }

    fun evalAndRegister(variable: Variable, expr: SExpr): SExpr {
        set(variable, expr.eval(this))
        return get(variable)
    }

    open operator fun get(variable: Variable): SExpr {
        return symbols[variable]
            ?: throw EvalException("Can't find variable $variable")
    }

    open operator fun set(variable: Variable, expr: SExpr) {
        symbols[variable] = expr
    }

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
