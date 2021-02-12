package spready.ui

import javafx.scene.layout.Priority
import spready.ui.menu.MenuView
import spready.ui.status.StatusView
import spready.ui.tabs.TabView
import tornadofx.View
import tornadofx.action
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.box
import tornadofx.button
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.px
import tornadofx.style

/**
 * Has the main components in a borderpane
 *
 * Also has a button to open [NewScriptView]
 */
class MainView : View() {
    private val status: StatusView by inject()

    override val root = borderpane {
        addClass(Style.default)

        top<MenuView>()
        center<TabView>()

        bottom = hbox {
            status.root.hgrow = Priority.ALWAYS
            add(status)

            button("Add Script") {
                action {
                    find<NewScriptView>().openModal()
                }
            }

            style {
                padding = box(10.px)
            }
        }
    }
}
