package spready.spread

import kotlinx.serialization.Serializable
import spready.lisp.parse
import spready.lisp.sexpr.Cell
import spready.lisp.tokenize

@Serializable(with = SpreadSerializer::class)
class Spread {
    private val cellInput: MutableMap<Cell, String> = mutableMapOf()
    private val cellResults: MutableMap<Cell, String> = mutableMapOf()

    private val env =
        SpreadEnvironment(
            { cell, result -> cellResults[cell] = result.toString() },
            { cellResults[it] = "#Err" }
        )

    val allResults: Map<Cell, String>
        get() = cellResults

    val allInputs: Map<Cell, String>
        get() = cellInput

    fun getResult(cell: Cell): String {
        return cellResults[cell] ?: throw SpreadException("Cell not found!")
    }

    fun getInput(cell: Cell): String {
        return cellInput[cell] ?: throw SpreadException("Cell not found!")
    }

    operator fun set(cell: Cell, input: String) {
        cellInput[cell] = input

        val parsed = parse(tokenize(input))

        if (parsed.size != 1) {
            throw SpreadException("Cell input can only have 1 Expression!")
        }

        env[cell] = parsed[0]
    }

    operator fun minusAssign(cell: Cell) {
        env.removeCell(cell)

        cellInput.remove(cell)
        cellResults.remove(cell)
    }
}
