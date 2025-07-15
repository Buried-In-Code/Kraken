package github.buriedincode.kraken.schemas

import github.buriedincode.kraken.serializers.NullableStringSerializer
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * A data model representing a basic series.
 *
 * @property id The unique identifier of the series.
 * @property issueCount The number of issues in the series.
 * @property modified The date and time when the series was last modified.
 * @property name The name of the series.
 * @property volume The volume number of the series.
 * @property yearBegan The year the series began.
 */
@OptIn(ExperimentalSerializationApi::class, ExperimentalTime::class)
@Serializable
data class BasicSeries(
  val id: Long,
  val issueCount: Int,
  val modified: Instant,
  @JsonNames("series") val name: String,
  val volume: Int,
  val yearBegan: Int,
)

/**
 * A data model representing a series.
 *
 * @property associated The associated series.
 * @property comicvineId The Comic Vine ID of the series.
 * @property description The description of the series.
 * @property genres The genres associated with the series.
 * @property grandComicsDatabaseId The Grand Comics Database ID of the series.
 * @property id The unique identifier of the series.
 * @property imprint The imprint of the series or None.
 * @property issueCount The number of issues in the series.
 * @property modified The date and time when the series was last modified.
 * @property name The name of the series.
 * @property publisher The publisher of the series.
 * @property resourceUrl The URL of the series resource.
 * @property seriesType The type of series.
 * @property status The status of the series.
 * @property sortName The name used for sorting the series.
 * @property volume The volume number of the series.
 * @property yearBegan The year the series began.
 * @property yearEnd The year the series ended.
 */
@OptIn(ExperimentalSerializationApi::class, ExperimentalTime::class)
@Serializable
data class Series(
  val associated: List<Associated> = emptyList(),
  @JsonNames("cv_id") val comicvineId: Long? = null,
  @Serializable(with = NullableStringSerializer::class) @JsonNames("desc") val description: String? = null,
  val genres: List<GenericItem> = emptyList(),
  @JsonNames("gcd_id") val grandComicsDatabaseId: Long? = null,
  val id: Long,
  val imprint: GenericItem? = null,
  val issueCount: Int,
  val modified: Instant,
  val name: String,
  val publisher: GenericItem,
  val resourceUrl: String,
  val seriesType: GenericItem,
  val status: String,
  val sortName: String,
  val volume: Int,
  val yearBegan: Int,
  val yearEnd: Int? = null,
) {
  /**
   * A data model representing an associated series.
   *
   * @property id The unique identifier of the associated series.
   * @property name The name of the associated series.
   */
  @OptIn(ExperimentalSerializationApi::class)
  @Serializable
  data class Associated(val id: Long, @JsonNames("series") val name: String)
}
