package github.buriedincode.kraken.schemas

import github.buriedincode.kraken.serializers.NullableStringSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * A data model representing an imprint.
 *
 * @property comicvineId The Comic Vine ID of the publisher.
 * @property description The description of the publisher.
 * @property founded The year the publisher was founded.
 * @property grandComicsDatabaseId The Grand Comics Database ID of the publisher.
 * @property id The unique identifier of the resource.
 * @property image The image URL of the publisher.
 * @property modified The date and time when the resource was last modified.
 * @property name The name of the resource.
 * @property publisher The generic item representing the publisher.
 * @property resourceUrl The URL of the publisher resource.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Imprint(
    @JsonNames("cv_id")
    val comicvineId: Long? = null,
    @Serializable(with = NullableStringSerializer::class)
    @JsonNames("desc")
    val description: String? = null,
    val founded: Int? = null,
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
