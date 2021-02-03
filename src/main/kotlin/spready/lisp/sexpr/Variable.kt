package spready.lisp.sexpr

import kotlinx.serialization.Serializable
import spready.lisp.Environment
import spready.spread.CellSerializer

sealed class Variable : SExpr {
    override fun eval(env: Environment): SExpr {
        return env[this]
    }
}

data class Symbol(val value: String) : Variable() {
    override fun toString() = value
}

@Serializable(with = CellSerializer::class)
data class Cell(val row: Int, val col: Int) : Variable() {
    override fun toString() = "#$row.$col"

    operator fun rangeTo(other: Cell): List<Cell> {
        return (row..other.row).flatMap { r ->
            (col..other.col).map { c ->
                Cell(r, c)
            }
        }
    }
}
