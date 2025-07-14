package github.buriedincode.kraken.schemas

import github.buriedincode.kraken.serializers.NullableStringSerializer
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.serialization.Serializable

/**
 * A generic response wrapper for paginated API results.
 *
 * @param T The type of items contained in the `results` list.
 * @property count The total number of items available in the resource.
 * @property next The URL for the next page of results, if available. `null` if there are no more pages.
 * @property previous The URL for the previous page of results, if available. `null` if on the first page.
 * @property results A list of items of type `T` returned by the API.
 */
@Serializable
data class ListResponse<T>(
  val count: Int,
  @Serializable(with = NullableStringSerializer::class) val next: String? = null,
  @Serializable(with = NullableStringSerializer::class) val previous: String? = null,
  val results: List<T> = listOf(),
)

/**
 * A data model representing a generic item.
 *
 * @property id The unique identifier of the generic item.
 * @property name The name fo the generic item.
 */
@Serializable data class GenericItem(val id: Long, val name: String)

/**
 * A data model representing a base resource.
 *
 * @property id The unique identifier of the base resource.
 * @property modified The date and time when the base resource was last modified.
 * @property name The name of the base resource.
 */
@OptIn(ExperimentalTime::class)
@Serializable
data class BaseResource(val id: Long, val modified: Instant, val name: String)
