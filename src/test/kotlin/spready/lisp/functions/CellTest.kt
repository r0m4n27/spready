package spready.lisp.functions

import spready.lisp.BaseEval
import spready.lisp.sexpr.Cell
import spready.lisp.sexpr.Integer
import kotlin.test.Test

class CellTest : BaseEval() {

    @Test
    fun `cell range normal`() {
        val input = mapOf(
            Cell(1, 1) to 1,
            Cell(1, 2) to 2,
            Cell(1, 3) to 3,
            Cell(2, 1) to 4,
            Cell(2, 2) to 5,
            Cell(2, 3) to 6,
        )

        input.forEach {
            env[it.key] = Integer(it.value)
        }

        equalsEval("'(1 2 3 4 5 6)", "(cell-range '#1.1 '#2.3)")
    }
}
