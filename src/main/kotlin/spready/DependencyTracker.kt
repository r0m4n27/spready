package spready

import spready.lisp.Environment
import spready.lisp.sexpr.Cell
import spready.lisp.sexpr.ListElem
import spready.lisp.sexpr.SExpr

class DependencyTracker(
    private val env: Environment,
    private val parsedCells: MutableMap<Cell, SExpr>
) {

    private val cellInfluences: MutableMap<Cell, MutableSet<Cell>> = mutableMapOf()

    fun updateCell(cell: Cell) {
        val cellDepends = findDependencies(
            parsedCells[cell] ?: throw SpreadException("Cant find cell!")
        )

        if (cellDepends.contains(cell)) {
            throw SpreadException("Cell cant reference itself")
        }

        for (dependency in cellDepends) {
            cellInfluences.getOrPut(dependency, ::mutableSetOf).add(cell)
        }

        notifyDependencies(cell)
    }

    fun removeCell(cell: Cell) {
        if (cellInfluences.containsKey(cell)) {
            throw SpreadException("$cell influences others!")
        }

        cellInfluences.remove(cell)
    }

    private fun notifyDependencies(cell: Cell) {
        cellInfluences[cell]?.forEach {
            val parsed = parsedCells[it] ?: throw SpreadException("Can't find Cell!")

            env.evalAndRegister(it, parsed)
            notifyDependencies(it)
        }
    }

    private fun findDependencies(expr: SExpr): Set<Cell> {
        return when (expr) {
            is ListElem -> {
                val output: MutableSet<Cell> = mutableSetOf()

                for (subExpr in expr) {
                    if (subExpr is Cell) {
                        output.add(subExpr)
                    } else if (subExpr is ListElem) {
                        output.addAll(findDependencies(subExpr))
                    }
                }

                output
            }
            is Cell -> setOf(expr)
            else -> setOf()
        }
    }
}
