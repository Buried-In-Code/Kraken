package github.buriedincode.kalibak.schemas

import github.buriedincode.kalibak.serializers.NullableStringSerializer
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class BasicIssue(
    val coverDate: LocalDate,
    @Serializable(with = NullableStringSerializer::class)
    val coverHash: String? = null,
    val id: Long,
    @Serializable(with = NullableStringSerializer::class)
    val image: String? = null,
    val modified: Instant,
    @JsonNames("issue")
    val name: String,
    val number: String,
    val series: Series,
    val storeDate: LocalDate? = null,
) {
    @Serializable
    data class Series(
        val name: String,
        val volume: Int,
        val yearBegan: Int,
    )
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Issue(
    val arcs: List<BaseResource> = emptyList(),
    val characters: List<BaseResource> = emptyList(),
    @JsonNames("cv_id")
    val comicvineId: Long? = null,
    val coverDate: LocalDate,
    @Serializable(with = NullableStringSerializer::class)
    val coverHash: String? = null,
    val credits: List<Credit> = emptyList(),
    @JsonNames("desc")
    @Serializable(with = NullableStringSerializer::class)
    val description: String? = null,
    val id: Long,
    @Serializable(with = NullableStringSerializer::class)
    val image: String? = null,
    val imprint: GenericItem? = null,
    @Serializable(with = NullableStringSerializer::class)
    val isbn: String? = null,
    val modified: Instant,
    val number: String,
    @JsonNames("page")
    val pageCount: Int? = null,
    val price: Double? = null,
    val publisher: GenericItem,
    val rating: GenericItem,
    val reprints: List<Reprint> = emptyList(),
    val resourceUrl: String,
    val series: Series,
    @Serializable(with = NullableStringSerializer::class)
    val sku: String? = null,
    val storeDate: LocalDate? = null,
    @JsonNames("name")
    val stories: List<String> = emptyList(),
    val teams: List<BaseResource> = emptyList(),
    @Serializable(with = NullableStringSerializer::class)
    val title: String? = null,
    val universes: List<BaseResource> = emptyList(),
    @Serializable(with = NullableStringSerializer::class)
    val upc: String? = null,
    val variants: List<Variant> = emptyList(),
) {
    @Serializable
    data class Credit(
        val creator: String,
        val id: Long,
        val role: List<GenericItem> = emptyList(),
    )

    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    data class Reprint(
        val id: Long,
        @JsonNames("issue")
        val name: String,
    )

    @Serializable
    data class Series(
        val genres: List<GenericItem> = emptyList(),
        val id: Long,
        val name: String,
        val seriesType: GenericItem,
        val sortName: String,
        val volume: Int,
    )

    @Serializable
    data class Variant(
        val image: String,
        @Serializable(with = NullableStringSerializer::class)
        val name: String? = null,
        @Serializable(with = NullableStringSerializer::class)
        val sku: String? = null,
        @Serializable(with = NullableStringSerializer::class)
        val upc: String? = null,
    )
}
