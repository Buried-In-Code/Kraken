package github.buriedincode.kalibak.schemas

import github.buriedincode.kalibak.Metron
import github.buriedincode.kalibak.SQLiteCache
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.junit.jupiter.api.assertAll
import java.nio.file.Paths

@TestInstance(Lifecycle.PER_CLASS)
class RoleTest {
    private val session: Metron

    init {
        val username = System.getenv("METRON__USERNAME") ?: "IGNORED"
        val password = System.getenv("METRON__PASSWORD") ?: "IGNORED"
        val cache = SQLiteCache(path = Paths.get("cache.sqlite"), expiry = null)
        session = Metron(username = username, password = password, cache = cache)
    }

    @Nested
    inner class ListRoles {
        @Test
        fun `Test ListRoles with a valid search`() {
            val results = session.listRoles(params = mapOf("name" to "Writer"))
            assertEquals(1, results.size)
            assertAll(
                { assertEquals(1, results[0].id) },
                { assertEquals("Writer", results[0].name) },
            )
        }

        @Test
        fun `Test ListRoles with an invalid search`() {
            val results = session.listRoles(params = mapOf("name" to "INVALID"))
            assertTrue(results.isEmpty())
        }
    }
}
