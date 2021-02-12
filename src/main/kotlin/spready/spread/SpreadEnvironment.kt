package spready.spread

import spready.lisp.Environment
import spready.lisp.sexpr.Cell
import spready.lisp.sexpr.ListElem
import spready.lisp.sexpr.SExpr

/**
 * Tracks the dependencies of the cells
 *
 * If a cell is updated it will find all the dependencies of it,
 * eval it if possible and notify other cells it influences
 *
 * @param [canRunAction] Will be executed if a cell was evaluated
 * @param [cantRunAction] Will be executed if a cell can't be evaluated
 * @param [env] The environment the cells will be executed
 */
class SpreadEnvironment(
    private val canRunAction: (cell: Cell, result: SExpr) -> Unit,
    private val cantRunAction: (Cell) -> Unit,
    private val env: Environment
) {
    private val influences: MutableMap<Cell, MutableSet<Cell>> = mutableMapOf()
    private val dependencies: MutableMap<Cell, Set<Cell>> = mutableMapOf()
    private val canRun: MutableSet<Cell> = mutableSetOf()

    private val parsed: MutableMap<Cell, SExpr> = mutableMapOf()

    /**
     * Sets the [cell] to be tracked
     *
     * Will update it's dependencies
     * and evaluated if possible
     *
     * @throws [SpreadException] if a cycle is found
     */
    operator fun set(cell: Cell, expr: SExpr) {
        resetDependencies(cell)
        updateDependencies(cell, findDependencies(expr))

        parsed[cell] = expr

        if (hasCycle()) {
            throw SpreadException("Cycle found!")
        }

        evalCell(cell)
    }

    private fun resetDependencies(cell: Cell) {
        influences.forEach {
            it.value.remove(cell)
        }

        dependencies.remove(cell)
        canRun.remove(cell)
    }

    private fun updateDependencies(cell: Cell, cellDependencies: Set<Cell>) {
        dependencies[cell] = cellDependencies

        cellDependencies.forEach {
            dependencies.getOrPut(it, ::emptySet)
            influences.getOrPut(it, ::mutableSetOf).add(cell)
        }
    }

    /**
     * Remove the cell from the tracker and [env]
     *
     * @throws [SpreadException] if the [cell] influences other cells
     */
    fun removeCell(cell: Cell) {
        if (cell in influences) {
            throw SpreadException("$cell influences others!")
        }

        env -= cell

        dependencies.remove(cell)
        influences.remove(cell)
        canRun.remove(cell)
        parsed.remove(cell)
    }

    /**
     * Evaluated the [cell] if possible
     *
     * Calls [canRunAction] if the [cell] can be evaluated the with the result
     * Cells [cantRunAction] if the [cell] can't be evaluated
     *
     * If the [cell] can be evaluated [evalCell] will also be called
     * of the cells that [cell] influences
     */
    private fun evalCell(cell: Cell) {
        if (cell in canRun || dependencies[cell]!!.all(canRun::contains)) {
            canRun.add(cell)
            val parsedCell = parsed[cell]!!

            canRunAction(cell, env.evalAndRegister(cell, parsedCell))

            influences[cell]?.forEach {
                evalCell(it)
            }
        } else {
            cantRunAction(cell)
        }
    }

    /**
     * Finds all cells that are in [expr]
     */
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

    /**
     * Checks if there are cycles in the tracker
     *
     * Uses Kahns algorithm
     * Tarjan's could also be used but extra structures would be required
     */
    private fun hasCycle(): Boolean {
        val noIncoming = mutableListOf<Cell>()
        val graph =
            dependencies.mapValues { it.value.toMutableSet() }.toMutableMap()

        graph.filterValues { it.isEmpty() }.forEach {
            noIncoming.add(it.key)
            graph.remove(it.key)
        }

        while (noIncoming.isNotEmpty()) {
            val cell = noIncoming.removeFirst()

            influences[cell]?.forEach {
                val cellDepends = graph[it]!!
                cellDepends.remove(cell)
                if (cellDepends.size == 0) {
                    noIncoming.add(it)
                    graph.remove(it)
                }
            }
        }

        return graph.isNotEmpty()
    }
}
