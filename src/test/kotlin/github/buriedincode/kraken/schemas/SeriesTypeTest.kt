package github.buriedincode.kraken.schemas

import github.buriedincode.kraken.Metron
import github.buriedincode.kraken.SQLiteCache
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.assertAll
import java.nio.file.Paths

@TestInstance(Lifecycle.PER_CLASS)
class SeriesTypeTest {
    private val session: Metron

    init {
        val username = System.getenv("METRON__USERNAME") ?: "IGNORED"
        val password = System.getenv("METRON__PASSWORD") ?: "IGNORED"
        val cache = SQLiteCache(path = Paths.get("cache.sqlite"), expiry = null)
        session = Metron(username = username, password = password, cache = cache)
    }

    @Nested
    inner class ListSeriesTypes {
        @Test
        fun `Test ListSeriesTypes with a valid search`() {
            val results = session.listSeriesTypes(params = mapOf("name" to "Single Issue"))
            assertEquals(1, results.size)
            assertAll(
                { assertEquals(13, results[0].id) },
                { assertEquals("Single Issue", results[0].name) },
            )
        }

        @Test
        fun `Test ListSeriesTypes with an invalid search`() {
            val results = session.listSeriesTypes(params = mapOf("name" to "INVALID"))
            assertTrue(results.isEmpty())
        }
    }
}
