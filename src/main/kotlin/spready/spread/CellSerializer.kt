package spready.spread

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import spready.lisp.sexpr.Cell

object CellSerializer : KSerializer<Cell> {
    private val cellRegex = Regex("""(\d+)\.(\d+)""")

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Cell", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Cell) {
        encoder.encodeString("${value.row}.${value.col}")
    }

    override fun deserialize(decoder: Decoder): Cell {
        val input = decoder.decodeString()

        val (row, col) = cellRegex.matchEntire(input)?.destructured
            ?: throw SerializationException("Can't parse Cell!")

        return Cell(row.toInt(), col.toInt())
    }
}
