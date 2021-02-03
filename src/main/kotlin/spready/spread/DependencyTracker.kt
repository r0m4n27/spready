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
    private val cellDependencies: MutableMap<Cell, Set<Cell>> = mutableMapOf()

    fun updateCell(cell: Cell) {
        val cellDepends = findDependencies(
            parsedCells[cell] ?: throw SpreadException("Cant find cell!")
        )

        cellDependencies[cell] = cellDepends

        if (cellDepends.any { it !in cellDependencies }) {
            throw SpreadException("Previous Cells are not tracked!")
        }

        cellDepends.forEach {
            cellInfluences.getOrPut(it, ::mutableSetOf).add(cell)
        }

        if (hasCycle()) {
            throw SpreadException("Cycle found!")
        }

        notifyDependencies(cell)
    }

    fun removeCell(cell: Cell) {
        if (cell in cellInfluences) {
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
            else -> emptySet()
        }
    }

    // Kahns algorithm is used but without keeping track of the order
    // Tarjan's could also be used but extra structures would be required

    private fun hasCycle(): Boolean {
        val noIncoming = mutableListOf<Cell>()
        val graph =
            cellDependencies.mapValues { it.value.toMutableSet() }.toMutableMap()

        graph.filterValues { it.isEmpty() }.forEach {
            noIncoming.add(it.key)
            graph.remove(it.key)
        }

        while (noIncoming.isNotEmpty()) {
            val cell = noIncoming.removeFirst()

            cellInfluences[cell]?.forEach {
                val cellDepends = graph[it]
                cellDepends?.remove(cell)
                if (cellDepends?.size == 0) {
                    noIncoming.add(it)
                    graph.remove(it)
                }
            }
        }

        return graph.isNotEmpty()
    }
}
