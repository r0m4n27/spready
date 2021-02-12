package spready.ui.tabs

import tornadofx.FXEvent

/**
 * Signalises that a new script with [name] should be created
 */
class AddScriptEvent(val name: String) : FXEvent()
