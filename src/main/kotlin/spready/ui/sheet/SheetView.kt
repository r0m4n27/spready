package spready.ui.sheet

import javafx.scene.control.SelectionMode
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import org.controlsfx.control.spreadsheet.Grid
import org.controlsfx.control.spreadsheet.GridBase
import org.controlsfx.control.spreadsheet.SpreadsheetCellType
import org.controlsfx.control.spreadsheet.SpreadsheetView
import spready.lisp.sexpr.Cell
import tornadofx.View
import tornadofx.action
import tornadofx.asObservable
import tornadofx.button
import tornadofx.hbox
import tornadofx.hboxConstraints
import tornadofx.singleAssign
import tornadofx.textfield
import tornadofx.vbox
import tornadofx.vgrow

class SheetView : View() {
    private var sheet: SpreadsheetView by singleAssign()
    private val model: SheetModel by inject()

    override val root = vbox {
        sheet = initSpreadSheet()

        sheet.addEventFilter(KeyEvent.KEY_RELEASED) {
            changeCell()
        }
        sheet.addEventFilter(MouseEvent.MOUSE_CLICKED) {
            changeCell()
        }

        sheet.vgrow = Priority.ALWAYS

        hbox {
            textfield(model.currentInputProperty) {
                addEventFilter(KeyEvent.KEY_RELEASED) {
                    if (it.code == KeyCode.ENTER) {
                        model.evalInput()
                    }
                }

                hboxConstraints {
                    hGrow = Priority.ALWAYS
                }
            }

            button("Add Row") {
                action {
                    val rowSize = sheet.grid.rowCount + 1
                    val colSize = sheet.grid.columnCount

                    changeSheetSize(rowSize, colSize)
                }
            }

            button("Add Column") {
                action {
                    val rowSize = sheet.grid.rowCount
                    val colSize = sheet.grid.columnCount + 1

                    changeSheetSize(rowSize, colSize)
                }
            }
        }

        add(sheet)
    }

    init {
        model.changedCellsProperty.addListener { _, _, list ->
            list.forEach {
                val result = model.allResults[it] ?: ""

                sheet.grid.setCellValue(it.row - 1, it.col - 1, result)
            }
        }
    }

    private fun createGrid(rowSize: Int, colSize: Int): Grid {
        val grid = GridBase(rowSize, colSize)

        val rows = (0 until rowSize).map { row ->
            (0 until colSize).map { col ->
                val text = model.allResults[Cell(row, col)] ?: ""

                SpreadsheetCellType.STRING.createCell(row, col, 1, 1, text)
            }.asObservable()
        }.asObservable()

        grid.setRows(rows)
        grid.columnHeaders.setAll((1..colSize).map(Int::toString))

        return grid
    }

    private fun initSpreadSheet(): SpreadsheetView {
        val sheet = SpreadsheetView(createGrid(10, 10))

        sheet.columns.forEach {
            it.setPrefWidth(150.0)
        }

        sheet.isEditable = false
        sheet.selectionModel.selectionMode = SelectionMode.SINGLE
        return sheet
    }

    private fun changeSheetSize(rowSize: Int, colSize: Int) {
        sheet.grid = createGrid(rowSize, colSize)

        sheet.columns.forEach {
            it.setPrefWidth(150.0)
        }
    }

    private fun changeCell() {
        val cell = sheet.selectionModel.focusedCell

        model.chooseCell(Cell(cell.row + 1, cell.column + 1))
    }
}
