package spready.spread

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Nested
import spready.lisp.sexpr.Cell
import kotlin.test.Test
import kotlin.test.assertEquals

class SpreadSerializerTest {
    @Test
    fun `serialize normal`() {
        val spread = Spread()

        spread[Cell(1, 1)] = "#2.1"
        spread[Cell(3, 2)] = "(+ #1.1 3)"
        spread[Cell(2, 1)] = "(+ 4 5)"

        val expected =
            """{"cells":{"1.1":"#2.1","3.2":"(+ #1.1 3)","2.1":"(+ 4 5)"}}"""

        assertEquals(expected, Json.encodeToString(spread))
    }

    @Nested
    inner class Deserialize {
        @Test
        fun `deserialize simple`() {
            val input =
                """
                {
                    "cells": {
                        "1.1": "3",
                        "2.1": "(+ #1.1 3)"
                    }
                }
                """.trimIndent()

            val spread = Json.decodeFromString<Spread>(input)

            val expected = mapOf(
                Cell(1, 1) to "3",
                Cell(2, 1) to "6"
            )

            assertEquals(expected, spread.allResults)
        }

        @Test
        fun `deserialize complex`() {
            val input =
                """
                {
                    "cells": {
                        "1.1": "3",
                        "2.1": "(+ #1.1 #3.1 #2.2 3)",
                        "4.1": "1",
                        "3.1": "(+ #2.2 1)",
                        "2.2": "2"
                    }
                }
                """.trimIndent()

            val spread = Json.decodeFromString<Spread>(input)

            val expected = mapOf(
                Cell(1, 1) to "3",
                Cell(2, 1) to "11",
                Cell(4, 1) to "1",
                Cell(3, 1) to "3",
                Cell(2, 2) to "2",
            )

            assertEquals(expected, spread.allResults)
        }
    }
}
