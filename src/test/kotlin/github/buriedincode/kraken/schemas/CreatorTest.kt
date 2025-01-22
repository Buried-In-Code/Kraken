package github.buriedincode.kraken.schemas

import github.buriedincode.kraken.Metron
import github.buriedincode.kraken.SQLiteCache
import github.buriedincode.kraken.ServiceException
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
class CreatorTest {
    private val session: Metron

    init {
        val username = System.getenv("METRON__USERNAME") ?: "IGNORED"
        val password = System.getenv("METRON__PASSWORD") ?: "IGNORED"
        val cache = SQLiteCache(path = Paths.get("cache.sqlite"), expiry = null)
        session = Metron(username = username, password = password, cache = cache)
    }

    @Nested
    inner class ListCreators {
        @Test
        fun `Test ListCreators with a valid search`() {
            val results = session.listCreators(params = mapOf("name" to "Jeff Smith"))
            assertEquals(1, results.size)
            assertAll(
                { assertEquals(573, results[0].id) },
                { assertEquals("Jeff Smith", results[0].name) },
            )
        }

        @Test
        fun `Test ListCreators with an invalid search`() {
            val results = session.listCreators(params = mapOf("name" to "INVALID"))
            assertTrue(results.isEmpty())
        }
    }

    @Nested
    inner class GetCreator {
        @Test
        fun `Test GetCreator with a valid id`() {
            val result = session.getCreator(id = 573)
            assertNotNull(result)
            assertAll(
                { assertTrue(result.alias.isEmpty()) },
                { assertEquals(LocalDate(1960, 2, 27), result.birth) },
                { assertEquals(23088, result.comicvineId) },
                { assertNull(result.death) },
                { assertNull(result.grandComicsDatabaseId) },
                { assertEquals(573, result.id) },
                { assertEquals("https://static.metron.cloud/media/creator/2018/12/06/jeff_smith.jpg", result.image) },
                { assertEquals("Jeff Smith", result.name) },
                { assertEquals("https://metron.cloud/creator/jeff-smith/", result.resourceUrl) },
            )
        }

        @Test
        fun `Test GetCreator with an invalid id`() {
            assertThrows(ServiceException::class.java) {
                session.getCreator(id = -1)
            }
        }
    }
}
