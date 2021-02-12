package spready.ui.menu

import javafx.stage.FileChooser
import tornadofx.FileChooserMode
import tornadofx.ViewModel
import tornadofx.chooseFile
import java.io.File

class MenuModel : ViewModel() {

    private var lastFile: File? = null

    /**
     * Chooses a json file to open
     *
     * Fires a [SpreadFileEvent]
     * and saves the lastFile
     */
    fun openFile() {
        val files = chooseFile(
            "Select file to open",
            arrayOf(FileChooser.ExtensionFilter("Json", "*.json")),
            mode = FileChooserMode.Single
        )

        if (files.isNotEmpty()) {
            lastFile = files.first()

            fire(SpreadFileEvent(Open, files.first()))
        }
    }

    /**
     * Saves the file
     *
     * If [lastFile] is null it will use [saveFileAs]
     * otherwise it uses the [lastFile]
     */
    fun saveFile() {
        lastFile.let {
            if (it == null) {
                saveFileAs()
            } else {
                fire(SpreadFileEvent(Save, it))
            }
        }
    }

    /**
     * Chooses a json file to save the spread
     *
     * Saves the file to [lastFile] to use later
     */
    fun saveFileAs() {
        val files = chooseFile(
            "Select file to save",
            arrayOf(FileChooser.ExtensionFilter("Json", "*.json")),
            mode = FileChooserMode.Save
        )

        if (files.isNotEmpty()) {
            lastFile = files.first()

            fire(SpreadFileEvent(Save, files.first()))
        }
    }
}
