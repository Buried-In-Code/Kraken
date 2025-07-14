package github.buriedincode.kraken.schemas

import github.buriedincode.kraken.serializers.EmptyListSerializer
import github.buriedincode.kraken.serializers.NullableStringSerializer
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * A data model representing a character.
 *
 * @property alias The aliases of the character.
 * @property comicvineId The Comic Vine ID of the character.
 * @property creators The creators of the character.
 * @property description The description of the character.
 * @property grandComicsDatabaseId The Grand Comics Database ID of the character
 * @property id The unique identifier of the resource.
 * @property image The image URL of the character.
 * @property modified The date and time when the resource was last modified.
 * @property name The name of the resource.
 * @property resourceUrl The URL of the character resource.
 * @property teams The teams the character belongs to.
 * @property universes The universes the character is associated with.
 */
@OptIn(ExperimentalSerializationApi::class, ExperimentalTime::class)
@Serializable
data class Character(
  @Serializable(with = EmptyListSerializer::class) val alias: List<String> = emptyList(),
  @JsonNames("cv_id") val comicvineId: Long? = null,
  val creators: List<BaseResource> = emptyList(),
  @Serializable(with = NullableStringSerializer::class) @JsonNames("desc") val description: String? = null,
  @JsonNames("gcd_id") val grandComicsDatabaseId: Long? = null,
  val id: Long,
  @Serializable(with = NullableStringSerializer::class) val image: String? = null,
  val modified: Instant,
  val name: String,
  val resourceUrl: String,
  val teams: List<BaseResource> = emptyList(),
  val universes: List<BaseResource> = emptyList(),
)
