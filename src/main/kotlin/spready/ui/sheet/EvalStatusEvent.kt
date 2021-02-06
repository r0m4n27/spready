package spready.ui.sheet

import tornadofx.FXEvent

class EvalStatusEvent(val result: Result) : FXEvent()

sealed class Result

object Ok : Result()
data class Err(val message: String) : Result()
