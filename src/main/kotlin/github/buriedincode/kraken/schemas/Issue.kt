package github.buriedincode.kraken.schemas

import github.buriedincode.kraken.serializers.NullableStringSerializer
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * A data model representing a basic issue
 *
 * @property coverDate The cover date of the issue.
 * @property coverHash The hash value of the issue cover.
 * @property id The unique identifier of the issue.
 * @property image The image URL of the issue.
 * @property modified The date and time when the issue was last modified.
 * @property number The number of the issue.
 * @property series The basic series associated with the issue.
 * @property storeDate The store date of the issue.
 * @property title The name of the issue.
 */
@OptIn(ExperimentalSerializationApi::class, ExperimentalTime::class)
@Serializable
data class BasicIssue(
  val coverDate: LocalDate,
  @Serializable(with = NullableStringSerializer::class) val coverHash: String? = null,
  val id: Long,
  @Serializable(with = NullableStringSerializer::class) val image: String? = null,
  val modified: Instant,
  val number: String,
  val series: Series,
  val storeDate: LocalDate? = null,
  @JsonNames("issue") val title: String,
) {
  /**
   * A class representing a basic series with name, volume and year began.
   *
   * @property name The name of the series.
   * @property volume The volume of the series.
   * @property yearBegan The year the series began.
   */
  @Serializable data class Series(val name: String, val volume: Int, val yearBegan: Int)
}

/**
 * A data model representing an issue.
 *
 * @property alternativeNumber The alternative number of the issue.
 * @property arcs The arcs associated with the issue.
 * @property characters The characters featured in the issue.
 * @property comicvineId The Comic Vine ID of the issue.
 * @property coverDate The cover date of the issue.
 * @property coverHash The hash value of the issue cover.
 * @property credits The credits for the issue.
 * @property description The description of the issue.
 * @property focDate The final order cutoff date of the issue.
 * @property grandComicsDatabaseId The Grand Comics Database ID of the issue.
 * @property id The unique identifier of the issue.
 * @property image The image URL of the issue.
 * @property imprint The imprint of the issue or None.
 * @property isbn The International Standard Book Number (ISBN) of the issue.
 * @property modified The date and time when the issue was last modified.
 * @property number The number of the issue.
 * @property pageCount The number of pages in the issue.
 * @property price The price of the issue.
 * @property publisher The publisher of the issue.
 * @property rating The rating of the issue.
 * @property reprints The reprints of the issue.
 * @property resourceUrl The URL of the issue.
 * @property series The series to which the issue belongs.
 * @property sku The Stock Keeping Unit (SKU) of the issue.
 * @property storeDate The store date of the issue.
 * @property stories The titles of the stories in the issue.
 * @property teams The teams involved in the issue.
 * @property title The title of the issue.
 * @property universes The universes related to the issue.
 * @property upc The Universal Product Code (UPC) of the issue.
 * @property variants The variants of the issue.
 */
@OptIn(ExperimentalSerializationApi::class, ExperimentalTime::class)
@Serializable
data class Issue(
  @JsonNames("alt_number") @Serializable(with = NullableStringSerializer::class) val alternativeNumber: String? = null,
  val arcs: List<BaseResource> = emptyList(),
  val characters: List<BaseResource> = emptyList(),
  @JsonNames("cv_id") val comicvineId: Long? = null,
  val coverDate: LocalDate,
  @Serializable(with = NullableStringSerializer::class) val coverHash: String? = null,
  val credits: List<Credit> = emptyList(),
  @JsonNames("desc") @Serializable(with = NullableStringSerializer::class) val description: String? = null,
  val focDate: LocalDate? = null,
  @JsonNames("gcd_id") val grandComicsDatabaseId: Long? = null,
  val id: Long,
  @Serializable(with = NullableStringSerializer::class) val image: String? = null,
  val imprint: GenericItem? = null,
  @Serializable(with = NullableStringSerializer::class) val isbn: String? = null,
  val modified: Instant,
  val number: String,
  @JsonNames("page") val pageCount: Int? = null,
  val price: Double? = null,
  val publisher: GenericItem,
  val rating: GenericItem,
  val reprints: List<Reprint> = emptyList(),
  val resourceUrl: String,
  val series: Series,
  @Serializable(with = NullableStringSerializer::class) val sku: String? = null,
  val storeDate: LocalDate? = null,
  @JsonNames("name") val stories: List<String> = emptyList(),
  val teams: List<BaseResource> = emptyList(),
  @Serializable(with = NullableStringSerializer::class) val title: String? = null,
  val universes: List<BaseResource> = emptyList(),
  @Serializable(with = NullableStringSerializer::class) val upc: String? = null,
  val variants: List<Variant> = emptyList(),
) {
  /**
   * A class representing a credit with ID, creator and roles.
   *
   * @property creator The creator associated with the credit.
   * @property id The ID of the credit.
   * @property roles The list of roles the creator has in this credit.
   */
  @Serializable
  data class Credit(val creator: String, val id: Long, @JsonNames("role") val roles: List<GenericItem> = emptyList())

  /**
   * A data model representing a reprint.
   *
   * @property id The unique identifier of the reprint.
   * @property issue The issue being reprinted.
   */
  @OptIn(ExperimentalSerializationApi::class) @Serializable data class Reprint(val id: Long, val issue: String)

  /**
   * A data model representing an issue series.
   *
   * @property genres The genres associated with the series.
   * @property id The unique identifier of the series.
   * @property name The name of the series.
   * @property seriesType The type of series.
   * @property sortName The name used for sorting the series.
   * @property volume The volume number of the series.
   * @property yearBegan The year the series began.
   */
  @Serializable
  data class Series(
    val genres: List<GenericItem> = emptyList(),
    val id: Long,
    val name: String,
    val seriesType: GenericItem,
    val sortName: String,
    val volume: Int,
    val yearBegan: Int,
  )

  /**
   * A data model representing a variant cover.
   *
   * @property image The image URL of the variant.
   * @property name The name of the variant.
   * @property sku The Stock Keeping Unit (SKU) of the variant.
   * @property upc The Universal Product Code (UPC) of the variant.
   */
  @Serializable
  data class Variant(
    val image: String,
    @Serializable(with = NullableStringSerializer::class) val name: String? = null,
    @Serializable(with = NullableStringSerializer::class) val sku: String? = null,
    @Serializable(with = NullableStringSerializer::class) val upc: String? = null,
  )
}
