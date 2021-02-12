package spready.ui.status

import tornadofx.FXEvent

/**
 * Signalises that something was changed in the model
 *
 * If it was successful the [result] will be [Ok]
 *
 * If something wrong happened the [result] will be [Err] with a message
 */
class StatusEvent(val result: Result) : FXEvent()

sealed class Result

object Ok : Result()
data class Err(val message: String) : Result()
