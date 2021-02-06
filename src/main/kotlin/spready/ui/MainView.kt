package spready.ui

import spready.ui.sheet.SheetView
import tornadofx.View
import tornadofx.addClass
import tornadofx.borderpane

class MainView : View() {
    private val sheetView: SheetView by inject()

    override val root = borderpane {
        addClass(Style.default)

        center = sheetView.root
    }
}
