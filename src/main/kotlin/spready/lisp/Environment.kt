package spready.lisp

class Environment(private val symbols: MutableMap<Symbol, SExpr>) {
    constructor() : this(mutableMapOf())

    fun eval(expr: SExpr): SExpr {
        return expr.eval(this)
    }

    fun copy(): Environment {
        return Environment(symbols.toMutableMap())
    }

    operator fun get(symbol: Symbol): SExpr {
        return symbols[symbol]
            ?: throw EvalException("Can't find symbol $symbol")
    }

    operator fun set(symbol: Symbol, expr: SExpr) {
        symbols[symbol] = expr
    }

    operator fun minusAssign(symbol: Symbol) {
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

    // companion object {
    //
    //     fun defaultEnv(): Environment {
    //         val env = Environment()
    //
    //         env += baseFunctions()
    //         env += mathFunctions()
    //
    //         return env
    //     }
    // }
}
