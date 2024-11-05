package github.buriedincode.kalibak

import github.buriedincode.kalibak.Utils.log
import github.buriedincode.kalibak.schemas.Arc
import github.buriedincode.kalibak.schemas.BaseResource
import github.buriedincode.kalibak.schemas.BasicIssue
import github.buriedincode.kalibak.schemas.BasicSeries
import github.buriedincode.kalibak.schemas.Character
import github.buriedincode.kalibak.schemas.Creator
import github.buriedincode.kalibak.schemas.GenericItem
import github.buriedincode.kalibak.schemas.Issue
import github.buriedincode.kalibak.schemas.ListResponse
import github.buriedincode.kalibak.schemas.Publisher
import github.buriedincode.kalibak.schemas.Series
import github.buriedincode.kalibak.schemas.Team
import github.buriedincode.kalibak.schemas.Universe
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.Level
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpConnectTimeoutException
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.Base64
import kotlin.collections.joinToString
import kotlin.collections.plus
import kotlin.collections.sortedBy
import kotlin.jvm.Throws
import kotlin.ranges.until
import kotlin.text.isNotEmpty
import kotlin.text.toByteArray
import kotlin.text.toInt

class Metron(
    username: String,
    password: String,
    private val cache: SQLiteCache? = null,
    timeout: Duration = Duration.ofSeconds(30),
) {
    private val client: HttpClient = HttpClient
        .newBuilder()
        .followRedirects(HttpClient.Redirect.ALWAYS)
        .connectTimeout(timeout)
        .build()
    private val authorization: String = "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray())

    fun encodeURI(endpoint: String, params: Map<String, String> = emptyMap()): URI {
        val encodedParams = params.entries
            .sortedBy { it.key }
            .joinToString("&") { "${it.key}=${URLEncoder.encode(it.value, StandardCharsets.UTF_8)}" }
        return URI.create("$BASE_API$endpoint/${if (encodedParams.isNotEmpty()) "?$encodedParams" else ""}")
    }

    @Throws(ServiceException::class, AuthenticationException::class)
    private fun performGetRequest(uri: URI): String {
        try {
            @Suppress("ktlint:standard:max-line-length", "ktlint:standard:argument-list-wrapping")
            val request = HttpRequest
                .newBuilder()
                .uri(uri)
                .setHeader("Accept", "application/json")
                .setHeader("User-Agent", "Kalibak/0.1.1 (${System.getProperty("os.name")}/${System.getProperty("os.version")}; Kotlin/${KotlinVersion.CURRENT})")
                .setHeader("Authorization", this.authorization)
                .GET()
                .build()
            val response = this.client.send(request, HttpResponse.BodyHandlers.ofString())
            val level = when (response.statusCode()) {
                in 100 until 200 -> Level.WARN
                in 200 until 300 -> Level.DEBUG
                in 300 until 400 -> Level.INFO
                in 400 until 500 -> Level.WARN
                else -> Level.ERROR
            }
            LOGGER.log(level) { "GET: ${response.statusCode()} - $uri" }
            if (response.statusCode() == 200) {
                return response.body()
            }

            val content = JSON.parseToJsonElement(response.body()).jsonObject
            LOGGER.error { content.toString() }
            throw when (response.statusCode()) {
                401 -> AuthenticationException(content["detail"]?.jsonPrimitive?.content ?: "")
                404 -> ServiceException("Resource not found")
                else -> ServiceException(content["detail"]?.jsonPrimitive?.content ?: "")
            }
        } catch (ioe: IOException) {
            throw ServiceException(cause = ioe)
        } catch (hcte: HttpConnectTimeoutException) {
            throw ServiceException(cause = hcte)
        } catch (ie: InterruptedException) {
            throw ServiceException(cause = ie)
        } catch (se: SerializationException) {
            throw ServiceException(cause = se)
        }
    }

    @Throws(ServiceException::class, AuthenticationException::class)
    internal inline fun <reified T> getRequest(uri: URI): T {
        this.cache?.select(url = uri.toString())?.let {
            try {
                LOGGER.debug { "Using cached response for $uri" }
                return JSON.decodeFromString(it)
            } catch (se: SerializationException) {
                LOGGER.warn(se) { "Unable to deserialize cached response" }
                this.cache.delete(url = uri.toString())
            }
        }
        val response = this.performGetRequest(uri = uri)
        this.cache?.insert(url = uri.toString(), response = response)
        return try {
            JSON.decodeFromString(response)
        } catch (se: SerializationException) {
            throw ServiceException(cause = se)
        }
    }

    @Throws(ServiceException::class, AuthenticationException::class)
    private inline fun <reified T> fetchList(endpoint: String, params: Map<String, String>): List<T> {
        val resultList = mutableListOf<T>()
        var page = params.getOrDefault("page", "1").toInt()

        do {
            val uri = this.encodeURI(endpoint = endpoint, params = params + ("page" to page.toString()))
            val response = this.getRequest<ListResponse<T>>(uri = uri)
            resultList.addAll(response.results)
            page++
        } while (response.next != null)

        return resultList
    }

    @Throws(ServiceException::class, AuthenticationException::class)
    private inline fun <reified T> fetchItem(endpoint: String): T = this.getRequest<T>(uri = this.encodeURI(endpoint = endpoint))

    @Throws(ServiceException::class, AuthenticationException::class)
    fun listArcs(params: Map<String, String> = emptyMap()): List<BaseResource> {
        return this.fetchList<BaseResource>(endpoint = "/arc", params = params)
    }

    @Throws(ServiceException::class, AuthenticationException::class)
    fun getArc(id: Long): Arc = this.fetchItem<Arc>(endpoint = "/arc/$id")

    @Throws(ServiceException::class, AuthenticationException::class)
    fun listCharacters(params: Map<String, String> = emptyMap()): List<BaseResource> {
        return this.fetchList<BaseResource>(endpoint = "/character", params = params)
    }

    @Throws(ServiceException::class, AuthenticationException::class)
    fun getCharacter(id: Long): Character = this.fetchItem<Character>(endpoint = "/character/$id")

    @Throws(ServiceException::class, AuthenticationException::class)
    fun listCreators(params: Map<String, String> = emptyMap()): List<BaseResource> {
        return this.fetchList<BaseResource>(endpoint = "/creator", params = params)
    }

    @Throws(ServiceException::class, AuthenticationException::class)
    fun getCreator(id: Long): Creator = this.fetchItem<Creator>(endpoint = "/creator/$id")

    @Throws(ServiceException::class, AuthenticationException::class)
    fun listIssues(params: Map<String, String> = emptyMap()): List<BasicIssue> {
        return this.fetchList<BasicIssue>(endpoint = "/issue", params = params)
    }

    @Throws(ServiceException::class, AuthenticationException::class)
    fun getIssue(id: Long): Issue = this.fetchItem<Issue>(endpoint = "/issue/$id")

    @Throws(ServiceException::class, AuthenticationException::class)
    fun listPublishers(params: Map<String, String> = emptyMap()): List<BaseResource> {
        return this.fetchList<BaseResource>(endpoint = "/publisher", params = params)
    }

    @Throws(ServiceException::class, AuthenticationException::class)
    fun getPublisher(id: Long): Publisher = this.fetchItem<Publisher>(endpoint = "/publisher/$id")

    @Throws(ServiceException::class, AuthenticationException::class)
    fun listRoles(params: Map<String, String> = emptyMap()): List<GenericItem> {
        return this.fetchList<GenericItem>(endpoint = "/role", params = params)
    }

    @Throws(ServiceException::class, AuthenticationException::class)
    fun listSeries(params: Map<String, String> = emptyMap()): List<BasicSeries> {
        return this.fetchList<BasicSeries>(endpoint = "/series", params = params)
    }

    @Throws(ServiceException::class, AuthenticationException::class)
    fun getSeries(id: Long): Series = this.fetchItem<Series>(endpoint = "/series/$id")

    @Throws(ServiceException::class, AuthenticationException::class)
    fun listSeriesTypes(params: Map<String, String> = emptyMap()): List<GenericItem> {
        return this.fetchList<GenericItem>(endpoint = "/series_type", params = params)
    }

    @Throws(ServiceException::class, AuthenticationException::class)
    fun listTeams(params: Map<String, String> = emptyMap()): List<BaseResource> {
        return this.fetchList<BaseResource>(endpoint = "/team", params = params)
    }

    @Throws(ServiceException::class, AuthenticationException::class)
    fun getTeam(id: Long): Team = this.fetchItem<Team>(endpoint = "/team/$id")

    @Throws(ServiceException::class, AuthenticationException::class)
    fun listUniverses(params: Map<String, String> = emptyMap()): List<BaseResource> {
        return this.fetchList<BaseResource>(endpoint = "/universe", params = params)
    }

    @Throws(ServiceException::class, AuthenticationException::class)
    fun getUniverse(id: Long): Universe = this.fetchItem<Universe>(endpoint = "/universe/$id")

    companion object {
        @JvmStatic
        private val LOGGER = KotlinLogging.logger { }

        private const val BASE_API = "https://metron.cloud/api"

        @OptIn(ExperimentalSerializationApi::class)
        private val JSON: Json = Json {
            prettyPrint = true
            encodeDefaults = true
            namingStrategy = JsonNamingStrategy.SnakeCase
        }
    }
}
