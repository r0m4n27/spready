package spready.ui.status

import javafx.beans.property.SimpleStringProperty
import tornadofx.ViewModel
import tornadofx.getValue
import tornadofx.setValue

class StatusModel : ViewModel() {
    val lastErrorProperty = SimpleStringProperty("")
    private var lastError by lastErrorProperty

    init {
        subscribe<StatusEvent> {
            lastError = when (it.result) {
                Ok -> ""
                is Err -> it.result.message
            }
        }
    }
}
