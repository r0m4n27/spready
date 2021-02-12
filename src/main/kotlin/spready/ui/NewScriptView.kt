package spready.ui

import javafx.beans.property.SimpleStringProperty
import spready.ui.tabs.AddScriptEvent
import tornadofx.Fragment
import tornadofx.action
import tornadofx.button
import tornadofx.fieldset
import tornadofx.form
import tornadofx.getValue
import tornadofx.px
import tornadofx.style
import tornadofx.textfield

/**
 * Shows a Modal to add a new script
 *
 * Fires [AddScriptEvent] when a new name is submitted
 */
class NewScriptView : Fragment() {
    private val newNameProperty = SimpleStringProperty("")
    private val newName by newNameProperty

    override val root = form {
        fieldset("New name of the script") {
            textfield(newNameProperty)

            button("Ok") {
                action {
                    if (newName != "") {
                        fire(AddScriptEvent(newName))
                    }

                    close()
                }
            }

            style {
                spacing = 10.px
            }
        }
    }
}
