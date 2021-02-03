package spready.spread

import spready.lisp.Environment
import spready.lisp.sexpr.Cell
import spready.lisp.sexpr.ListElem
import spready.lisp.sexpr.SExpr

class SpreadEnvironment(
    private val canRunAction: (cell: Cell, result: SExpr) -> Unit,
    private val cantRunAction: (Cell) -> Unit,
    private val env: Environment = Environment.defaultEnv()
) {
    private val influences: MutableMap<Cell, MutableSet<Cell>> = mutableMapOf()
    private val dependencies: MutableMap<Cell, Set<Cell>> = mutableMapOf()
    private val canRun: MutableSet<Cell> = mutableSetOf()

    private val parsed: MutableMap<Cell, SExpr> = mutableMapOf()

    fun updateDependency(cell: Cell, expr: SExpr) {

        resetDependencies(cell)
        registerDependencies(cell, findDependencies(expr))

        parsed[cell] = expr

        if (hasCycle()) {
            throw SpreadException("Cycle found!")
        }
    }

    private fun resetDependencies(cell: Cell) {
        influences.forEach {
            it.value.remove(cell)
        }

        canRun.remove(cell)
    }

    private fun registerDependencies(cell: Cell, cellDependencies: Set<Cell>) {
        dependencies[cell] = cellDependencies

        cellDependencies.forEach {
            dependencies.getOrPut(it, ::emptySet)

            influences.getOrPut(it, ::mutableSetOf).add(cell)
        }

        updateCanRun(cell)
    }

    private fun updateCanRun(cell: Cell) {
        if (cell !in canRun && dependencies[cell]!!.all { it in canRun }) {
            canRun.add(cell)
            influences[cell]?.forEach {
                updateCanRun(it)
            }
        }
    }

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

    fun evalCell(cell: Cell) {
        if (cell in canRun) {
            val parsedCell = parsed[cell]!!

            canRunAction(cell, env.evalAndRegister(cell, parsedCell))

            influences[cell]?.forEach {
                evalCell(it)
            }
        } else {
            cantRunAction(cell)
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
