package spready

import spready.lisp.Environment
import spready.lisp.parse
import spready.lisp.sexpr.Cell
import spready.lisp.sexpr.SExpr
import spready.lisp.tokenize

class Spread(private val env: Environment) {

    private val cellsParsed: MutableMap<Cell, SExpr> = mutableMapOf()
    private val cellsInput: MutableMap<Cell, String> = mutableMapOf()
    private val tracker = DependencyTracker(env, cellsParsed)

    val allExprs: Map<Cell, String>
        get() {
            val out = mutableMapOf<Cell, String>()

            for (key in cellsInput.keys) {
                out[key] = env[key].toString()
            }

            return out
        }

    operator fun get(cell: Cell): String {

        return cellsInput[cell] ?: throw SpreadException("Cell not found!")
    }

    operator fun set(cell: Cell, input: String) {
        val parsed = parse(tokenize(input))

        if (parsed.size != 1) {
            throw SpreadException("Cell input can only have 1 Expression!")
        }
        cellsParsed[cell] = parsed[0]
        cellsInput[cell] = input

        env.evalAndRegister(cell, parsed[0])
        tracker.updateCell(cell)
    }

    operator fun minusAssign(cell: Cell) {
        tracker.removeCell(cell)

        env -= cell

        cellsInput.remove(cell)
        cellsParsed.remove(cell)
    }
}
