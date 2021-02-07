package spready.ui

import spready.ui.menu.MenuView
import spready.ui.sheet.SheetView
import spready.ui.status.StatusView
import tornadofx.View
import tornadofx.addClass
import tornadofx.borderpane

class MainView : View() {
    private val sheet: SheetView by inject()
    private val status: StatusView by inject()
    private val menu: MenuView by inject()

    override val root = borderpane {
        addClass(Style.default)

        top = menu.root
        center = sheet.root
        bottom = status.root
    }
}
