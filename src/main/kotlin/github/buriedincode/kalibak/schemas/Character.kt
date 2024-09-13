package github.buriedincode.kalibak.schemas

import github.buriedincode.kalibak.serializers.NullableStringSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Character(
    val alias: List<String> = emptyList(),
    @JsonNames("cv_id")
    val comicvineId: Long? = null,
    val creators: List<BaseResource> = emptyList(),
    @Serializable(with = NullableStringSerializer::class)
    @JsonNames("desc")
    val description: String? = null,
    val id: Long,
    @Serializable(with = NullableStringSerializer::class)
    val image: String? = null,
    val modified: Instant,
    val name: String,
    val resourceUrl: String,
    val teams: List<BaseResource> = emptyList(),
    val universes: List<BaseResource> = emptyList(),
)
