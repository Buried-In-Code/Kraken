package github.buriedincode.kraken.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object NullableStringSerializer : KSerializer<String?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("NullableString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String? {
        val value = decoder.decodeString()
        return value.ifBlank { null }
    }

    override fun serialize(encoder: Encoder, value: String?) {
        encoder.encodeString(value ?: "")
    }
}
