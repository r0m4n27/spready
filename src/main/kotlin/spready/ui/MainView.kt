package spready.ui

import org.controlsfx.control.spreadsheet.SpreadsheetView
import tornadofx.View
import tornadofx.vbox

class MainView : View() {
    override val root = vbox {
        SpreadsheetView()
    }
}
