package spready.ui.sheet

import javafx.beans.property.SimpleSetProperty
import javafx.beans.property.SimpleStringProperty
import spready.lisp.EvalException
import spready.lisp.sexpr.Cell
import spready.spread.SpreadException
import spready.ui.SpreadModel
import spready.ui.status.Err
import spready.ui.status.Ok
import spready.ui.status.StatusEvent
import tornadofx.ViewModel
import tornadofx.asObservable
import tornadofx.getValue
import tornadofx.setValue

/**
 * Holds the [currentCell], [allResults] of the spread, [currentInput] and the [changedCells]
 */
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

    /**
     * Evaluated the [currentCell] with the [currentInput]
     *
     * If [currentInput] is empty it will remove the cell from the spread
     * Fires a [StatusEvent] with the success of the evaluation or removal
     *
     */
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

            fire(StatusEvent(result))
        }
    }

    fun chooseCell(cell: Cell) {
        currentCell = cell

        currentInput = spread.getInput(cell) ?: ""
    }
}
