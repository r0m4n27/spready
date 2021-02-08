package spready.ui.tabs

import spready.ui.SpreadModel
import spready.ui.status.Err
import spready.ui.status.Ok
import spready.ui.status.Result
import tornadofx.ViewModel
import tornadofx.getValue

class TabModel : ViewModel() {
    private val spreadModel: SpreadModel by inject()

    val spreadProperty = spreadModel.spreadProperty
    private val spread by spreadProperty

    fun initializeScript(name: String): Result {
        return if (name in spread.allScripts) {
            Err("Script already exists!")
        } else {
            spread.setScript(name, "")
            Ok
        }
    }
}
