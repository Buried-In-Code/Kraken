package github.buriedincode.kraken.schemas

import github.buriedincode.kraken.serializers.NullableStringSerializer
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * A data model representing a creator.
 *
 * @property alias The aliases of the creator.
 * @property birth The birthdate of the creator.
 * @property comicvineId The Comic Vine ID of the creator.
 * @property death The death date of the creator.
 * @property description The description of the creator.
 * @property grandComicsDatabaseId The Grand Comics Database ID of the creator.
 * @property id The unique identifier of the resource.
 * @property image The image URL of the creator.
 * @property modified The date and time when the resource was last modified.
 * @property name The name of the resource.
 * @property resourceUrl The URL of the creator resource.
 */
@OptIn(ExperimentalSerializationApi::class, ExperimentalTime::class)
@Serializable
data class Creator(
  val alias: List<String> = emptyList(),
  val birth: LocalDate? = null,
  @JsonNames("cv_id") val comicvineId: Long? = null,
  val death: LocalDate? = null,
  @Serializable(with = NullableStringSerializer::class) @JsonNames("desc") val description: String? = null,
  @JsonNames("gcd_id") val grandComicsDatabaseId: Long? = null,
  val id: Long,
  @Serializable(with = NullableStringSerializer::class) val image: String? = null,
  val modified: Instant,
  val name: String,
  val resourceUrl: String,
)
