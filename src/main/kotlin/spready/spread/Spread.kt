package spready.spread

import kotlinx.serialization.Serializable
import spready.lisp.parse
import spready.lisp.sexpr.Cell
import spready.lisp.tokenize

@Serializable(with = SpreadSerializer::class)
class Spread {
    private val cellInput: MutableMap<Cell, String> = mutableMapOf()
    private val cellResults: MutableMap<Cell, String> = mutableMapOf()
    private val changed: MutableSet<Cell> = mutableSetOf()

    private val env =
        SpreadEnvironment(
            { cell, result ->
                changed.add(cell)
                cellResults[cell] = result.toString()
            },
            {
                changed.add(it)
                cellResults[it] = "#Err"
            }
        )

    val allInputs: Map<Cell, String>
        get() = cellInput

    val allResults: Map<Cell, String>
        get() = cellResults

    val changedCells: Set<Cell>
        get() = changed

    fun getInput(cell: Cell): String? {
        return cellInput[cell]
    }

    operator fun set(cell: Cell, input: String) {
        cellInput[cell] = input

        val parsed = parse(tokenize(input))

        if (parsed.size != 1) {
            throw SpreadException("Cell input can only have 1 Expression!")
        }

        changed.clear()
        env[cell] = parsed[0]
    }

    operator fun minusAssign(cell: Cell) {
        env.removeCell(cell)

        cellInput.remove(cell)
        cellResults.remove(cell)
    }
}
