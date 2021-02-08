package spready.ui.script

import tornadofx.Scope

class ScriptScope(scriptName: String) : Scope() {
    val model = ScriptModel(scriptName)
}
