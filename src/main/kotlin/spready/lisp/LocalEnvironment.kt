package spready.lisp

class LocalEnvironment(
    localSymbols: MutableMap<Symbol, SExpr>,
    private val globEnv: Environment
) :
    Environment(localSymbols) {
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
}
