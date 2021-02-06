package spready.ui

import tornadofx.Stylesheet
import tornadofx.cssclass
import tornadofx.px

class Style : Stylesheet() {
    companion object {
        val default by cssclass()
    }

    init {
        default {
            fontSize = 14.px
        }
    }
}
