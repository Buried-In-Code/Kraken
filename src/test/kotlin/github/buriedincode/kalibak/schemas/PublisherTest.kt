package github.buriedincode.kalibak.schemas

import github.buriedincode.kalibak.Metron
import github.buriedincode.kalibak.SQLiteCache
import github.buriedincode.kalibak.ServiceException
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
class PublisherTest {
    private val session: Metron

    init {
        val username = System.getenv("METRON__USERNAME") ?: "IGNORED"
        val password = System.getenv("METRON__PASSWORD") ?: "IGNORED"
        val cache = SQLiteCache(path = Paths.get("cache.sqlite"), expiry = null)
        session = Metron(username = username, password = password, cache = cache)
    }

    @Nested
    inner class ListPublishers {
        @Test
        fun `Test ListPublishers with a valid search`() {
            val results = session.listPublishers(params = mapOf("name" to "Cartoon Books"))
            assertEquals(1, results.size)
            assertAll(
                { assertEquals(19, results[0].id) },
                { assertEquals("Cartoon Books", results[0].name) },
            )
        }

        @Test
        fun `Test ListPublishers with an invalid search`() {
            val results = session.listPublishers(params = mapOf("name" to "INVALID"))
            assertTrue(results.isEmpty())
        }
    }

    @Nested
    inner class GetPublisher {
        @Test
        fun `Test GetPublisher with a valid id`() {
            val result = session.getPublisher(id = 19)
            assertNotNull(result)
            assertAll(
                { assertEquals(490, result.comicvineId) },
                { assertEquals(1991, result.founded) },
                { assertNull(result.grandComicsDatabaseId) },
                { assertEquals(19, result.id) },
                { assertEquals("https://static.metron.cloud/media/publisher/2019/01/21/cartoon-books.jpg", result.image) },
                { assertEquals("Cartoon Books", result.name) },
                { assertEquals("https://metron.cloud/publisher/cartoon-books/", result.resourceUrl) },
            )
        }

        @Test
        fun `Test GetPublisher with an invalid id`() {
            assertThrows(ServiceException::class.java) {
                session.getPublisher(id = -1)
            }
        }
    }
}
