package spready.lisp.sexpr

import kotlinx.serialization.Serializable
import spready.lisp.Environment
import spready.spread.CellSerializer

/**
 * A variable that can be saved in the env
 */
sealed class Variable : SExpr {

    /**
     * Looks up itself in the env and returns the value
     */
    override fun eval(env: Environment): SExpr {
        return env[this]
    }
}

/**
 * Represents a global variable
 */
data class Symbol(val value: String) : Variable() {
    override fun toString() = value
}

/**
 * A cell that can be used in a Spread
 */
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
