package github.buriedincode.kraken.schemas

import github.buriedincode.kraken.serializers.NullableStringSerializer
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * A data model representing an arc.
 *
 * @property comicvineId The Comic Vine ID of the arc.
 * @property description The description of the arc.
 * @property grandComicsDatabaseId The Grand Comics Database ID of the arc.
 * @property id The unique identifier of the resource.
 * @property image The image URL of the arc.
 * @property modified The date and time when the resource was last modified.
 * @property name The name of the resource.
 * @property resourceUrl The URL of the arc resource.
 */
@OptIn(ExperimentalSerializationApi::class, ExperimentalTime::class)
@Serializable
data class Arc(
  @JsonNames("cv_id") val comicvineId: Long? = null,
  @Serializable(with = NullableStringSerializer::class) @JsonNames("desc") val description: String? = null,
  @JsonNames("gcd_id") val grandComicsDatabaseId: Long? = null,
  val id: Long,
  @Serializable(with = NullableStringSerializer::class) val image: String? = null,
  val modified: Instant,
  val name: String,
  val resourceUrl: String,
)
