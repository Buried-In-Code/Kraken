package github.buriedincode.kraken.schemas

import github.buriedincode.kraken.Metron
import github.buriedincode.kraken.SQLiteCache
import github.buriedincode.kraken.ServiceException
import java.nio.file.Paths
import kotlinx.datetime.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.assertAll

@TestInstance(Lifecycle.PER_CLASS)
class IssueTest {
  private val session: Metron

  init {
    val username = System.getenv("METRON__USERNAME") ?: "IGNORED"
    val password = System.getenv("METRON__PASSWORD") ?: "IGNORED"
    val cache = SQLiteCache(path = Paths.get("cache.sqlite"), expiry = null)
    session = Metron(username = username, password = password, cache = cache)
  }

  @Nested
  inner class ListIssues {
    @Test
    fun `Test ListIssues with a valid search`() {
      val results = session.listIssues(params = mapOf("series_id" to 119.toString(), "number" to "1"))
      assertEquals(1, results.size)
      assertAll(
        { assertEquals(LocalDate(1991, 7, 1), results[0].coverDate) },
        { assertEquals("87386cc738ac7b38", results[0].coverHash) },
        { assertEquals(1088, results[0].id) },
        { assertEquals("https://static.metron.cloud/media/issue/2019/01/21/bone-1.jpg", results[0].image) },
        { assertEquals("Bone (1991) #1", results[0].title) },
        { assertEquals("1", results[0].number) },
        {
          assertAll(
            { assertEquals("Bone", results[0].series.name) },
            { assertEquals(1, results[0].series.volume) },
            { assertEquals(1991, results[0].series.yearBegan) },
          )
        },
        { assertNull(results[0].storeDate) },
      )
    }

    @Test
    fun `Test ListIssues with an invalid search`() {
      val results = session.listIssues(params = mapOf("series_id" to 119.toString(), "number" to "INVALID"))
      assertTrue(results.isEmpty())
    }
  }

  @Nested
  inner class GetIssue {
    @Test
    fun `Test GetIssue with a valid id`() {
      val result = session.getIssue(id = 1088)
      assertNotNull(result)
      assertAll(
        { assertNull(result.alternativeNumber) },
        { assertTrue(result.arcs.isEmpty()) },
        {
          assertAll(
            { assertEquals(1232, result.characters[0].id) },
            { assertEquals("Fone Bone", result.characters[0].name) },
          )
        },
        { assertEquals(34352, result.comicvineId) },
        { assertEquals(LocalDate(1991, 7, 1), result.coverDate) },
        { assertEquals("87386cc738ac7b38", result.coverHash) },
        {
          assertAll(
            { assertEquals("Jeff Smith", result.credits[0].creator) },
            { assertEquals(573, result.credits[0].id) },
            {
              assertAll(
                { assertEquals(1, result.credits[0].roles[0].id) },
                { assertEquals("Writer", result.credits[0].roles[0].name) },
              )
            },
          )
        },
        { assertNull(result.focDate) },
        { assertNull(result.grandComicsDatabaseId) },
        { assertEquals(1088, result.id) },
        { assertEquals("https://static.metron.cloud/media/issue/2019/01/21/bone-1.jpg", result.image) },
        { assertNull(result.imprint) },
        { assertNull(result.isbn) },
        { assertEquals("1", result.number) },
        { assertEquals(28, result.pageCount) },
        { assertEquals(2.95, result.price) },
        {
          assertAll({ assertEquals(19, result.publisher.id) }, { assertEquals("Cartoon Books", result.publisher.name) })
        },
        { assertAll({ assertEquals(1, result.rating.id) }, { assertEquals("Unknown", result.rating.name) }) },
        {
          assertAll(
            { assertEquals(113595, result.reprints[0].id) },
            { assertEquals("Bone TPB (2004) #1", result.reprints[0].issue) },
          )
        },
        { assertEquals("https://metron.cloud/issue/bone-1991-1/", result.resourceUrl) },
        {
          assertAll(
            { assertTrue(result.series.genres.isEmpty()) },
            { assertEquals(119, result.series.id) },
            { assertEquals("Bone", result.series.name) },
            {
              assertAll(
                { assertEquals(13, result.series.seriesType.id) },
                { assertEquals("Single Issue", result.series.seriesType.name) },
              )
            },
            { assertEquals("Bone", result.series.sortName) },
            { assertEquals(1, result.series.volume) },
            { assertEquals(1991, result.series.yearBegan) },
          )
        },
        { assertNull(result.sku) },
        { assertNull(result.storeDate) },
        { assertEquals("The Map", result.stories[0]) },
        {
          assertAll({ assertEquals(1473, result.teams[0].id) }, { assertEquals("Rat Creatures", result.teams[0].name) })
        },
        { assertNull(result.title) },
        { assertTrue(result.universes.isEmpty()) },
        { assertNull(result.upc) },
        { assertTrue(result.variants.isEmpty()) },
      )
    }

    @Test
    fun `Test GetIssue with an invalid id`() {
      assertThrows(ServiceException::class.java) { session.getIssue(id = -1) }
    }
  }
}
