package github.buriedincode.kalibak.schemas

import github.buriedincode.kalibak.serializers.NullableStringSerializer
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Creator(
    val alias: List<String> = emptyList(),
    val birth: LocalDate? = null,
    @JsonNames("cv_id")
    val comicvineId: Long? = null,
    val death: LocalDate? = null,
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
)
