package github.buriedincode.kraken.schemas

import github.buriedincode.kraken.Metron
import github.buriedincode.kraken.SQLiteCache
import github.buriedincode.kraken.ServiceException
import java.nio.file.Paths
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
class UniverseTest {
  private val session: Metron

  init {
    val username = System.getenv("METRON__USERNAME") ?: "IGNORED"
    val password = System.getenv("METRON__PASSWORD") ?: "IGNORED"
    val cache = SQLiteCache(path = Paths.get("cache.sqlite"), expiry = null)
    session = Metron(username = username, password = password, cache = cache)
  }

  @Nested
  inner class ListUniverses {
    @Test
    fun `Test ListUniverses with a valid search`() {
      val results = session.listUniverses(params = mapOf("name" to "Earth 2"))
      assertEquals(6, results.size)
      assertAll({ assertEquals(18, results[0].id) }, { assertEquals("Earth 2", results[0].name) })
    }

    @Test
    fun `Test ListUniverses with an invalid search`() {
      val results = session.listUniverses(params = mapOf("name" to "INVALID"))
      assertTrue(results.isEmpty())
    }
  }

  @Nested
  inner class GetUniverse {
    @Test
    fun `Test GetUniverse with a valid id`() {
      val result = session.getUniverse(id = 18)
      assertNotNull(result)
      assertAll(
        { assertEquals("Earth 2", result.designation) },
        { assertNull(result.grandComicsDatabaseId) },
        { assertEquals(18, result.id) },
        { assertEquals("https://static.metron.cloud/media/universe/2024/01/25/earth-2.webp", result.image) },
        { assertEquals("Earth 2", result.name) },
        { assertAll({ assertEquals(2, result.publisher.id) }, { assertEquals("DC Comics", result.publisher.name) }) },
        { assertEquals("https://metron.cloud/universe/earth-2/", result.resourceUrl) },
      )
    }

    @Test
    fun `Test GetUniverse with an invalid id`() {
      assertThrows(ServiceException::class.java) { session.getUniverse(id = -1) }
    }
  }
}
