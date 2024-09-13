package github.buriedincode.kalibak.schemas

import github.buriedincode.kalibak.serializers.NullableStringSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class ListResponse<T>(
    val count: Int,
    @Serializable(with = NullableStringSerializer::class)
    val next: String? = null,
    @Serializable(with = NullableStringSerializer::class)
    val previous: String? = null,
    val results: List<T> = listOf(),
)

@Serializable
data class GenericItem(
    val id: Long,
    val name: String,
)

@Serializable
data class BaseResource(
    val id: Long,
    val modified: Instant,
    val name: String,
)
