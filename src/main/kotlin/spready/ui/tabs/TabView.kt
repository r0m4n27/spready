package spready.ui.tabs

import javafx.scene.control.TabPane
import spready.ui.script.ScriptScope
import spready.ui.script.ScriptView
import spready.ui.sheet.SheetView
import spready.ui.status.StatusEvent
import tornadofx.View
import tornadofx.find
import tornadofx.replaceWith
import tornadofx.tab
import tornadofx.tabpane

class TabView : View() {
    private val model: TabModel by inject()

    override val root = tabpane {
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

        tab<SheetView>()
    }

    init {
        subscribe<AddScriptEvent> {
            newScript(it.name)
        }

        model.spreadProperty.addListener { _, _, spread ->
            root.replaceWith(
                tabpane {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

                    tab<SheetView>()

                    spread.allScripts.forEach {
                        val scope = ScriptScope(it.key)
                        tab(it.key, find(ScriptView::class, scope).root)
                    }
                }
            )
        }
    }

    private fun newScript(name: String) {
        val result = model.initializeScript(name)

        val scope = ScriptScope(name)

        root.tab(name, find(ScriptView::class, scope).root)

        fire(StatusEvent(result))
    }
}
