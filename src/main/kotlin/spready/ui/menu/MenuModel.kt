package spready.ui.menu

import javafx.stage.FileChooser
import tornadofx.FileChooserMode
import tornadofx.ViewModel
import tornadofx.chooseFile
import java.io.File

class MenuModel : ViewModel() {

    private var lastFile: File? = null

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

    fun saveFile() {
        lastFile.let {
            if (it == null) {
                saveFileAs()
            } else {
                fire(SpreadFileEvent(Save, it))
            }
        }
    }

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
