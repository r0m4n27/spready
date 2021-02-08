package spready.ui.sheet

import javafx.beans.property.SimpleSetProperty
import javafx.beans.property.SimpleStringProperty
import spready.lisp.EvalException
import spready.lisp.sexpr.Cell
import spready.spread.SpreadException
import spready.ui.SpreadModel
import tornadofx.ViewModel
import tornadofx.asObservable
import tornadofx.getValue
import tornadofx.setValue

class SheetModel : ViewModel() {
    private val spreadModel: SpreadModel by inject()

    private val spread by spreadModel.spreadProperty

    private var currentCell: Cell? = null

    val currentInputProperty = SimpleStringProperty("")
    private var currentInput by currentInputProperty

    val changedCellsProperty = SimpleSetProperty(emptySet<Cell>().asObservable())
    private var changedCells by changedCellsProperty

    val allResults: Map<Cell, String>
        get() = spread.allResults

    fun evalInput() {
        val cell = currentCell

        if (cell != null) {
            val result = if (currentInput == "") {
                try {
                    spread -= cell
                    Ok
                } catch (_: EvalException) {
                    Ok
                } catch (ex: SpreadException) {
                    Err(ex.message)
                }
            } else {
                try {
                    spread.setCell(cell, currentInput)
                    changedCells = spread.changedCells.asObservable()
                    Ok
                } catch (ex: EvalException) {
                    Err(ex.message)
                } catch (ex: SpreadException) {
                    Err(ex.message)
                }
            }

            fire(EvalStatusEvent(result))
        }
    }

    fun chooseCell(cell: Cell) {
        currentCell = cell

        currentInput = spread.getInput(cell) ?: ""
    }
}
