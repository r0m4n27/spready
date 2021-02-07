package spready.spread

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import spready.lisp.sexpr.Cell

object SpreadSerializer : KSerializer<Spread> {
    override val descriptor: SerialDescriptor
        get() = SpreadSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Spread) {
        val surrogate = SpreadSurrogate(value.allInputs, value.allScripts)
        encoder.encodeSerializableValue(SpreadSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Spread {
        val surrogate = decoder.decodeSerializableValue(SpreadSurrogate.serializer())
        val spread = Spread()

        surrogate.scripts.forEach(spread::setScript)
        surrogate.cells.forEach(spread::setCell)

        return spread
    }
}

@Serializable
@SerialName("Spread")
private class SpreadSurrogate(
    val cells: Map<Cell, String>,
    val scripts: Map<String, String>
)
