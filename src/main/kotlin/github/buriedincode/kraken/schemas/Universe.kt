package github.buriedincode.kraken.schemas

import github.buriedincode.kraken.serializers.NullableStringSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * A data model representing a universe.
 *
 * @property description The description of the universe.
 * @property designation The designation fo the universe.
 * @property grandComicsDatabaseId The Grand Comics Database ID of the universe.
 * @property id The unique identifier of the resource.
 * @property image The image URL of the universe.
 * @property modified The date and time when the resource was last modified.
 * @property name The name of the resource.
 * @property publisher The publisher of the universe.
 * @property resourceUrl The URL of the universe.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Universe(
    @Serializable(with = NullableStringSerializer::class)
    @JsonNames("desc")
    val description: String? = null,
    @Serializable(with = NullableStringSerializer::class)
    val designation: String? = null,
    @JsonNames("gcd_id")
    val grandComicsDatabaseId: Long? = null,
    val id: Long,
    @Serializable(with = NullableStringSerializer::class)
    val image: String? = null,
    val modified: Instant,
    val name: String,
    val publisher: GenericItem,
    val resourceUrl: String,
)
