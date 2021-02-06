package spready.ui

import spready.ui.sheet.SheetView
import spready.ui.status.StatusView
import tornadofx.View
import tornadofx.addClass
import tornadofx.borderpane

class MainView : View() {
    private val sheet: SheetView by inject()
    private val status: StatusView by inject()

    override val root = borderpane {
        addClass(Style.default)

        center = sheet.root
        bottom = status.root
    }
}
