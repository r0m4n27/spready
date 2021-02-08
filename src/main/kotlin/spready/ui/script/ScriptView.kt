package spready.ui.script

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import tornadofx.View
import tornadofx.action
import tornadofx.button
import tornadofx.textarea
import tornadofx.vbox
import tornadofx.vgrow

class ScriptView : View() {
    override val scope = super.scope as ScriptScope
    private val model = scope.model

    override val root = vbox {
        alignment = Pos.CENTER

        textarea(model.currentInputProperty) {
            vgrow = Priority.ALWAYS
        }

        button("Run Script").action(model::runScript)
    }
}
