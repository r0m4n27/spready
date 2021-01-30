package spready

import spready.lisp.Environment
import spready.lisp.parse
import spready.lisp.sexpr.Cell
import spready.lisp.sexpr.SExpr
import spready.lisp.tokenize

class Spread(private val env: Environment) {

    // String input and parsed
    private val stringInput: MutableMap<Cell, Pair<String, SExpr>> = mutableMapOf()

    val allExprs: Map<Cell, String>
        get() {
            val out = mutableMapOf<Cell, String>()

            for (key in stringInput.keys) {
                out[key] = env[key].toString()
            }

            return out
        }

    operator fun get(cell: Cell): String {

        return stringInput[cell]?.first ?: throw SpreadException("Cell not found!")
    }

    operator fun set(cell: Cell, input: String) {
        val parsed = parse(tokenize(input))

        if (parsed.size != 1) {
            throw SpreadException("Cell input can only have 1 Expression!")
        }

        stringInput[cell] = Pair(input, parsed[0])
        env.evalAndRegister(cell, parsed[0])
    }

    operator fun minusAssign(cell: Cell) {
        env -= cell
        stringInput.remove(cell)
    }
}
