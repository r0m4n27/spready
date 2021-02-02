package spready.spread

import spready.lisp.Environment
import spready.lisp.sexpr.Cell
import spready.lisp.sexpr.ListElem
import spready.lisp.sexpr.SExpr

class DependencyTracker(
    private val env: Environment,
    private val parsedCells: MutableMap<Cell, SExpr>
) {

    private val cellInfluences: MutableMap<Cell, MutableSet<Cell>> = mutableMapOf()
    private val cellDependencies: MutableMap<Cell, MutableSet<Cell>> = mutableMapOf()

    fun updateCell(cell: Cell) {
        val cellDepends = findDependencies(
            parsedCells[cell] ?: throw SpreadException("Cant find cell!")
        )

        if (cellDepends.contains(cell)) {
            throw SpreadException("Cell cant reference itself")
        }

        cellDependencies[cell] = cellDepends

        for (dependency in cellDepends) {
            cellInfluences.getOrPut(dependency, ::mutableSetOf).add(cell)
        }

        if (hasCycle()) {
            throw SpreadException("Cycle found!")
        }

        notifyDependencies(cell)
    }

    fun removeCell(cell: Cell) {
        if (cellInfluences.containsKey(cell)) {
            throw SpreadException("$cell influences others!")
        }

        cellDependencies.remove(cell)
        cellInfluences.remove(cell)
    }

    private fun notifyDependencies(cell: Cell) {
        cellInfluences[cell]?.forEach {
            val parsed = parsedCells[it] ?: throw SpreadException("Can't find Cell!")

            env.evalAndRegister(it, parsed)
            notifyDependencies(it)
        }
    }

    private fun findDependencies(expr: SExpr): MutableSet<Cell> {
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
            is Cell -> mutableSetOf(expr)
            else -> mutableSetOf()
        }
    }

    // Kahns algorithm is used but without keeping track of the order
    // Tarjan's could also be used but extra structures would be required

    private fun hasCycle(): Boolean {
        val noIncoming: MutableList<Cell> = mutableListOf()
        val dependency = cellDependencies.toMutableMap()
        val influence = cellInfluences.toMutableMap()

        dependency.filterValues { it.isEmpty() }.forEach {
            noIncoming.add(it.key)
            dependency.remove(it.key)
        }

        while (noIncoming.isNotEmpty()) {
            val cell = noIncoming.removeFirst()
            influence.remove(cell)?.forEach {
                if (dependency[it]?.size == 1) {
                    noIncoming.add(it)
                    dependency.remove(it)
                }
            }
        }

        return dependency.isNotEmpty()
    }
}
