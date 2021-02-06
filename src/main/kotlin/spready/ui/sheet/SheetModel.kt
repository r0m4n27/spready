package spready.ui.sheet

import javafx.beans.property.SimpleSetProperty
import javafx.beans.property.SimpleStringProperty
import spready.lisp.sexpr.Cell
import spready.spread.Spread
import tornadofx.ViewModel
import tornadofx.asObservable
import tornadofx.getValue
import tornadofx.setValue

class SheetModel(private val spread: Spread = Spread()) : ViewModel() {

    private var currentCell: Cell? = null

    val currentInputProperty = SimpleStringProperty("")
    private var currentInput by currentInputProperty

    val changedCellsProperty = SimpleSetProperty<Cell>()
    private var changedCells by changedCellsProperty

    val allResults: Map<Cell, String>
        get() = spread.allResults

    fun evalInput() {
        val cell = currentCell

        if (cell != null) {
            spread[cell] = currentInput
        }

        changedCells = spread.changedCells.asObservable()
    }

    fun chooseCell(cell: Cell) {
        currentCell = cell

        currentInput = spread.getInput(cell) ?: ""
    }
}
