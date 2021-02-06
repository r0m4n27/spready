package spready.ui

import tornadofx.App
import tornadofx.reloadStylesheetsOnFocus

class SpreadyApp : App(MainView::class, Style::class) {
    init {
        reloadStylesheetsOnFocus()
    }
}
