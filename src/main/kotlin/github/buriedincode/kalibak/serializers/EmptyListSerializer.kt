package github.buriedincode.kalibak.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class EmptyListSerializer<T>(private val elementSerializer: KSerializer<T>) : KSerializer<List<T>> {
    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = listSerialDescriptor(elementSerializer.descriptor)

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): List<T> {
        return decoder.decodeNullableSerializableValue(ListSerializer(elementSerializer)) ?: emptyList()
    }

    override fun serialize(encoder: Encoder, value: List<T>) {
        encoder.encodeSerializableValue(ListSerializer(elementSerializer), value)
    }
}
