package spready.ui.status

import tornadofx.FXEvent

class StatusEvent(val result: Result) : FXEvent()

sealed class Result

object Ok : Result()
data class Err(val message: String) : Result()
