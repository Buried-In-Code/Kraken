package github.buriedincode.kraken.schemas

import github.buriedincode.kraken.serializers.EmptyListSerializer
import github.buriedincode.kraken.serializers.NullableStringSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Character(
    @Serializable(with = EmptyListSerializer::class)
    val alias: List<String> = emptyList(),
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
    val teams: List<BaseResource> = emptyList(),
    val universes: List<BaseResource> = emptyList(),
)
