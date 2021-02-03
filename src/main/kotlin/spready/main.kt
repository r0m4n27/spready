package spready

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import spready.lisp.sexpr.Cell
import spready.spread.Spread

fun main() {
    val spread = Spread()
    spread[Cell(2, 1)] = "#1.1"
    spread[Cell(1, 1)] = "1"
    println(spread.allInputs)
    println(spread.allResults)

    val json = Json.encodeToString(spread)

    val newSpread = Json.decodeFromString<Spread>(json)
    println(newSpread.allInputs)
    println(newSpread.allResults)
}
