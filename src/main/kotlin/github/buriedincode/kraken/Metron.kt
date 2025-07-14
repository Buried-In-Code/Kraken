package github.buriedincode.kraken

import github.buriedincode.kraken.schemas.Arc
import github.buriedincode.kraken.schemas.BaseResource
import github.buriedincode.kraken.schemas.BasicIssue
import github.buriedincode.kraken.schemas.BasicSeries
import github.buriedincode.kraken.schemas.Character
import github.buriedincode.kraken.schemas.Creator
import github.buriedincode.kraken.schemas.GenericItem
import github.buriedincode.kraken.schemas.Imprint
import github.buriedincode.kraken.schemas.Issue
import github.buriedincode.kraken.schemas.ListResponse
import github.buriedincode.kraken.schemas.Publisher
import github.buriedincode.kraken.schemas.Series
import github.buriedincode.kraken.schemas.Team
import github.buriedincode.kraken.schemas.Universe
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.Level
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
import kotlin.jvm.optionals.getOrNull
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * A client for interacting with the Metron API.
 *
 * @param username The username for authentication with the Metron API.
 * @param password The password for authentication with the Metron API.
 * @param cache An optional [SQLiteCache] instance for caching API responses. Defaults to `null` (no caching).
 * @param timeout The maximum duration for HTTP connections. Defaults to 30 seconds.
 * @param maxRetries The maximum number of retries for requests that fail due to rate-limiting. Defaults to 5.
 * @property cache The optional cache instance used for storing and retrieving cached API responses.
 * @property maxRetries The maximum number of retries allowed for rate-limited requests.
 * @constructor Creates a new instance of the `Metron` client.
 */
class Metron(
  username: String,
  password: String,
  val cache: SQLiteCache? = null,
  timeout: Duration = Duration.ofSeconds(30),
  var maxRetries: Int = 5,
) {
  private val client: HttpClient =
    HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).connectTimeout(timeout).build()
  private val authorization: String = "Basic " + Base64.getEncoder().encodeToString("$username:$password".toByteArray())

  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  private fun performGetRequest(uri: URI): String {
    var attempt = 0

    while (attempt < this.maxRetries) {
      try {
        val request =
          HttpRequest.newBuilder()
            .uri(uri)
            .setHeader("Accept", "application/json")
            .setHeader(
              "User-Agent",
              "Kraken/$VERSION (${System.getProperty("os.name")}/${System.getProperty("os.version")}; Kotlin/${KotlinVersion.CURRENT})",
            )
            .setHeader("Authorization", this.authorization)
            .GET()
            .build()
        val response = this.client.send(request, HttpResponse.BodyHandlers.ofString())
        val level =
          when (response.statusCode()) {
            in 100 until 200 -> Level.WARN
            in 200 until 300 -> Level.DEBUG
            in 300 until 400 -> Level.INFO
            in 400 until 500 -> Level.WARN
            else -> Level.ERROR
          }
        LOGGER.log(level) { "GET: ${response.statusCode()} - $uri" }
        if (response.statusCode() == 200) {
          return response.body()
        } else if (response.statusCode() == 429) {
          val backoffDelay = response.headers().firstValue("Retry-After").getOrNull()?.toLongOrNull() ?: 2
          LOGGER.warn { "Received 429 Too Many Requests. Retrying in ${backoffDelay}s..." }
          Thread.sleep(backoffDelay * 1000)
          attempt++
          continue
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
    throw RateLimitException("Max retries reached for $uri")
  }

  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
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
    val response = performGetRequest(uri = uri)
    this.cache?.insert(url = uri.toString(), response = response)
    return try {
      JSON.decodeFromString(response)
    } catch (se: SerializationException) {
      throw ServiceException(cause = se)
    }
  }

  internal fun encodeURI(endpoint: String, params: Map<String, String> = emptyMap()): URI {
    val encodedParams =
      params.entries
        .sortedBy { it.key }
        .joinToString("&") { "${it.key}=${URLEncoder.encode(it.value, StandardCharsets.UTF_8)}" }
    return URI.create("$BASE_API$endpoint/${if (encodedParams.isNotEmpty()) "?$encodedParams" else ""}")
  }

  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  internal inline fun <reified T> fetchList(endpoint: String, params: Map<String, String>): List<T> {
    val resultList = mutableListOf<T>()
    var page = params.getOrDefault("page", "1").toInt()

    do {
      val uri = encodeURI(endpoint = endpoint, params = params + ("page" to page.toString()))
      val response = getRequest<ListResponse<T>>(uri = uri)
      resultList.addAll(response.results)
      page++
    } while (response.next != null)

    return resultList
  }

  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  internal inline fun <reified T> fetchItem(endpoint: String): T =
    getRequest<T>(uri = this.encodeURI(endpoint = endpoint))

  /**
   * Retrieves a list of Arcs from the Metron API.
   *
   * @param params A map of query parameters to filter the results.
   * @return A list of Arcs as [BaseResource] objects.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun listArcs(params: Map<String, String> = emptyMap()): List<BaseResource> {
    return fetchList<BaseResource>(endpoint = "/arc", params = params)
  }

  /**
   * Retrieves details of a specific Arc by its ID.
   *
   * @param id The unique identifier of the Arc to retrieve.
   * @return The Arc as an [Arc] object.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun getArc(id: Long): Arc = fetchItem<Arc>(endpoint = "/arc/$id")

  /**
   * Retrieves a list of Characters from the Metron API.
   *
   * @param params A map of query parameters to filter the results.
   * @return A list of Characters as [BaseResource] objects.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun listCharacters(params: Map<String, String> = emptyMap()): List<BaseResource> {
    return fetchList<BaseResource>(endpoint = "/character", params = params)
  }

  /**
   * Retrieves details of a specific Character by its ID.
   *
   * @param id The unique identifier of the Character to retrieve.
   * @return The Character as a [Character] object.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun getCharacter(id: Long): Character = fetchItem<Character>(endpoint = "/character/$id")

  /**
   * Retrieves a list of Creators from the Metron API.
   *
   * @param params A map of query parameters to filter the results.
   * @return A list of Creators as [BaseResource] objects.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun listCreators(params: Map<String, String> = emptyMap()): List<BaseResource> {
    return fetchList<BaseResource>(endpoint = "/creator", params = params)
  }

  /**
   * Retrieves details of a specific Creator by its ID.
   *
   * @param id The unique identifier of the Creator to retrieve.
   * @return The Creator as a [Creator] object.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun getCreator(id: Long): Creator = fetchItem<Creator>(endpoint = "/creator/$id")

  /**
   * Retrieves a list of Imprints from the Metron API.
   *
   * @param params A map of query parameters to filter the results.
   * @return A list of Imprints as [BaseResource] objects.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun listImprints(params: Map<String, String> = emptyMap()): List<BaseResource> {
    return fetchList<BaseResource>(endpoint = "/imprint", params = params)
  }

  /**
   * Retrieves details of a specific Imprint by its ID.
   *
   * @param id The unique identifier of the Imprint to retrieve.
   * @return The Imprint as an [Imprint] object.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun getImprint(id: Long): Imprint = fetchItem<Imprint>(endpoint = "/imprint/$id")

  /**
   * Retrieves a list of Issues from the Metron API.
   *
   * @param params A map of query parameters to filter the results.
   * @return A list of Issues as [BasicIssue] objects.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun listIssues(params: Map<String, String> = emptyMap()): List<BasicIssue> {
    return fetchList<BasicIssue>(endpoint = "/issue", params = params)
  }

  /**
   * Retrieves details of a specific Issue by its ID.
   *
   * @param id The unique identifier of the Issue to retrieve.
   * @return The Issue as an [Issue] object.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun getIssue(id: Long): Issue = fetchItem<Issue>(endpoint = "/issue/$id")

  /**
   * Retrieves a list of Publishers from the Metron API.
   *
   * @param params A map of query parameters to filter the results.
   * @return A list of Publishers as [BaseResource] objects.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun listPublishers(params: Map<String, String> = emptyMap()): List<BaseResource> {
    return fetchList<BaseResource>(endpoint = "/publisher", params = params)
  }

  /**
   * Retrieves details of a specific Publisher by its ID.
   *
   * @param id The unique identifier of the Publisher to retrieve.
   * @return The Publisher as a [Publisher] object.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun getPublisher(id: Long): Publisher = fetchItem<Publisher>(endpoint = "/publisher/$id")

  /**
   * Retrieves a list of Roles from the Metron API.
   *
   * @param params A map of query parameters to filter the results.
   * @return A list of Roles as [GenericItem] objects.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun listRoles(params: Map<String, String> = emptyMap()): List<GenericItem> {
    return fetchList<GenericItem>(endpoint = "/role", params = params)
  }

  /**
   * Retrieves a list of Series from the Metron API.
   *
   * @param params A map of query parameters to filter the results.
   * @return A list of Series as [BasicSeries] objects.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun listSeries(params: Map<String, String> = emptyMap()): List<BasicSeries> {
    return fetchList<BasicSeries>(endpoint = "/series", params = params)
  }

  /**
   * Retrieves details of a specific Series by its ID.
   *
   * @param id The unique identifier of the Series to retrieve.
   * @return The Series as a [Series] object.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun getSeries(id: Long): Series = fetchItem<Series>(endpoint = "/series/$id")

  /**
   * Retrieves a list of SeriesTypes from the Metron API.
   *
   * @param params A map of query parameters to filter the results.
   * @return A list of SeriesTypes as [GenericItem] objects.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun listSeriesTypes(params: Map<String, String> = emptyMap()): List<GenericItem> {
    return fetchList<GenericItem>(endpoint = "/series_type", params = params)
  }

  /**
   * Retrieves a list of Teams from the Metron API.
   *
   * @param params A map of query parameters to filter the results.
   * @return A list of Teams as [BaseResource] objects.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun listTeams(params: Map<String, String> = emptyMap()): List<BaseResource> {
    return fetchList<BaseResource>(endpoint = "/team", params = params)
  }

  /**
   * Retrieves details of a specific Team by its ID.
   *
   * @param id The unique identifier of the Team to retrieve.
   * @return The Team as a [Team] object.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun getTeam(id: Long): Team = fetchItem<Team>(endpoint = "/team/$id")

  /**
   * Retrieves a list of Universes from the Metron API.
   *
   * @param params A map of query parameters to filter the results.
   * @return A list of Universes as [BaseResource] objects.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun listUniverses(params: Map<String, String> = emptyMap()): List<BaseResource> {
    return fetchList<BaseResource>(endpoint = "/universe", params = params)
  }

  /**
   * Retrieves details of a specific Universe by its ID.
   *
   * @param id The unique identifier of the Universe to retrieve.
   * @return The Universe as an [Universe] object.
   * @throws ServiceException If a generic error occurs during the API call.
   * @throws AuthenticationException If the provided credentials are invalid.
   * @throws RateLimitException If the maximum number of retries is exceeded due to rate-limiting.
   */
  @Throws(ServiceException::class, AuthenticationException::class, RateLimitException::class)
  fun getUniverse(id: Long): Universe = fetchItem<Universe>(endpoint = "/universe/$id")

  companion object {
    @JvmStatic private val LOGGER = KotlinLogging.logger {}

    private const val BASE_API = "https://metron.cloud/api"

    @OptIn(ExperimentalSerializationApi::class)
    private val JSON: Json = Json {
      prettyPrint = true
      encodeDefaults = true
      namingStrategy = JsonNamingStrategy.SnakeCase
    }
  }
}
