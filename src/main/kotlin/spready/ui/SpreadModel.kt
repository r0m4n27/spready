package spready.ui

import javafx.beans.property.SimpleObjectProperty
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import spready.spread.Spread
import spready.ui.menu.Open
import spready.ui.menu.Save
import spready.ui.menu.SpreadFileEvent
import spready.ui.status.Err
import spready.ui.status.Ok
import spready.ui.status.StatusEvent
import tornadofx.ViewModel
import tornadofx.getValue
import tornadofx.setValue
import java.io.File

/**
 * Holds a [SimpleObjectProperty] with a [Spread] that can be accessed by every other model
 *
 * Subscribes to [SpreadFileEvent] to update the [Spread]
 */
class SpreadModel : ViewModel() {
    val spreadProperty = SimpleObjectProperty(Spread())
    var spread: Spread by spreadProperty

    init {
        subscribe<SpreadFileEvent> {
            when (it.action) {
                Open -> loadFromFile(it.file)
                Save -> saveToFile(it.file)
            }
        }
    }

    /**
     * Loads a new [Spread] from the [file]
     *
     * Fires a [StatusEvent] at the end
     *
     * Fires a [NewSpreadEvent] when the new spread could be loaded
     */
    private fun loadFromFile(file: File) {
        val result = try {
            spread = Json.decodeFromString(file.readText())

            val maxSizes = spread.allResults.keys.fold(Pair(10, 10)) { acc, cell ->
                var newPair = acc

                if (cell.row > newPair.first) {
                    newPair = Pair(cell.row, newPair.second)
                }

                if (cell.col > newPair.second) {
                    newPair = Pair(newPair.first, cell.col)
                }

                newPair
            }

            fire(NewSpreadEvent(maxSizes.first, maxSizes.second))
            Ok
        } catch (ex: SerializationException) {
            Err(ex.message ?: "Can't open file!")
        }

        fire(StatusEvent(result))
    }

    /**
     * Saves the spread to the [file]
     *
     * Fires a [StatusEvent] wit [Ok] at the end
     */
    private fun saveToFile(file: File) {
        val text = Json.encodeToString(spread)
        file.writeText(text)

        fire(StatusEvent(Ok))
    }
}
