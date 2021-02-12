package spready.ui.script

import javafx.beans.property.SimpleStringProperty
import spready.lisp.EvalException
import spready.ui.SpreadModel
import spready.ui.status.Err
import spready.ui.status.Ok
import spready.ui.status.StatusEvent
import tornadofx.FX
import tornadofx.ViewModel
import tornadofx.getValue
import tornadofx.setValue

/**
 * Holds the current input of the specific script
 *
 * The [spreadModel] is injected by FX.defaultScope
 * to acess the global [SpreadModel] and not to create another one
 */
class ScriptModel(private val name: String) : ViewModel() {

    // All Models should have the same SpreadModel
    private val spreadModel: SpreadModel by inject(FX.defaultScope)
    private val spread by spreadModel.spreadProperty

    val currentInputProperty = SimpleStringProperty(
        spread.allScripts[name]
            ?: throw IllegalArgumentException("Can't find name!")
    )
    private var currentInput by currentInputProperty

    /**
     * Evaluated the script
     *
     * Fires [StatusEvent] with the result of the evaluation
     */
    fun runScript() {
        val result = try {
            spread.setScript(name, currentInput)
            Ok
        } catch (ex: EvalException) {
            Err(ex.message)
        }

        fire(StatusEvent(result))
    }
}
