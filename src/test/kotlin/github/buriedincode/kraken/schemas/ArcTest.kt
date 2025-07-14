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
class ArcTest {
  private val session: Metron

  init {
    val username = System.getenv("METRON__USERNAME") ?: "IGNORED"
    val password = System.getenv("METRON__PASSWORD") ?: "IGNORED"
    val cache = SQLiteCache(path = Paths.get("cache.sqlite"), expiry = null)
    session = Metron(username = username, password = password, cache = cache)
  }

  @Nested
  inner class ListArcs {
    @Test
    fun `Test ListArcs with a valid search`() {
      val results = session.listArcs(params = mapOf("name" to "Cow Race"))
      assertEquals(1, results.size)
      assertAll({ assertEquals(1491, results[0].id) }, { assertEquals("The Great Cow Race", results[0].name) })
    }

    @Test
    fun `Test ListArcs with an invalid search`() {
      val results = session.listArcs(params = mapOf("name" to "INVALID"))
      assertTrue(results.isEmpty())
    }
  }

  @Nested
  inner class GetArc {
    @Test
    fun `Test GetArc with a valid id`() {
      val result = session.getArc(id = 1491)
      assertNotNull(result)
      assertAll(
        { assertEquals(41751, result.comicvineId) },
        { assertNull(result.grandComicsDatabaseId) },
        { assertEquals(1491, result.id) },
        {
          assertEquals(
            "https://static.metron.cloud/media/arc/2024/03/07/d75aba2ca26349c89c3104690d32cc2f.jpg",
            result.image,
          )
        },
        { assertEquals("The Great Cow Race", result.name) },
        { assertEquals("https://metron.cloud/arc/bone-the-great-cow-race/", result.resourceUrl) },
      )
    }

    @Test
    fun `Test GetArc with an invalid id`() {
      assertThrows(ServiceException::class.java) { session.getArc(id = -1) }
    }
  }
}
