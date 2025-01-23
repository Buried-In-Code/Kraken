package github.buriedincode.kraken.schemas

import github.buriedincode.kraken.serializers.NullableStringSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

/**
 * A data model representing a team.
 *
 * @property comicvineId The Comic Vine ID of the team.
 * @property creators The creators of the team.
 * @property description The description of the team.
 * @property grandComicsDatabaseId The Grand Comics Database ID of the team.
 * @property id The unique identifier of the resource.
 * @property image The image URL of the team.
 * @property modified The date and time when the resource was last modified.
 * @property name The name of the resource.
 * @property resourceUrl The URL of the team.
 * @property universes The universes the team is associated with.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Team(
    @JsonNames("cv_id")
    val comicvineId: Long? = null,
    val creators: List<BaseResource> = emptyList(),
    @Serializable(with = NullableStringSerializer::class)
    @JsonNames("desc")
    val description: String? = null,
    @JsonNames("gcd_id")
    val grandComicsDatabaseId: Long? = null,
    val id: Long,
    @Serializable(with = NullableStringSerializer::class)
    val image: String? = null,
    val modified: Instant,
    val name: String,
    val resourceUrl: String,
    val universes: List<BaseResource> = emptyList(),
)
