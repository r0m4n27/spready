package spready.ui.script

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import tornadofx.View
import tornadofx.action
import tornadofx.box
import tornadofx.button
import tornadofx.px
import tornadofx.style
import tornadofx.textarea
import tornadofx.vbox
import tornadofx.vgrow

/**
 * The view of one script
 *
 * Uses the [ScriptScope] to have multiple scripts open
 */
class ScriptView : View() {
    override val scope = super.scope as ScriptScope
    private val model = scope.model

    override val root = vbox {
        alignment = Pos.CENTER

        textarea(model.currentInputProperty) {
            vgrow = Priority.ALWAYS
        }

        button("Run Script").action(model::runScript)

        style {
            spacing = 10.px
            padding = box(5.px)
        }
    }
}
