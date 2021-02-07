package spready.ui.menu

import tornadofx.View
import tornadofx.action
import tornadofx.item
import tornadofx.menu
import tornadofx.menubar

class MenuView : View() {
    private val model: MenuModel by inject()

    override val root = menubar {
        menu("File") {
            item("Open", "Shortcut+O") {
                action(model::openFile)
            }

            item("Save", "Shortcut+S") {
                action(model::saveFile)
            }
            item("Save As", "Shortcut+Shift+S") {
                action(model::saveFileAs)
            }
        }
    }
}
