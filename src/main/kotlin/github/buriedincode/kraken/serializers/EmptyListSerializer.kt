package github.buriedincode.kraken.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A custom serializer for handling nullable lists during serialization and deserialization.
 *
 * This serializer ensures that `null` values are deserialized as an empty list (`List<T>`), avoiding potential issues
 * with nullability when working with collections. It can be used for fields that are expected to be lists but may
 * sometimes be `null` in the input.
 *
 * @param T The type of elements contained within the list.
 * @property elementSerializer The serializer used for the individual elements in the list.
 *
 * ### Example Usage:
 * ```kotlin
 * @Serializable
 * data class Example(
 *     @Serializable(with = EmptyListSerializer::class)
 *     val items: List<String>
 * )
 *
 * val json = """{"items": null}"""
 * val result = Json.decodeFromString<Example>(json)
 * println(result.items) // Output: []
 * ```
 */
class EmptyListSerializer<T>(private val elementSerializer: KSerializer<T>) : KSerializer<List<T>> {
  /** The serial descriptor for the list, derived from the element serializer. */
  @OptIn(ExperimentalSerializationApi::class)
  override val descriptor: SerialDescriptor = listSerialDescriptor(elementSerializer.descriptor)

  /**
   * Deserializes a nullable list into a non-nullable list. If the input is `null`, it returns an empty list instead.
   *
   * @param decoder The decoder used to read the serialized input.
   * @return A list of type `T`, or an empty list if the input was `null`.
   */
  @OptIn(ExperimentalSerializationApi::class)
  override fun deserialize(decoder: Decoder): List<T> {
    return decoder.decodeNullableSerializableValue(ListSerializer(elementSerializer)) ?: emptyList()
  }

  /**
   * Serializes a list of type `T` into the desired output format.
   *
   * @param encoder The encoder used to write the serialized output.
   * @param value The list of items to be serialized.
   */
  override fun serialize(encoder: Encoder, value: List<T>) {
    encoder.encodeSerializableValue(ListSerializer(elementSerializer), value)
  }
}
