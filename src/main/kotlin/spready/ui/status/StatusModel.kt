package spready.ui.status

import javafx.beans.property.SimpleStringProperty
import spready.ui.sheet.Err
import spready.ui.sheet.EvalStatusEvent
import spready.ui.sheet.Ok
import tornadofx.ViewModel
import tornadofx.getValue
import tornadofx.setValue

class StatusModel : ViewModel() {
    val lastErrorProperty = SimpleStringProperty("")
    private var lastError by lastErrorProperty

    init {
        subscribe<EvalStatusEvent> {
            lastError = when (it.result) {
                Ok -> ""
                is Err -> it.result.message
            }
        }
    }
}
