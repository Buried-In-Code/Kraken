package github.buriedincode.kalibak.schemas

import github.buriedincode.kalibak.Metron
import github.buriedincode.kalibak.SQLiteCache
import github.buriedincode.kalibak.ServiceException
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
import java.nio.file.Paths

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
                { assertEquals("Bone (1991) #1", results[0].name) },
                { assertEquals("1", results[0].number) },
                { assertEquals("Bone", results[0].series.name) },
                { assertEquals(1, results[0].series.volume) },
                { assertEquals(1991, results[0].series.yearBegan) },
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
                { assertTrue(result.arcs.isEmpty()) },
                { assertEquals(1232, result.characters[0].id) },
                { assertEquals(34352, result.comicvineId) },
                { assertEquals(LocalDate(1991, 7, 1), result.coverDate) },
                { assertEquals("87386cc738ac7b38", result.coverHash) },
                { assertEquals(573, result.credits[0].id) },
                { assertNull(result.grandComicsDatabaseId) },
                { assertEquals(1088, result.id) },
                { assertEquals("https://static.metron.cloud/media/issue/2019/01/21/bone-1.jpg", result.image) },
                { assertNull(result.isbn) },
                { assertEquals("1", result.number) },
                { assertEquals(28, result.pageCount) },
                { assertEquals(2.95, result.price) },
                { assertEquals(19, result.publisher.id) },
                { assertEquals("Cartoon Books", result.publisher.name) },
                { assertEquals(1, result.rating.id) },
                { assertEquals("Unknown", result.rating.name) },
                { assertEquals(113595, result.reprints[0].id) },
                { assertEquals("https://metron.cloud/issue/bone-1991-1/", result.resourceUrl) },
                { assertTrue(result.series.genres.isEmpty()) },
                { assertEquals(119, result.series.id) },
                { assertEquals("Bone", result.series.name) },
                { assertEquals(13, result.series.seriesType.id) },
                { assertEquals("Single Issue", result.series.seriesType.name) },
                { assertEquals("Bone", result.series.sortName) },
                { assertEquals(1, result.series.volume) },
                { assertNull(result.sku) },
                { assertNull(result.storeDate) },
                { assertEquals("The Map", result.stories[0]) },
                { assertEquals(1473, result.teams[0].id) },
                { assertNull(result.title) },
                { assertTrue(result.universes.isEmpty()) },
                { assertNull(result.upc) },
                { assertTrue(result.variants.isEmpty()) },
            )
        }

        @Test
        fun `Test GetIssue with an invalid id`() {
            assertThrows(ServiceException::class.java) {
                session.getIssue(id = -1)
            }
        }
    }
}
