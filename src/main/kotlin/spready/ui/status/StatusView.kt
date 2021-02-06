package spready.ui.status

import javafx.scene.paint.Color
import tornadofx.View
import tornadofx.hbox
import tornadofx.label

class StatusView : View() {
    private val model: StatusModel by inject()

    override val root = hbox {
        label(model.lastErrorProperty) {
            textFill = Color.RED
        }
    }
}
