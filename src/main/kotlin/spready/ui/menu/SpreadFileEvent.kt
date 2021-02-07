package spready.ui.menu

import tornadofx.FXEvent
import java.io.File

class SpreadFileEvent(val action: FileAction, val file: File) : FXEvent()

sealed class FileAction

object Save : FileAction()

object Open : FileAction()
