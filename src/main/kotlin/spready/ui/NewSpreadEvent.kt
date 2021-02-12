package spready.ui

import tornadofx.FXEvent

/**
 * Signalises that a new Spread was created
 *
 * @constructor Contains the [maxRow] and [maxCol] of the new Spread
 */
class NewSpreadEvent(val maxRow: Int, val maxCol: Int) : FXEvent()
