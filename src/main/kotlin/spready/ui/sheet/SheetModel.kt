package spready.ui.sheet

import javafx.beans.property.SimpleSetProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import spready.lisp.EvalException
import spready.lisp.sexpr.Cell
import spready.spread.Spread
import spready.spread.SpreadException
import spready.ui.menu.Open
import spready.ui.menu.Save
import spready.ui.menu.SpreadFileEvent
import tornadofx.ViewModel
import tornadofx.asObservable
import tornadofx.getValue
import tornadofx.setValue
import java.io.File

class SheetModel(private var spread: Spread = Spread()) : ViewModel() {

    private var currentCell: Cell? = null

    val currentInputProperty = SimpleStringProperty("")
    private var currentInput by currentInputProperty

    val changedCellsProperty = SimpleSetProperty<Cell>()
    private var changedCells by changedCellsProperty

    val allResults: Map<Cell, String>
        get() = spread.allResults

    init {
        subscribe<SpreadFileEvent> {
            when (it.action) {
                Open -> loadFromFile(it.file)
                Save -> saveToFile(it.file)
            }
        }
    }

    fun evalInput() {
        val cell = currentCell

        if (cell != null) {
            if (currentInput == "") {
                try {
                    spread -= cell
                } catch (_: EvalException) {
                } catch (ex: SpreadException) {
                    fire(EvalStatusEvent(Err(ex.message)))
                }
            } else {
                try {
                    spread[cell] = currentInput
                    fire(EvalStatusEvent(Ok))
                } catch (ex: EvalException) {
                    fire(EvalStatusEvent(Err(ex.message)))
                } catch (ex: SpreadException) {
                    fire(EvalStatusEvent(Err(ex.message)))
                }
            }
        }

        changedCells = spread.changedCells.asObservable()
    }

    fun chooseCell(cell: Cell) {
        currentCell = cell

        currentInput = spread.getInput(cell) ?: ""
    }

    private fun loadFromFile(file: File) {
        try {
            spread = Json.decodeFromString(file.readText())
            var maxRow = 0
            var maxCol = 0

            for (item in spread.allResults) {
                val cell = item.key

                if (cell.row > maxRow) {
                    maxRow = cell.row
                }

                if (cell.col > maxCol) {
                    maxCol = cell.col
                }
            }

            if (maxRow < 10) {
                maxRow = 10
            }

            if (maxCol < 10) {
                maxCol = 10
            }

            fire(NewSpreadEvent(maxRow, maxCol))
        } catch (ex: SerializationException) {
            fire(EvalStatusEvent(Err(ex.message ?: "Can't open file!")))
        }
    }

    private fun saveToFile(file: File) {
        val text = Json.encodeToString(spread)
        file.writeText(text)
    }
}
