package spready.lisp.sexpr

import spready.lisp.Environment

sealed class Variable : SExpr {
    override fun eval(env: Environment): SExpr {
        return env[this]
    }
}

data class Symbol(val value: String) : Variable() {
    override fun toString() = value
}

data class Cell(val row: Int, val col: Int) : Variable() {
    override fun toString() = "#$row.$col"
}
