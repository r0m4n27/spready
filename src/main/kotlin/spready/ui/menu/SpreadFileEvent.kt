package spready.ui.menu

import tornadofx.FXEvent
import java.io.File

/**
 * Signalises that a new [File] was chosen
 *
 * FileAction can either be [Save] or [Open]
 */
class SpreadFileEvent(val action: FileAction, val file: File) : FXEvent()

sealed class FileAction

object Save : FileAction()

object Open : FileAction()
