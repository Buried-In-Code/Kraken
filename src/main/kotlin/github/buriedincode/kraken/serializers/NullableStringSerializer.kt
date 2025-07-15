package github.buriedincode.kraken.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A custom serializer for handling nullable strings in a standardized way.
 *
 * This serializer ensures that blank strings (e.g., `""`) are deserialized as `null`. It provides a convenient
 * mechanism to handle string fields that may either be blank or null in the input data.
 *
 * ### Example Usage:
 * ```kotlin
 * @Serializable
 * data class Example(
 *     @Serializable(with = NullableStringSerializer::class)
 *     val description: String?
 * )
 *
 * val json = """{"description": ""}"""
 * val result = Json.decodeFromString<Example>(json)
 * println(result.description) // Output: null
 *
 * val serialized = Json.encodeToString(Example(description = null))
 * println(serialized) // Output: {"description":null}
 * ```
 */
object NullableStringSerializer : KSerializer<String?> {
  /** The serial descriptor for a nullable string. */
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("NullableString", PrimitiveKind.STRING)

  /**
   * Deserializes a string value, converting blank strings to `null`.
   *
   * @param decoder The decoder used to read the serialized input.
   * @return A nullable string, where blank strings are represented as `null`.
   */
  override fun deserialize(decoder: Decoder): String? {
    val value = decoder.decodeString()
    return value.ifBlank { null }
  }

  /**
   * Serializes a nullable string.
   *
   * @param encoder The encoder used to write the serialized output.
   * @param value The nullable string value to be serialized.
   */
  @OptIn(ExperimentalSerializationApi::class)
  override fun serialize(encoder: Encoder, value: String?) {
    if (value.isNullOrBlank()) {
      encoder.encodeNull()
    } else {
      encoder.encodeString(value)
    }
  }
}
